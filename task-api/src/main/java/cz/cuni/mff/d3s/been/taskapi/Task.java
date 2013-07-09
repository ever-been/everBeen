package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.socketworks.NamedSockets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.TaskMessageType;
import cz.cuni.mff.d3s.been.core.TaskPropertyNames;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * 
 * @author Kuba Břečka
 */
public abstract class Task {

	private static final Logger log = LoggerFactory.getLogger(Task.class);

	private String id;
	private String taskContextId;
	private String benchmarkId;
	protected final ResultFacade results = ResultFacadeFactory.getResultFacade();

	/**
	 * Returns ID of the running task.
	 * 
	 * @return ID of the running task
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns context ID the running task is associated with.
	 * 
	 * @return context ID associated with the running task
	 */
	public String getTaskContextId() {
		return taskContextId;
	}
    
	/**
	 * Returns benchmark ID of the running task.
	 *
	 * @return benchmark ID of the running task
	 */
	public String getBenchmarkId() {
		return benchmarkId;
	}

	/**
	 * Returns system property associated with the running task.
	 *
	 * @param propertyName
	 *          name of the property
	 *
	 * @return value associated with the name
	 */
	public String getProperty(String propertyName) {
		return System.getenv(propertyName);
	}

	/**
	 * Returns system property associated with the running task or default value
	 *
	 * @param propertyName
	 *          name of the property
	 *
	 * @return value associated with the name or the default value when the
	 *         property is not set
	 */
	public String getProperty(String propertyName, String defaultValue) {
		String propertyValue = System.getenv(propertyName);
		if (propertyValue == null) {
			return defaultValue;
		} else {
			return propertyValue;
		}
	}

	/**
	 * The method subclasses override to implement task's functionality.
	 *
	 * To execute a task {@link #doMain(String[])} will be called.
	 */
	public abstract void run(String[] args);

	/**
	 *
	 * The method which sets up task's environment and calls
	 * {@link #run(String[])}.
	 *
	 * @param args
	 */
	public void doMain(String[] args) {
		try {
			initialize();
			run(args);
		} finally {
			tearDown();
		}
	}

	private void initialize() {
		this.id = System.getenv(TaskPropertyNames.TASK_ID);
		this.taskContextId = System.getenv(TaskPropertyNames.TASK_CONTEXT_ID);
        this.benchmarkId = System.getenv(TaskPropertyNames.BENCHMARK_ID);

        System.out.println(String.format("result queue url: %s", NamedSockets.TASK_RESULT_0MQ.getConnection()));

		try {
			Messages.send(String.format("%s#%s", TaskMessageType.TASK_RUNNING, id));
		} catch (MessagingException e) {
			// message passing does not work, try it with stderr ...
			System.err.println("Cannot send \"i'm running\" message");
		}
	}

	private void tearDown() {
		try {
			ResultFacadeFactory.quit();
		} catch (MessagingException e) {
			log.error("Failed to release results facade.");
		}

		try {
			Messages.terminate();
		} catch (MessagingException e) {
			// TODO consider System.err instead, since this probably means that the log pipe is broken 
			log.error("Failed to release Messaging");
		}
	}
}
