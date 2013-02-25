package cz.cuni.mff.d3s.been.core.protocol.messages;

@SuppressWarnings("serial")
public final class TaskStartedMessage extends BaseMessage {

	/**
	 * Name of the started task
	 */
	public String taskName;

	public TaskStartedMessage(String senderId, String receiverId, String taskName) {
		super(senderId, receiverId);
		this.taskName = taskName;
	}

}
