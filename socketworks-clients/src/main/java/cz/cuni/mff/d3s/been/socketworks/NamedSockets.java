package cz.cuni.mff.d3s.been.socketworks;

/**
 * Sockets provided by the
 */
public enum NamedSockets {
	TASK_LOG_0MQ("TaskLogs"), TASK_CHECKPOINT_0MQ("TaskCheckpoints"), TASK_RESULT_0MQ("TaskResults");

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
