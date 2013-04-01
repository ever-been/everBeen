package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;

/**
 * Sends messages to a {@link IMessageQueue}.
 * 
 * @see IMessageReceiver
 * @author Martin Sixta
 */
@NotThreadSafe
public interface IMessageSender<T extends Serializable> {

	/**
	 * Sends an object (= message) to its message queue.
	 * 
	 * TODO: when it will block? Will messages be lost?
	 * 
	 * @param object
	 *          object to send
	 * @throws MessagingException
	 *           when the object cannot be sent
	 */
	public void send(final T object) throws MessagingException;

	/**
	 * Returns address (=queue) the sender is connected to.
	 * 
	 * @return address
	 */
	public String getConnection();

	/**
	 * Closes the sender. No further message can be sent from the sender.
	 */
	public void close();
}
