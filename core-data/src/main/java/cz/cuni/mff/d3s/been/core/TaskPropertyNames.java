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
	 * System property with Task ID
	 */
	public static final String TASK_ID = "been.task.id";

	/**
	 * System property with Task Context ID
	 */
	public static final String CONTEXT_ID = "been.context.id";

	/**
	 * System property with Benchmark ID
	 */
	public static final String BENCHMARK_ID = "been.benchmark.id";

	/**
	 * System property with alias for absolute path to logger script for native
	 * tasks.
	 */
	public static final String LOGGER = "logger";

	/** Name of the environment property which sets Task log level */
	public static final String TASK_LOG_LEVEL = "task.log.level";

}
