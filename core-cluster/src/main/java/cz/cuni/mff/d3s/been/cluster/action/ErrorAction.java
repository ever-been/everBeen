package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.Replays;

/**
 * @author Martin Sixta
 */
public class ErrorAction implements Action {
	private final String msg;

	public ErrorAction(String msg) {
		this.msg = msg;
	}

	@Override
	public Replay goGetSome() {
		return Replays.createErrorReplay(msg);
	}
}
