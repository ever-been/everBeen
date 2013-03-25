package cz.cuni.mff.d3s.been.debugassistant;

import java.lang.String;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 25.03.13
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class DebugListItem implements Serializable {
	String taskId;
	String hostName;
	int debugPort;

	public String getTaskId() {
		return taskId;
	}

	public String getHostName() {
		return hostName;
	}

	public int getDebugPort() {
		return debugPort;
	}

	public DebugListItem(String taskId, String hostName, int debugPort) {
		this.taskId = taskId;
		this.hostName = hostName;
		this.debugPort = debugPort;
	}
}
