package cz.cuni.mff.d3s.been.core;

/**
 * 
 * Defines property names for running task. Host Runtime must define these
 * properties in order to be used in tasks.
 * 
 * @author Martin Sixta
 */
public class TaskPropertyNames {

	/**
	 * port used for checkpoint synchronization
	 */
	public static final String REQUEST_PORT = "been.task.hr.request.port";

	/**
	 * System property with Task ID
	 */
	public static final String TASK_ID = "been.task.id";

	/**
	 * System property with Task Context ID
	 */
	public static final String TASK_CONTEXT_ID = "been.taskcontext.id";

	/**
	 * System property with Benchmark ID
	 */
	public static final String BENCHMARK_ID = "been.benchmark.id";

	/**
	 * System property with Host Runtime Communication Port (aka sink). Through
	 * this port is realized for example logging.
	 */
	public static final String HR_COMM_PORT = "been.task.mq.sink.port";

	/**
	 * System property with alias for absolute path to logger script for native
	 * tasks.
	 */
	public static final String LOGGER = "logger";

	/**
	 * 
	 * System property with Host Runtime Results Port
	 * 
	 */
	public static final String HR_RESULTS_PORT = "been.task.mq.results.port";

	/**
	 * System property with value of Host Runtime host name.
	 */
	public static final String HR_HOSTNAME = "been.task.hr.hostname";

}
