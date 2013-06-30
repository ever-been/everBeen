package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;

/**
 * @author Martin Sixta
 */
public class ErrorAction implements Action {
	private final String msg;

	public ErrorAction(String msg) {
		this.msg = msg;
	}

	@Override
	public Reply goGetSome() {
		return Replies.createErrorReply(msg);
	}
}
