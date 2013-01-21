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
	ABORTED;
}
