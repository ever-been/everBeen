package cz.cuni.mff.d3s.been.core.protocol.messages;

/**
 * This message is sent when specified task should be killed on receiver node.
 * 
 * @author donarus
 * 
 */
@SuppressWarnings("serial")
public final class KillTaskMessage extends BaseMessage {

	/**
	 * Reason why specified should be killed
	 */
	public String reason;

	/**
	 * Task which should be terminated
	 */
	public String taskId;

	/**
	 * Constructs new message.
	 *
	 * @param recieverId
	 *          id of message receiver
	 * @param reason
	 *          why the task should be killed (this text could be used for example
	 *          for logging of another debug purposes)
	 * @param taskId
	 *          cluster-wide unique task identifier
	 * 
	 */
	public KillTaskMessage(String recieverId, String reason, String taskId) {
		super(recieverId);
		this.reason = reason;
		this.taskId = taskId;
	}

}
