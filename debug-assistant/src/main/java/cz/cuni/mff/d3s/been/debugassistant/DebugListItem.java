package cz.cuni.mff.d3s.been.debugassistant;

import java.io.Serializable;

/**
 * Debug item.
 * 
 * @author Kuba Břečka
 */
public class DebugListItem implements Serializable {
	private String taskId;
	private String hostName;
	private int debugPort;
	private boolean suspended;

	/**
	 * Whether the task is suspended.
	 * 
	 * @return Whether the task is suspended.
	 */
	public boolean isSuspended() {
		return suspended;
	}

	/**
	 * Sets suspended flag.
	 * 
	 * @param suspended
	 *          the flag
	 */
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	/**
	 * Returns taskId of the listening task
	 * 
	 * @return taskId of the listening task
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * Returns host name of the listening task
	 * 
	 * @return host name of the listening task
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Returns debug port of the listening task
	 * 
	 * @return debug port of the listening task
	 */
	public int getDebugPort() {
		return debugPort;
	}

	/**
	 * Creates new DebugListItem.
	 * 
	 * @param taskId
	 *          ID of the listening task
	 * @param hostName
	 *          host name where the task is
	 * @param debugPort
	 *          port the task listens on
	 * @param suspended
	 *          whether the task was suspended on start
	 */
	public DebugListItem(String taskId, String hostName, int debugPort, boolean suspended) {
		this.taskId = taskId;
		this.hostName = hostName;
		this.debugPort = debugPort;
		this.suspended = suspended;
	}
}
