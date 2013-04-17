package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.mq.rep.Replay;

/**
 * @author Martin Sixta
 */
public interface Action {
	public Replay goGetSome();
}
