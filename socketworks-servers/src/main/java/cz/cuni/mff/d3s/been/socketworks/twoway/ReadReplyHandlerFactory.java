package cz.cuni.mff.d3s.been.socketworks.twoway;

/**
 * A factory that provides Read-Reply handlers.
 * 
 * @author darklight
 * 
 */
public interface ReadReplyHandlerFactory {

	/**
	 * Get a handler ready to perform handling logic.This method is guaranteed to
	 * get called every time a new handler thread is spawned. However, no
	 * guarantees are made on behalf of call interleaving on the handler's
	 * methods. If you intend to recycle handler objects, you must keep in mind
	 * that the handler you provided previously might still be in use by some
	 * thread.
	 * 
	 * @return The handler
	 */
	ReadReplyHandler getHandler();
}
