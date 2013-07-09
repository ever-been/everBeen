package cz.cuni.mff.d3s.been.hostruntime.task;

import static cz.cuni.mff.d3s.been.socketworks.NamedSockets.TASK_LOG_0MQ;

import org.apache.commons.exec.LogOutputStream;

import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.TaskMessageType;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.core.utils.JsonException;
import cz.cuni.mff.d3s.been.mq.MessageQueues;

/**
 * 
 * Processes output from a task line by line.
 * 
 * A line is converted to a {@link LogMessage} and send to a queue for
 * processing.
 * 
 * // TODO This implementation is suboptimal since it uses
 * {@link MessageQueues#send(String, java.io.Serializable)}.
 * 
 * 
 * @author Martin Sixta
 */
public class ClusterStreamHandler extends LogOutputStream {
	private final String taskId;
	private final String contextId;
	private final String benchmarkId;
	private final String name;

	private final MessageQueues messageQueues;

	public ClusterStreamHandler(String taskId, String contextId, String benchmarkId, String name) {
		this.taskId = taskId;
		this.contextId = contextId;
		this.benchmarkId = benchmarkId;
		this.name = name;
		this.messageQueues = MessageQueues.getInstance();

	}

	@Override
	protected void processLine(String line, int level) {
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
		LogMessage logMsg = new LogMessage(name, level, line).withTimestamp();
		logMsg.withTaskId(taskId).withContextId(contextId).withBenchmarkId(benchmarkId);

		return JSONUtils.serialize(logMsg);
	}

	private String createTypedMessage(String json) {
		return String.format("%s#%s", TaskMessageType.LOG_MESSAGE.toString(), json);
	}

}
