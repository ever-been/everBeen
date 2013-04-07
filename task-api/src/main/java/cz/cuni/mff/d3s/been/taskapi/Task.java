package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.core.TaskPropertyNames;

/**
 * Created with IntelliJ IDEA. User: Kuba Date: 11.03.13 Time: 10:50 To change
 * this template use File | Settings | File Templates.
 */
public abstract class Task {

	private String id;

	public String getId() {
		return id;
	}

	public abstract void run();

	private void initialize() {
		this.id = System.getenv(TaskPropertyNames.TASK_ID);
		// TODO notify HostManager that the task is no longer suspended
		// Messages.send("TASK_RUNNING#");
	}

	public void doMain(String[] args) {
		initialize();
		run();

		// TODO not ideal, what if other threads are running and we shut them down?
		Messages.terminate();
	}
}
