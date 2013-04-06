package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.core.TaskPropertyNames;
import cz.cuni.mff.d3s.been.taskapi.mq.Messaging;
import cz.cuni.mff.d3s.been.taskapi.mq.MessagingSystem;
import cz.cuni.mff.d3s.been.taskapi.results.ResultFacade;
import cz.cuni.mff.d3s.been.taskapi.results.ResultFacadeFactory;

/**
 * Created with IntelliJ IDEA. User: Kuba Date: 11.03.13 Time: 10:50 To change
 * this template use File | Settings | File Templates.
 */
public abstract class Task {

	private String id;
	private Messaging resultMarshalling;
	private ResultFacade results;

	public String getId() {
		return id;
	}

	public abstract void run();

	private void initialize() {
		this.id = System.getProperty(TaskPropertyNames.TASK_ID);
		MessagingSystem.connect();
		this.resultMarshalling = MessagingSystem.getMessaging();
		this.results = ResultFacadeFactory.createResultFacade(resultMarshalling);
		// TODO notify HostManager that the task is no longer suspended
	}

	private void tearDown() {
		MessagingSystem.disconnect();
	}

	public void doMain(String[] args) {
		initialize();

		System.out.println("Task is started");
		run();
		System.out.println("Task is finished");

		tearDown();
	}

	protected ResultFacade results() {
		return results;
	}
}
