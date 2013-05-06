package cz.cuni.mff.d3s.been.benchmarkapi;

import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.taskapi.Requestor;
import cz.cuni.mff.d3s.been.taskapi.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

/**
 * @author Kuba Brecka
 */
public abstract class Benchmark extends Task {

	private static final Logger log = LoggerFactory.getLogger(Benchmark.class);

	public abstract TaskContextDescriptor generateTaskContext() throws BenchmarkException;

	private BenchmarkRequestor benchmarkRequestor;

	@Override
	public void run(String[] args) {
		benchmarkRequestor = new BenchmarkRequestor();
		try {
			processContexts();
		} finally {
			benchmarkRequestor.close();
		}
	}

	private void processContexts() {
		TaskContextDescriptor taskContextDescriptor = null;
		while (true) {
			try {
				taskContextDescriptor = generateTaskContext();

				if (taskContextDescriptor == null) {
					return;
				}
			} catch (BenchmarkException e) {
				log.error("Cannot generate task context.", e);
				// TODO System.exit ?
				return;
			}

			try {
				log.info("Submitting task context descriptor.");
				String taskContextId = benchmarkRequestor.contextSubmit(taskContextDescriptor);
				log.info("Task context descriptor with ID {}, waiting for finished.", taskContextId);
				benchmarkRequestor.contextWait(taskContextId);
				log.info("Task context finished.");
			} catch (TimeoutException e) {
				log.error("Requestor timed out.", e);
				// TODO System.exit ?
				return;
			}
		}
	}

	public TaskContextDescriptor getTaskContextFromResource(String resourceName) throws BenchmarkException {
		InputStream inputStream = this.getClass().getResourceAsStream(resourceName);

		TaskContextDescriptor taskContextDescriptor = null;
		try {
			BindingParser<TaskContextDescriptor> bindingComposer = XSD.TASK_CONTEXT_DESCRIPTOR.createParser(TaskContextDescriptor.class);
			taskContextDescriptor = bindingComposer.parse(inputStream);
		} catch (JAXBException | SAXException | ConvertorException e) {
			throw new BenchmarkException("Cannot read resource.", e);
		}

		return taskContextDescriptor;
	}
}
