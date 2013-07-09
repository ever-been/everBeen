package cz.cuni.mff.d3s.been.taskapi;

import static cz.cuni.mff.d3s.been.core.TaskMessageType.LOG_MESSAGE;
import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.*;
import static cz.cuni.mff.d3s.been.core.utils.JSONUtils.serialize;
import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.utils.JsonException;

/**
 * Logger implementation for a BEEN Task.
 * 
 * Logs are redirected to Host Runtime for processing/storage.
 * 
 * @author Kuba Brecka
 * @author Martin Sixta
 */
public class TaskLogger extends TaskLoggerBase {

	private static final String taskId;
	private static final String contextId;
	private static final String benchmarkId;

	private String name;

	static {
		taskId = System.getenv(TASK_ID);
		contextId = System.getenv(CONTEXT_ID);
		benchmarkId = System.getenv(BENCHMARK_ID);
	}

	public TaskLogger(String name) {

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

		return serialize(logMsg);

	}
}
