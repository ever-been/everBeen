package cz.cuni.mff.d3s.been.socketworks;

/**
 * Naming conventions for the sockets between <em>task</em> and <em>Host Runtime</em> processes
 */
public enum NamedSockets {
	/** Log socket */
	TASK_LOG_0MQ("TaskLogs"),
	/** Checkpoint socket */
	TASK_CHECKPOINT_0MQ("TaskCheckpoints"),
	/** Result socket */
	TASK_RESULT_PERSIST_0MQ("TaskResults"),
	/** Resutl query socket */
	TASK_RESULT_QUERY_0MQ("TaskResultQueries");

	private final String envVarName;

	private NamedSockets(String envVarName) {
		this.envVarName = envVarName;
	}

	/**
	 * @return The connection string to this named socket
	 */
	public String getConnection() {
		return System.getenv(envVarName);
	}

	/**
	 * @return The name of the environment variable associated with this named
	 *         socket
	 */
	public String getName() {
		return envVarName;
	}
}
