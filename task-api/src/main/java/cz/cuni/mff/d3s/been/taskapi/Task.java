package cz.cuni.mff.d3s.been.taskapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.TaskMessageType;
import cz.cuni.mff.d3s.been.core.TaskPropertyNames;
import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.Messaging;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.taskapi.results.ResultFacade;
import cz.cuni.mff.d3s.been.taskapi.results.ResultFacadeFactory;

/**
 * 
 * @author Kuba Břečka
 */
public abstract class Task {

	private static final Logger log = LoggerFactory.getLogger(Task.class);

	private String id;
	private String taskContextId;
	private IMessageQueue<String> resQueue;
	private IMessageSender<String> resSender;
	protected final ResultFacade results = new TaskFieldResultFacadeWrapper();
	private final String hostname = System.getenv(TaskPropertyNames.HR_HOSTNAME);

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
	 * Returns host name of the Host Runtime under which the task is running.
	 * 
	 * @return host name of the associated Host Runtime
	 */
	public String getHostName() {
		return hostname;
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
		final String resultPort = System.getenv(TaskPropertyNames.HR_RESULTS_PORT);
		resQueue = Messaging.createTaskQueue(Integer.valueOf(resultPort));
		try {
			resSender = resQueue.createSender();
			((TaskFieldResultFacadeWrapper) results).setResultFacade(ResultFacadeFactory.createResultFacade(resSender));
		} catch (MessagingException e) {
			log.error("Failed to create Task environment due to messaging system initialization error - {}", e.getMessage());
			log.debug("Reasons for Task setup failing:", e);
			// TODO exit with code
		}

		try {
			Messages.send(String.format("%s#%s", TaskMessageType.TASK_RUNNING, id));
		} catch (MessagingException e) {
			// message passing does not work, try it with stderr ...
			System.err.println("Cannot send 'i'm running' message");
		}
	}
	private void tearDown() {
		if (resSender != null) {
			resSender.close();
		}

		if (resQueue != null) {
			resQueue.terminate();
		}

		Messages.terminate();
	}

}
