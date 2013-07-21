package cz.cuni.mff.d3s.been.taskapi;

import static cz.cuni.mff.d3s.been.core.TaskMessageType.LOG_MESSAGE;
import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.*;
import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * Logger implementation for a BEEN Task.
 * 
 * Logs are redirected to Host Runtime for processing/storage.
 * 
 * @author Kuba Brecka
 * @author Martin Sixta
 */
class TaskLogger extends TaskLoggerBase {

	private static final TaskLogLevel DEFAULT_LOG_LEVEL = TaskLogLevel.INFO;

	private static final String taskId;
	private static final String contextId;
	private static final String benchmarkId;
	private static final TaskLogLevel logLevel;

	private String name;
	private final JSONUtils jsonUtils = JSONUtils.newInstance();

	static {
		taskId = System.getenv(TASK_ID);
		contextId = System.getenv(CONTEXT_ID);
		benchmarkId = System.getenv(BENCHMARK_ID);

		String logLevelString = System.getenv(TASK_LOG_LEVEL);

		TaskLogLevel tmpLogLevel;
		try {
			tmpLogLevel = TaskLogLevel.valueOf(logLevelString);
		} catch (IllegalArgumentException | NullPointerException e) {
			tmpLogLevel = DEFAULT_LOG_LEVEL;
		}

		logLevel = tmpLogLevel;
	}

	public TaskLogger(String name) {

		setLogLevel(logLevel);

		this.name = name;
	}

	/**
	 * Logs messages.
	 * 
	 * 
	 * @param level
	 *          log level
	 * @param message
	 *          log message
	 * @param t
	 *          throwable when logging exceptions
	 * 
	 */
	@Override
	void log(int level, String message, Throwable t) {
		String serializedMsg;
		try {
			serializedMsg = createJsonLogMessage(level, message, t);
		} catch (JsonException e) {
			// The message cannot be created, so just print stack trace
			// This should not happen
			e.printStackTrace();
			return;
		}

		try {
			// TODO magic string
			String msg = String.format("%s#%s", LOG_MESSAGE.toString(), serializedMsg);
			Messages.send(msg);
		} catch (Exception e) {
			System.err.printf("Cannot send log message: %s", serializedMsg);
			e.printStackTrace();
		}
	}

	private String createJsonLogMessage(int level, String message, Throwable t) throws JsonException {

		LogMessage logMsg = new LogMessage(name, level, message).withThreadName().withTimestamp().withThroable(t);
		logMsg.withTaskId(taskId).withContextId(contextId).withBenchmarkId(benchmarkId);

		return jsonUtils.serialize(logMsg);

	}

}
