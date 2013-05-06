package cz.cuni.mff.d3s.been.mq.req;

/**
 * @author Martin Sixta
 */
public enum RequestType {
	WAIT, GET, SET, LATCH_DOWN, LATCH_WAIT, LATCH_SET, LATCH_HAS_COUNT,
	CONTEXT_SUBMIT, CONTEXT_WAIT
}
