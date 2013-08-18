package cz.cuni.mff.d3s.been.hostruntime.task;

import static cz.cuni.mff.d3s.been.socketworks.NamedSockets.TASK_LOG_0MQ;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.LogOutputStream;

import cz.cuni.mff.d3s.been.core.TaskMessageType;
import cz.cuni.mff.d3s.been.logging.LogMessage;
import cz.cuni.mff.d3s.been.logging.TaskLogMessage;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * 
 * Processes output from a task line by line.
 * 
 * A line is converted to a {@link LogMessage} and request to a queue for
 * processing.
 * 
 * // TODO This implementation is suboptimal since it uses
 * {@link MessageQueues#send(String, java.io.Serializable)}.
 * 
 * 
 * @author Martin Sixta
 */
public class TaskStdInOutHandler extends LogOutputStream {
	public static final char UNIX_LINE_SEPARATOR = '\n';
	private final String taskId;
	private final String contextId;
	private final String benchmarkId;
	private final String name;

	private final MessageQueues messageQueues;
	private final JSONUtils jsonUtils;
	private final OutputStream secondRedirectOutputStream;

	public TaskStdInOutHandler(
			String taskId,
			String contextId,
			String benchmarkId,
			String name,
			OutputStream secondRedirectOutputStream) {
		this.taskId = taskId;
		this.contextId = contextId;
		this.benchmarkId = benchmarkId;
		this.name = name;
		this.messageQueues = MessageQueues.getInstance();
		this.jsonUtils = JSONUtils.newInstance();
		this.secondRedirectOutputStream = secondRedirectOutputStream;
	}

	@Override
	protected void processLine(String line, int level) {
		try {
			secondRedirectOutputStream.write((line + UNIX_LINE_SEPARATOR).getBytes());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		// Fabricate a log message

		// Send it
		try {
			String json = createJsonLogMessage(line, level);

			messageQueues.send(TASK_LOG_0MQ.getName(), createTypedMessage(json));

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private String createJsonLogMessage(String line, int level) throws JsonException {
		TaskLogMessage logMsg = new TaskLogMessage().withMessage(new LogMessage(name, level, line));
		logMsg.withTaskId(taskId).withContextId(contextId).withBenchmarkId(benchmarkId);

		return jsonUtils.serialize(logMsg);
	}

	private String createTypedMessage(String json) {
		return String.format("%s#%s", TaskMessageType.LOG_MESSAGE.toString(), json);
	}

	@Override
	public void close() throws IOException {
		super.close();
		secondRedirectOutputStream.close();
	}
}
