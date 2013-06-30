package cz.cuni.mff.d3s.been.socketworks.oneway;

import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;

/**
 * A read-only handler for one-way messages.
 * 
 * @author darklight
 */
public interface ReadOnlyHandler {
	void handle(String message) throws SocketHandlerException;
}
