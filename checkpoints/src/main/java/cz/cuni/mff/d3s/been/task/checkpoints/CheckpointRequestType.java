package cz.cuni.mff.d3s.been.task.checkpoints;

/**
 * Enum representing all available checkpoint request types, used by
 * {@link CheckpointRequest}.
 * 
 * @author Martin Sixta
 */
public enum CheckpointRequestType {

	/** wait operation for a checkpoint */
	WAIT,

	/** get the current value of the checkpoint */
	GET,

	/** sets a new value to the checkpoint */
	SET,

	/** perform a latch-down operation on a latch */
	LATCH_DOWN,

	/** waits for the latch to become zero */
	LATCH_WAIT,

	/** set a value of the latch */
	LATCH_SET,

	/** checks whether the value of the latch is zero or not */
	LATCH_HAS_COUNT,

	/** submits a task context */
	CONTEXT_SUBMIT,

	/** waits for a task context to finish */
	CONTEXT_WAIT,

	/** persists a key-value storage of the benchmark */
	STORAGE_PERSIST,

	/** retrieves a previously persisted key-value storage */
	STORAGE_RETRIEVE,

	/** retrieves the complete resubmit history of a benchmark */
	RESUBMIT_HISTORY_RETRIEVE,

	/** retrieves the statuses of contexts withing a benchmark */
	CONTAINED_CONTEXTS_RETRIEVE

}
