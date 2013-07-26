package cz.cuni.mff.d3s.been.cluster;

/**
 * 
 * @author Martin Sixta
 */
public class Names {

	// MAP NAMES
	/**
	 * In the map with this name should be stored information about all registered
	 * Host Runtimes.
	 */
	public static final String HOSTRUNTIMES_MAP_NAME = "BEEN_HOSTRUNTIMES";

	/**
	 * In the map with this name should be stored information about important
	 * services (Software Repository, Results Repository etc..).
	 */
	public static final String SERVICES_MAP_NAME = "BEEN_SERVICES";

	/**
	 * In the map with this name should be stored information about all Tasks
	 * (including their actual state).
	 */
	public static final String TASKS_MAP_NAME = "BEEN_MAP_TASKS";

	/**
	 * Map storing current task contexts.
	 */
	public static final String TASK_CONTEXTS_MAP_NAME = "BEEN_MAP_TASK_CONTEXTS";

	/**
	 * Map storing current benchmark entries.
	 */
	public static final String BENCHMARKS_MAP_NAME = "BEEN_MAP_BENCHMARKS";

	/**
	 * Name of the queue of persist requests
	 */
	public static final String PERSISTENCE_QUEUE_NAME = "BEEN_PERSISTENCE_ENTRIES";

	/**
	 * Name of the queue of queries to persistence layers
	 */
	public static final String PERSISTENCE_QUERY_QUEUE_NAME = "BEEN_PERSISTENCE_QUERIES";

	public static final String PERSISTENCE_QUERY_ANSWERS_MAP_NAME = "BEEN_PERSISTENCE_ANSWERS";

	/**
	 * Map with last log for a task.
	 */
	public static final String LOGS_TASK_MAP_NAME = "been.task.logs.task";

	/**
	 * Map with last log for a context.
	 */
	public static final String LOGS_CONTEXT_MAP_NAME = "been.task.logs.context";

	/**
	 * Map with last log for a benchmark.
	 */
	public static final String LOGS_BENCHMARK_MAP_NAME = "been.task.logs.benchmark";

	// TOPIC NAMES
	/**
	 * In the topic with this name should be published complete communication
	 * between Host Runtimes and Task Managers.
	 */
	public static final String BEEN_TOPIC_MAIN_NAME = "BEEN_TOPIC_MAIN";

	// MAP KEYS
	/**
	 * Under this key should be stored information about Software Repository in
	 * the map with name {@link Names#SERVICES_MAP_NAME}
	 */
	public static final String SWREPOSITORY_SERVICES_MAP_KEY = "SWREPOSITORY";

	/**
	 * All benchmarks run in a special task context with this ID.
	 */
	public static final String BENCHMARKS_CONTEXT_ID = "00000000-0000-0000-0000-000000001234";

    public static final String BEEN_MAP_COMMAND_ENTRIES = "BEEN_MAP_COMMAND_ENTRIES";
}
