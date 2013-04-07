package cz.cuni.mff.d3s.been.taskapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.TaskPropertyNames;
import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.Messaging;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.taskapi.results.ResultFacade;
import cz.cuni.mff.d3s.been.taskapi.results.ResultFacadeFactory;

/**
 * Created with IntelliJ IDEA. User: Kuba Date: 11.03.13 Time: 10:50 To change
 * this template use File | Settings | File Templates.
 */
public abstract class Task {

	private static final Logger log = LoggerFactory.getLogger(Task.class);

	private String id;
	private IMessageQueue<String> resQueue;
	protected final ResultFacade results = new TaskFieldResultFacadeWrapper();

	public String getId() {
		return id;
	}

	public abstract void run();

	private void initialize() {
		this.id = System.getenv(TaskPropertyNames.TASK_ID);
		final String resultPort = System.getenv(TaskPropertyNames.HR_RESULTS_PORT);
		resQueue = Messaging.createTaskQueue(Integer.valueOf(resultPort));
		try {
			((TaskFieldResultFacadeWrapper) results).setResultFacade(ResultFacadeFactory.createResultFacade(resQueue.createSender()));
		} catch (MessagingException e) {
			log.error(
					"Failed to create Task environment due to messaging system initialization error - {}",
					e.getMessage());
			log.debug("Reasons for Task setup failing:", e);
			// TODO exit with code
		}
		// TODO notify HostManager that the task is no longer suspended
		// Messages.send("TASK_RUNNING#");
	}
	private void tearDown() {
		resQueue.terminate();
	}

	public void doMain(String[] args) {
		initialize();
		run();
		tearDown();
	}
}
