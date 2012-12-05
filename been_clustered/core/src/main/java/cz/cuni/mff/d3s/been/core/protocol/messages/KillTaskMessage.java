package cz.cuni.mff.d3s.been.core.protocol.messages;

/**
 * This message is sent when specified task should be killed on receiver node.
 * 
 * @author donarus
 * 
 */
public final class KillTaskMessage extends BaseMessage {

	/**
	 * Reason why specified should be killed
	 */
	public String reason;

	/**
	 * Task which should be terminated
	 */
	// FIXME ?? maybe taskId instead of taskName
	public String taskName;

}
