package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;

/**
 * A replying message handler.
 * 
 * @author darklight
 * 
 */
public interface ReadReplyHandler {

	/**
	 * Process a message, reply when ready. This call may block.
	 * 
	 * @param message
	 *          Message to process
	 * 
	 * @return The response (once this message is processed)
	 * 
	 * @throws HandlerException
	 *           If something goes wrong during handling
	 * @throws InterruptedException
	 *           If the call blocks and is interrupted
	 */
	String handle(String message) throws SocketHandlerException, InterruptedException;

	/**
	 * The {@link ReadReplyGuard}'s thread keeper guarantees to call this method
	 * once it's done with a {@link ReadReplyHandler} instance. This should prime
	 * the object for recycling.
	 */
	void markAsRecyclable();
}
