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

	/**
	 * Returns port the receiver is bind to.
	 * 
	 * The port does not make always sense, as in inproc case.
	 * 
	 * @return port the receiver is bind to
	 */
	public int getPort();

	/**
	 * Closes the receiver.
	 * 
	 * @throws MessagingException
	 */
	public void close();

}
