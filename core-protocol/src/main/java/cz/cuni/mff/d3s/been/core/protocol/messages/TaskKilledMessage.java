package cz.cuni.mff.d3s.been.core.protocol.messages;

@SuppressWarnings("serial")
public final class TaskKilledMessage extends BaseMessage {

	/**
	 * Name of the killed task
	 */
	public String taskName;

	public TaskKilledMessage(String senderId, String recieverId, String taskName) {
		super(senderId, recieverId);
		this.taskName = taskName;
	}

}
