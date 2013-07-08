package cz.cuni.mff.d3s.been.task.checkpoints;

/**
 * @author Martin Sixta
 */
public enum CheckpointRequestType {
	WAIT, GET, SET, LATCH_DOWN, LATCH_WAIT, LATCH_SET, LATCH_HAS_COUNT,
	CONTEXT_SUBMIT, CONTEXT_WAIT,
	STORAGE_PERSIST, STORAGE_RETRIEVE
}
