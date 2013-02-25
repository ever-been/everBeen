package cz.cuni.mff.d3s.been.core.protocol.messages;

@SuppressWarnings("serial")
public final class TaskFinishedMessage extends BaseMessage {

	/**
	 * Name of the finished task
	 */
	public String taskName;

	public TaskFinishedMessage(String senderId, String recieverId, String taskName) {
		super(senderId, recieverId);
		this.taskName = taskName;
	}

}
