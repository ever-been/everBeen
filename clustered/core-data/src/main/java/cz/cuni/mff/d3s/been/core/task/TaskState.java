package cz.cuni.mff.d3s.been.core.task;

/**
 * @author Martin Sixta
 */
public enum TaskState {
	CREATED,
	SUBMITTED,
	SCHEDULED,
	RUNNING,
	SLEEPING,
	FINISHED,
	ABORTED, 
	/**
	 * This state means that Task is accepted and being processing by HostRuntime but not started yet.
	 * 
	 * FIXME Martin Sixta: Just to be sure, none of the previous states is usable like this state
	 */
	PROCESSING_BY_HR;

	public boolean canChangeTo(TaskState state) {

		// can abort in any state
		if (this != TaskState.ABORTED && state == TaskState.ABORTED) {
			return true;
		}

		// can advance forward
		if (this.ordinal() + 1 == state.ordinal()) {
			return true;
		}

		// Runtime can decline to run the new task ...
		if (this == TaskState.SCHEDULED && state == SUBMITTED) {
			return true;
		}

		return false;
	}
}
