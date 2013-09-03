package cz.cuni.mff.d3s.been.core.protocol.messages;

/**
 * This message is sent when specified task working directory should be deleted
 * on host runtime
 * 
 * @author donarus
 */
@SuppressWarnings("serial")
public final class DeleteTaskWrkDirMessage extends BaseMessage {

	/**
	 * key for
	 */
	public static final String OPERATION_ID_KEY = "DELETE_MSG";

	/**
	 * cluster wide unique id of operation
	 */
	public final long operationId;

	/**
	 * Task working dir to be deleted
	 */
	public final String taskWrkDirName;

	/**
	 * Constructs new message.
	 * 
	 * @param receiverId
	 *          id of message receiver
	 * @param taskWrkDirName
	 *          nam of task working directory to be deleted
	 */
	public DeleteTaskWrkDirMessage(String receiverId, String taskWrkDirName, long operationId) {
		super(receiverId);
		this.taskWrkDirName = taskWrkDirName;
		this.operationId = operationId;
	}

}
