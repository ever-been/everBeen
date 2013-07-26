package cz.cuni.mff.d3s.been.core.protocol.messages;

@SuppressWarnings("serial")
public final class RunTaskMessage extends BaseMessage {

	/**
	 * ID of the tusk to run;
	 */
	public String taskId;

	public RunTaskMessage(String receiverId, String taskId) {
		super(receiverId);
		this.taskId = taskId;
	}

}
