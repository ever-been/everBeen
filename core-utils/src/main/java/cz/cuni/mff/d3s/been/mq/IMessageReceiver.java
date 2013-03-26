package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;

/**
 * Receives messages to a {@link IMessageQueue}.
 * 
 * @see IMessageSender
 * @author Martin Sixta
 */
@NotThreadSafe
public interface IMessageReceiver<T extends Serializable> {

	/**
	 * Blocks until a message is received.
	 * 
	 * @return received message
	 * @throws MessagingException
	 *           a message cannot be received (close sockets, inappropriate
	 *           message)
	 */
	public T receive() throws MessagingException;

	/**
	 * Returns connection status.
	 * 
	 * @return true if the sender is connected to its queue, false otherwise
	 */
	public boolean isConnected();

}
