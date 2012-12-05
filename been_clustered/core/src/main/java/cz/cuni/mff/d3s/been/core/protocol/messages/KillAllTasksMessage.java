package cz.cuni.mff.d3s.been.core.protocol.messages;

/**
 * This message is sent when all tasks should be killed on receiver node.
 * 
 * @author donarus
 * 
 */
public final class KillAllTasksMessage extends BaseMessage {

	/**
	 * Reason why tasks should be killed
	 */
	public String reason;

}
