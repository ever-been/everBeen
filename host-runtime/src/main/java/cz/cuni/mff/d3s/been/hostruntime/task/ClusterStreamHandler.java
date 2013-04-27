package cz.cuni.mff.d3s.been.hostruntime.task;

import org.apache.commons.exec.LogOutputStream;

import com.hazelcast.core.MultiMap;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.LogMessage;

/**
 * @author Martin Sixta
 */
public class ClusterStreamHandler extends LogOutputStream {
	private final ClusterContext ctx;
	private final String taskId;
	private final String contextId;
	private final String name;
	private final MultiMap<String, LogMessage> logMap;
	private static final String LOG_PROPERTY = "been.hostruntime.task.output";

	public ClusterStreamHandler(ClusterContext ctx, String taskId, String contextId, String name) {
		this.ctx = ctx;
		this.taskId = taskId;
		this.contextId = contextId;
		this.name = name;
		this.logMap = ctx.getMultiMap(Names.LOGS_MULTIMAP_NAME);

	}

	@Override
	protected void processLine(String line, int level) {
		LogMessage logMsg = new LogMessage(name, level, line, null, taskId, contextId);
		logMsg.setThreadName(null);
		logMap.put(taskId, logMsg);

		// TODO
		if (System.getProperty(LOG_PROPERTY, "true").equals("true")) {
			System.out.printf("[%s]: %d: %s\n", name, level, line);
		}

	}

}
