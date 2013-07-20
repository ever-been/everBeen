package cz.cuni.mff.d3s.been.benchmarkapi;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.Properties;
import cz.cuni.mff.d3s.been.core.task.Property;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.taskapi.Task;

/**
 * @author Kuba Brecka
 */
public abstract class Benchmark extends Task {

	private static final Logger log = LoggerFactory.getLogger(Benchmark.class);

	public abstract TaskContextDescriptor generateTaskContext() throws BenchmarkException;

	private Map<String, String> storage;
	private BenchmarkRequestor benchmarkRequestor;

	protected String storageGet(String key) {
		return storage.get(key);
	}

	protected String storageGet(String key, String defaultValue) {
		return storage.get(key) != null ? storage.get(key) : defaultValue;
	}

	protected void storageSet(String key, String value) {
		storage.put(key, value);
	}

	@Override
	public void run(String[] args) {
		try {
			benchmarkRequestor = BenchmarkRequestor.create();
		} catch (MessagingException e) {
			log.error("Could not initialize checkpoint requestor", e);
		}
		try {
			processContexts();
		} finally {
			try {
				benchmarkRequestor.close();
			} catch (MessagingException e) {
				log.error("Could not close checkpoint requestor", e);
			}
		}
	}

	private void processContexts() {
		try {
			this.storage = benchmarkRequestor.storageRetrieve(this.getBenchmarkId());
		} catch (TimeoutException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		while (true) {
			TaskContextDescriptor taskContextDescriptor = null;
			try {
				taskContextDescriptor = generateTaskContext();

				if (taskContextDescriptor == null) {
					return;
				}
			} catch (BenchmarkException e) {
				throw new RuntimeException("Cannot generate task context.", e);
			}

			try {
				log.info("Submitting task context descriptor.");
				String taskContextId = benchmarkRequestor.contextSubmit(taskContextDescriptor, this.getBenchmarkId());
				log.info("Task context descriptor with ID {}.", taskContextId);

				benchmarkRequestor.storagePersist(this.getBenchmarkId(), this.storage);

				benchmarkRequestor.contextWait(taskContextId);
				log.info("Task context finished.");
			} catch (TimeoutException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	public TaskContextDescriptor getTaskContextFromResource(String resourceName) throws BenchmarkException {

		try {
			InputStream inputStream = this.getClass().getResourceAsStream(resourceName);

			TaskContextDescriptor taskContextDescriptor = null;

			BindingParser<TaskContextDescriptor> bindingComposer = XSD.TASK_CONTEXT_DESCRIPTOR.createParser(TaskContextDescriptor.class);
			taskContextDescriptor = bindingComposer.parse(inputStream);

			return taskContextDescriptor;
		} catch (Exception e) {
			throw new BenchmarkException("Cannot read resource.", e);
		}

	}

	public void setTaskContextProperty(TaskContextDescriptor descriptor, String key, String value) {
		Property p = new Property();
		p.setName(key);
		p.setValue(value);
		if (!descriptor.isSetProperties())
			descriptor.setProperties(new Properties());
		descriptor.getProperties().getProperty().add(p);
	}
}
