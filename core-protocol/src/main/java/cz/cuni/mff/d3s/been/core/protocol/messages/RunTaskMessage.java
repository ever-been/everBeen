package cz.cuni.mff.d3s.been.core.protocol.messages;

/**
 * Message to be sent to a Host Runtime to run a Task
 */
@SuppressWarnings("serial")
public final class RunTaskMessage extends BaseMessage {

	/**
	 * ID of the tusk to run;
	 */
	public String taskId;

	/**
	 * Creates new RunTaskMessage
	 * 
	 * @param receiverId
	 *          ID of the Host Runtime the message is to be sent to
	 * @param taskId
	 *          ID of the task to run
	 */
	public RunTaskMessage(String receiverId, String taskId) {
		super(receiverId);
		this.taskId = taskId;
	}

}
