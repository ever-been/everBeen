package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;

/**
 * An action that should be performed to handle a request from a task or
 * benchmark.
 * 
 * @author Martin Sixta
 */
public interface Action {

	/**
	 * Handles the request, can block.
	 * 
	 * @return reply to the request
	 */
	public Reply handle();

}
