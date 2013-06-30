package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * A participant in {@link Frames} exchange.
 * 
 * @author darklight
 * 
 */
interface FrameSink {

	/**
	 * This object has received some {@link Frames} from another {@link FrameSink}
	 * (at his communication level).
	 * 
	 * @param frames
	 *          {@link Frames} received
	 * 
	 * @throws MessagingException
	 *           If received data is corrupted
	 */
	void receiveFromBuddy(Frames frames) throws MessagingException;

	/**
	 * This object has received some {@link Frames} from the underlying wire (one
	 * communication level below).
	 * 
	 * @param frames
	 *          {@link Frames} received
	 * @throws MessagingException
	 *           If received data is corrupted
	 */
	void receiveFromWire(Frames frames) throws MessagingException;
}
