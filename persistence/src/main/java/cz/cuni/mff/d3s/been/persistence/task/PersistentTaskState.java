package cz.cuni.mff.d3s.been.persistence.task;

import cz.cuni.mff.d3s.been.core.persistence.TaskEntity;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * A persistent carrie object for {@link TaskState}
 * 
 * @author darklight
 */
public class PersistentTaskState extends TaskEntity {

	private long timeStarted;
	private long timeFinished;
	private String runtimeId;
	private TaskState taskState;

	public PersistentTaskState() {
		created = System.currentTimeMillis();
	}

	/**
	 * Get the state with which this task has finished
	 * 
	 * @return The task state
	 */
	public TaskState getTaskState() {
		return taskState;
	}

	/**
	 * Set the state with which given task has finished
	 * 
	 * @param taskState
	 *          State to set
	 */
	public void setTaskState(TaskState taskState) {
		this.taskState = taskState;
	}

	/**
	 * Fluently set the state with which given task has finished
	 * 
	 * @param taskState
	 *          State to set
	 * 
	 * @return This {@link PersistentTaskState}, with changed task state
	 */
	public PersistentTaskState withTaskState(TaskState taskState) {
		setTaskState(taskState);
		return this;
	}

	public long getTimeStarted() {
		return timeStarted;
	}

	public void setTimeStarted(long timeStarted) {
		this.timeStarted = timeStarted;
	}

	public long getTimeFinished() {
		return timeFinished;
	}

	public void setTimeFinished(long timeFinished) {
		this.timeFinished = timeFinished;
	}

	public String getRuntimeId() {
		return runtimeId;
	}

	public void setRuntimeId(String runtimeId) {
		this.runtimeId = runtimeId;
	}
}
