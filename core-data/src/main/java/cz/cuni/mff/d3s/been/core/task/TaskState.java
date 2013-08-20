package cz.cuni.mff.d3s.been.core.task;

/**
 * Different states a BEEN task can happen to be in.
 * 
 * To change the state of a {@link TaskEntry} use
 * {@link TaskEntries#setState(TaskEntry, TaskState, String, Object...)}, as it
 * will also create a log entry with the reason for the task state change.
 * 
 * @author Martin Sixta
 * 
 */
public enum TaskState {
	/**
	 * The initial state after creating a {@link TaskEntry}
	 */
	CREATED,

	/**
	 * The task is submitted to a task manager which will try to schedule it.
	 */
	SUBMITTED,

	/**
	 * Indicates that a task has been scheduled on a Host Runtime. The Host
	 * Runtime needs to accept the task before running it.
	 */
	SCHEDULED,

	/**
	 * Indicates that a task has been accepted by a Host Runtime and will be run
	 * after creating suitable environment for it .
	 */
	ACCEPTED,

	/**
	 * Indicates that a task is waiting for an asynchronous event.
	 */
	WAITING,

	/**
	 * Indicates that a task is currently running.
	 */
	RUNNING,

	/**
	 * Indicates that a task normally finished.
	 */
	FINISHED,

	/**
	 * Indicates that a task has been aborted.
	 */
	ABORTED;

	/**
	 * 
	 * Checks whether task state can be changed from the current state.
	 * 
	 * @param state
	 *          state to change to
	 * @return true if the change is legal, false otherwise
	 */
	public boolean canChangeTo(TaskState state) {

		// can abort in any state
		if (this != TaskState.ABORTED && state == TaskState.ABORTED) {
			return true;
		}

		// can advance forward
		if (this.ordinal() + 1 == state.ordinal()) {
			return true;
		}

		// can skip WAITING
		if (this == ACCEPTED && state == RUNNING) {
			return true;
		}

		// Runtime can decline to run the new task ...
		if (this == TaskState.SCHEDULED && state == SUBMITTED) {
			return true;
		}

		if (this == SUBMITTED && state == WAITING) {
			return true;
		}

		if (this == WAITING && state == SCHEDULED) {
			return true;
		}

		// can resubmit on a failed Host Runtime
		if (this == SCHEDULED && state == SCHEDULED) {
			return true;
		}

		return false;
	}
}
