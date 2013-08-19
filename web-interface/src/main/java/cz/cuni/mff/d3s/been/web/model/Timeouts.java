package cz.cuni.mff.d3s.been.web.model;

/**
 * This class contains definition of timeout constants for some operation
 * invoked from web interface.
 * 
 * @author donarus
 */
public final class Timeouts {

	private Timeouts() {
		// prevents instantiation
	}

	/** timeout for kill task operation in seconds */
	public static final int KILL_TASK_TIMEOUT = 10;

	/** timeout for kill task context operation in seconds */
	public static final int KILL_TASK_CONTEXT_TIMEOUT = 20;

	/** timeout for kill task benchmark operation in seconds */
	public static final int KILL_BENCHMARK_TIMEOUT = 30;

}
