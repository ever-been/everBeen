package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;

/**
 * An {@link Action} that represent an invalid request.
 * 
 * @author Martin Sixta
 */
public class ErrorAction implements Action {

	/** the message of the error  */
	private final String msg;

	/**
	 * Default constructor, creates a new instance with the specified message.
	 * 
	 * @param msg
	 *          the message of the error
	 */
	public ErrorAction(String msg) {
		this.msg = msg;
	}

	@Override
	public Reply handle() {
		return Replies.createErrorReply(msg);
	}

}
