package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.rep.Reply;

/**
 * @author Martin Sixta
 */
public class ErrorAction implements Action {
	private final String msg;

	public ErrorAction(String msg) {
		this.msg = msg;
	}

	@Override
	public Reply handle() {
		return Replies.createErrorReply(msg);
	}
}
