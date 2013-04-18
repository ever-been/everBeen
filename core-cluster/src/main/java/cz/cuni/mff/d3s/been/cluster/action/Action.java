package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.mq.rep.Replay;

/**
 * @author Martin Sixta
 */
public interface Action {
	/**
	 * 
	 * Handles the request, can block.
	 * 
	 * TODO figure out better name or one that actually is funny
	 * 
	 * @return replay to the request
	 */
	public Replay goGetSome();
}
