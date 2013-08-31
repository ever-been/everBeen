package cz.cuni.mff.d3s.been.objectrepository.janitor;

/**
 * Different types of the cleanup the {@link Janitor} can perform
 *
 * @author darklight
 */
public enum CleanupType {

	/** Cleanup after failed task contexts */
	CONTEXT_FAILED,
	/** Cleanup after finished task contexts */
	CONTEXT_FINISHED,
	/** Cleanup after zombie task contexts (started but never finished) */
	CONTEXT_ZOMBIE,
	/** Cleanup after failed tasks */
	TASK_FAILED,
	/** Cleanup after finished tasks */
	TASK_FINISHED,
	/** Cleanup after zombie tasks (started but never finished) */
	TASK_ZOMBIE,
	/** Cleanup logs after EverBEEN services */
	SERVICE_LOGS,
	/** Cleanup load samples */
	LOAD_SAMPLES
}
