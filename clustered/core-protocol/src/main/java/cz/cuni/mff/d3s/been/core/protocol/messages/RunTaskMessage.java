package cz.cuni.mff.d3s.been.core.protocol.messages;

import java.util.List;

@SuppressWarnings("serial")
public final class RunTaskMessage extends BaseMessage {

	/**
	 * ID of the tusk to run;
	 */
	public String taskId;

	/**
	 * Name of the task
	 */
	public String name;

	/**
	 * Maximum allowed task restarts - task should be terminated when
	 * maxRestarts reached
	 */
	public int maxRestarts;

	/**
	 * Task maximum timeout in seconds - task should be killed/restarted when
	 * timeout is reached
	 */
	public int timeout;

	/**
	 * JVM arguments for new JVM in which the task is run
	 */
	public List<String> javaArgs;

	/**
	 * Arguments for task
	 */
	public List<String> taskArgs;

	public boolean isDetailedMonitoring;

	public boolean isExclusive;

	/**
	 * Name of BPK task package.
	 */
	public String packageName;

}
