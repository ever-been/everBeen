package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;

/**
 * @author Martin Sixta
 */
public interface Action {
	/**
	 * 
	 * Handles the request, can block.
	 * 
	 * @return reply to the request
	 */
	public Reply handle();
}
