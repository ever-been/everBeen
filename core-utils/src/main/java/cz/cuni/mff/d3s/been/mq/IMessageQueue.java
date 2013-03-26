package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;

/**
 * Message Queue interface for multiply senders and single receiver.
 * 
 * Use {@link Messaging} to create appropriate instances.
 * 
 * <p/>
 * 
 * 
 * Simple API for simple needs.
 * 
 * @author Martin Sixta
 */
@NotThreadSafe
public interface IMessageQueue<T extends Serializable> {
	/**
	 * Returns receiver ready to receive messages.
	 * 
	 * Current implementations assume just one receiver (might be changed if need
	 * for multiply receivers arises).
	 * 
	 * @return
	 * @throws MessagingException
	 *           when the
	 */
	public IMessageReceiver<T> getReceiver() throws MessagingException;

	/**
	 * Returns sender ready to send messages.
	 * 
	 * @return sender ready to send messages
	 * @throws MessagingException
	 *           when a sender cannot be created
	 */
	public IMessageSender<T> createSender() throws MessagingException;

	/**
	 * Terminates the message queue.
	 */
	public void terminate();
}
