package cz.cuni.mff.d3s.been.benchmarkapi;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistoryItem;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.taskapi.Task;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * This class is an abstract base class for benchmarks. To implement a benchmark
 * (which can generate task contexts) extend this class an implement the
 * abstract methods. The implementation then serves as a generator task for the
 * benchmark.
 * 
 * Benchmark generator tasks are subclasses of {@link Task}, which means they
 * are submitted, scheduled and run the same way as any other task. However, the
 * generator task can (and/or are supposed) to run for a very long time, so they
 * support "resubmits". When the generator task fails for any reason, it will
 * get resubmit automatically. For correct behavior the generator should store
 * its internal state only using the {@link #storageSet} method, which will
 * persist the state in the cluster and retrieve it before resubmitting.
 * 
 * When extending this class to implement a benchmark, you have to implement the
 * methods {@link #generateTaskContext}, {@link #onResubmit} and
 * {@link #onTaskContextFinished}. The later two are only "notify" methods and
 * can be implemented as no-operations. The first method (
 * {@link #generateTaskContext}), however, serves as the main method that
 * "generates" all task contexts in the benchmark. This method will be called by
 * this class and is supposed to return a newly created context every time when
 * the benchmark can generate any. The method can block/wait when it does not
 * yet have any context to generate. When the method returns null, the benchmark
 * is ended.
 * 
 * @author Kuba Brecka
 */
public abstract class Benchmark extends Task {

	/** Logging */
	private static final Logger log = LoggerFactory.getLogger(Benchmark.class);

	/** Local storage which is synced to been */
	private Map<String, String> storage;

	/** requestor that communicates with the host runtime for benchmark requests */
	private BenchmarkRequestor benchmarkRequestor;

	/**
	 * Method which drives the benchmark by generating contexts. The context is
	 * submitted and run by BEEN. Subclasses are supposed to implement this
	 * method, which will be called automatically by this class. The
	 * implementation can wait/block when it doesn't have any content to run.
	 * 
	 * To indicate end of the benchmark null must be returned.
	 * 
	 * @return TaskContextDescriptor to be submitted, or null to indicate end of
	 *         the benchmark
	 * @throws BenchmarkException
	 *           when an error has occurred in the generator task
	 */
	public abstract TaskContextDescriptor generateTaskContext() throws BenchmarkException;

	/**
	 * Event handler that notifies the generator task that it has been resubmitted
	 * after an error.
	 */
	public abstract void onResubmit();

	/**
	 * Event handler that notifies the generator task that a context with the
	 * specified ID has finished with the specified state.
	 * 
	 * @param taskContextId
	 *          ID of the context that has finished
	 * @param state
	 *          state with which the context has finished
	 */
	public abstract void onTaskContextFinished(String taskContextId, TaskContextState state);

	/**
	 * Retrieves a value for the given key from benchmark-wide storage. If no
	 * value is found, null is returned instead.
	 * 
	 * The values are preserved among runs of the generator for the same
	 * benchmark.
	 * 
	 * @param key
	 *          identification of the value
	 * @return benchmark-wide value for the given key if it exists, null otherwise
	 */
	protected String storageGet(String key) {
		return storage.get(key);
	}

	/**
	 * Retrieves a value for the given key from benchmark wide storage. If no
	 * value is found, defaultValue is returned instead.
	 * 
	 * The values are preserved among runs of the generator for the same
	 * benchmark.
	 * 
	 * @param key
	 *          identification of the value
	 * @param defaultValue
	 *          value returned if no benchmark-value is found
	 * @return benchmark-wide value for the given key if it exists, defaultValue
	 *         otherwise
	 */
	protected String storageGet(String key, String defaultValue) {
		return storage.get(key) != null ? storage.get(key) : defaultValue;
	}

	/**
	 * Stores a value with the given key to Benchmark-wide storage.
	 * 
	 * The values are preserved among runs of the generator for the same benchmark
	 * and can be retrieved with {@link Benchmark#storageGet(String)} or
	 * {@link Benchmark#storageGet(String, String)}
	 * 
	 * @param key
	 *          identification of the value
	 * @param value
	 *          value to store to the benchmark-wide storage
	 */
	protected void storageSet(String key, String value) {
		storage.put(key, value);
	}

	@Override
	public void run(String[] args) throws BenchmarkException, MessagingException {
		benchmarkRequestor = BenchmarkRequestor.create();

		try {
			processContexts();
		} catch (TimeoutException | JsonException e) {
			throw new BenchmarkException("Benchmark has encountered an internal exception.", e);
		} finally {
			benchmarkRequestor.close();
		}
	}

	/**
	 * Checks whether this generator task is the original one or whether it is
	 * resubmitted.
	 * 
	 * @return true if this generator is resubmitted, false otherwise
	 */
	private boolean isResubmittedBenchmark() {
		try {
			Collection<ResubmitHistoryItem> historyItems;
			historyItems = benchmarkRequestor.resubmitHistoryRetrieve(getBenchmarkId());

			return historyItems.size() > 0;
		} catch (TimeoutException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the contexts ID of the currently running task context within this
	 * benchmark. If there is no running task context, returns null. If there is
	 * more than one running task contexts, this method throws an exception,
	 * because this is an invalid state.
	 * 
	 * @return ID of the currently running context or null if there is no such
	 *         context
	 * @throws TimeoutException
	 *           when the request for listing contexts fails
	 * @throws JsonException
	 *           when the request for listing contexts returns an unparsable
	 *           response
	 */
	private String benchmarkRunningContext() throws TimeoutException, JsonException {
		TaskContextStateInfo info = benchmarkRequestor.containedContextsRetrieve(getBenchmarkId());
		int numberOfRunningContexts = 0;
		String contextId = null;
		for (TaskContextStateInfo.Item item : info.items) {
			if (item.state == TaskContextState.RUNNING) {
				contextId = item.taskContextId;
				numberOfRunningContexts++;
			}
		}

		if (numberOfRunningContexts > 1) {
			throw new RuntimeException(String.format("The benchmark %s has more than one running context!", getBenchmarkId()));
		}

		return contextId;
	}

	/**
	 * The main generator task loop. This method first checks if it is being
	 * resubmitted and performs additional checks and notifications when it is
	 * resubmitted. Then it enters a loop that asks the subclass to provide a new
	 * task context, submits it and waits for it to finish. The loop is ended when
	 * the {@link #generateTaskContext} method returns null.
	 * 
	 * The extra handling for resubmitted generators means that it first waits for
	 * a (possibly) already running task context before entering the loop.
	 * 
	 * @throws TimeoutException
	 *           when the host runtime requestor times out
	 * @throws JsonException
	 *           when the requestor returns an unparsable response
	 */
	private void processContexts() throws TimeoutException, JsonException {
		try {
			this.storage = benchmarkRequestor.storageRetrieve(getBenchmarkId());
		} catch (TimeoutException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		if (isResubmittedBenchmark()) {
			this.onResubmit();

			String taskContextId = benchmarkRunningContext();
			if (taskContextId != null) {
				try {
					TaskContextState state = benchmarkRequestor.contextWait(taskContextId);
					log.trace("Task context '{}' finished with state {}.", taskContextId, state);
					this.onTaskContextFinished(taskContextId, state);
				} catch (TimeoutException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
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
				// First save the local storage, before submitting the context
				benchmarkRequestor.storagePersist(getBenchmarkId(), storage);

				log.trace("Submitting task context descriptor.");
				String taskContextId = benchmarkRequestor.contextSubmit(taskContextDescriptor, getBenchmarkId());
				log.trace("Task context descriptor with ID '{}' submitted", taskContextId);

				TaskContextState state = benchmarkRequestor.contextWait(taskContextId);
				log.trace("Task context '{}' finished with state {}.", taskContextId, state);

				this.onTaskContextFinished(taskContextId, state);
			} catch (TimeoutException e) {
				throw new RuntimeException(e.getMessage(), e);
				// TODO this must be handled with greater care!
			}
		}
	}

	/**
	 * Creates a task context descriptor from a XML descriptor file contained as a
	 * resource for the current class.
	 * 
	 * This method is deprecated, use {@link ContextBuilder} instead.
	 * 
	 * @param resourceName
	 *          the resource name of the XML descriptor
	 * @return a newly create task context
	 * @throws BenchmarkException
	 *           when the descriptor cannot be created
	 */
	@Deprecated
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

	/**
	 * Sets a property inside the task context descriptor with the specified key
	 * to the specified value.
	 * 
	 * This method is deprecated, use {@link ContextBuilder} instead.
	 * 
	 * @param descriptor
	 *          the task descriptor to modify
	 * @param key
	 *          the key of the property to set
	 * @param value
	 *          the value of the property to set
	 */
	@Deprecated
	public void setTaskContextProperty(TaskContextDescriptor descriptor, String key, String value) {
		Property p = new Property();
		p.setName(key);
		p.setValue(value);
		if (!descriptor.isSetProperties())
			descriptor.setProperties(new Properties());
		descriptor.getProperties().getProperty().add(p);
	}

}
