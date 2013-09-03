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

	/**
	 * Create a persistent variant of the task state
	 */
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

	/**
	 * Get the time at which the task was started
	 *
	 * @return The task start timestamp
	 */
	public long getTimeStarted() {
		return timeStarted;
	}

	/**
	 * Set the time at which the task was started
	 *
	 * @param timeStarted Task start timestamp to set
	 */
	public void setTimeStarted(long timeStarted) {
		this.timeStarted = timeStarted;
	}

	/**
	 * Get the time at which the task finished
	 *
	 * @return The task finish timestamp
	 */
	public long getTimeFinished() {
		return timeFinished;
	}

	/**
	 * Set the time at which the task was finished
	 *
	 * @param timeFinished Task finish timestamp to set
	 */
	public void setTimeFinished(long timeFinished) {
		this.timeFinished = timeFinished;
	}

	/**
	 * Get the ID of the <em>Host Runtime</em> on which this task was run
	 *
	 * @return The <em>Host Runtime</em> ID
	 */
	public String getRuntimeId() {
		return runtimeId;
	}

	/**
	 * Set the ID of the <em>Host Runtime</em> on which this task was run
	 *
	 * @param runtimeId <em>Host Runtime</em> ID to set
	 */
	public void setRuntimeId(String runtimeId) {
		this.runtimeId = runtimeId;
	}
}
