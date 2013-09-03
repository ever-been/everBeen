package cz.cuni.mff.d3s.been.socketworks.oneway;

import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;

/**
 * A read-only handler for one-way messages.
 * 
 * @author darklight
 */
public interface ReadOnlyHandler {

	/**
	 * Handle received message
	 *
	 * @param message Message to handle
	 *
	 * @throws SocketHandlerException When message processing fails, this exception should be thrown
	 */
	void handle(String message) throws SocketHandlerException;
}
