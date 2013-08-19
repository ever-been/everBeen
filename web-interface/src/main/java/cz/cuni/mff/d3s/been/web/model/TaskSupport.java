package cz.cuni.mff.d3s.been.web.model;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * @author donarus
 */
public final class TaskSupport {

	private final BeenApi api;

	public TaskSupport(BeenApi api) {
		this.api = api;
	}

	public void killTask(String taskId) throws BeenApiException, InterruptedException {
		api.killTask(taskId);
		int time = 0;
		TaskEntry entry = api.getTask(taskId);
		while (time < Timeouts.KILL_TASK_TIMEOUT && entry != null && entry.getState() != TaskState.ABORTED && entry.getState() != TaskState.FINISHED) {
			Thread.sleep(1000);
			time++;
			entry = api.getTask(taskId);
		}
	}

	public void removeKilledTask(String taskId) throws BeenApiException {
		this.api.removeTaskEntry(taskId);
	}

	public boolean isTaskInFinalState(String taskId) throws BeenApiException {
		TaskEntry taskEntry = this.api.getTask(taskId);
		TaskState state = taskEntry.getState();
		return state == TaskState.ABORTED || state == TaskState.FINISHED;
	}

}
