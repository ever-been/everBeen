package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;

/**
 * Message Queue interface for multiple senders and single receiver.
 * 
 * Use {@link Messaging} to create appropriate instances.
 * 
 * <p/>
 * 
 * @author Martin Sixta
 * @param T Type of the messages to process
 */
@NotThreadSafe
public interface IMessageQueue<T extends Serializable> {
	/**
	 * Returns receiver ready to receive messages.
	 * 
	 * Current implementations assume just one receiver (might be changed if need
	 * for multiply receivers arises).
	 * 
	 * @return The receiver
	 *
	 * @throws MessagingException Whe a receiver can't be created
	 */
	public IMessageReceiver<T> getReceiver() throws MessagingException;

	/**
	 * Returns sender ready to send messages.
	 * 
	 * @return sender ready to send messages
	 *
	 * @throws MessagingException When a sender cannot be created
	 */
	public IMessageSender<T> createSender() throws MessagingException;

	/**
	 * Terminates the message queue.
	 * 
	 * @throws MessagingException
	 *           On attempt to terminate a dangling queue
	 */
	public void terminate() throws MessagingException;
}
