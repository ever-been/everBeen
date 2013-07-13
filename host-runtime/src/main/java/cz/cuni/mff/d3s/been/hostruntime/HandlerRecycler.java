package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandler;

/**
 * Recycling interface for read-reply handlers. Helps prevent garbage collector flooding by providing an interface for recycling handler instances.
 */
public interface HandlerRecycler {

	/**
	 * Recycle a handler.
	 *
	 * @param handler Handler to recycle
	 */
	void recycle(ReadReplyHandler handler);
}
