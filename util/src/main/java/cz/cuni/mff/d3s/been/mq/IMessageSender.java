package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;

/**
 * Sends messages to a {@link IMessageQueue}.
 * 
 * @see IMessageReceiver
 * @author Martin Sixta
 * @param T Type of the messages to process
 */
@NotThreadSafe
public interface IMessageSender<T extends Serializable> extends AutoCloseable {

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
	 * Set a linger period for the sender (see ZMQ_LINGER). Accepts the following
	 * values
	 * <dl>
	 * <dt>-1</dt>
	 * <dd>Indefinitely. This is the default value</dd>
	 * <dt>0</dt>
	 * <dd>None. Sent messages will be discarded upon close</dd>
	 * <dt>t>0</dt>
	 * <dd>Linger time in milliseconds</dd>
	 * </dl>
	 * 
	 * @param linger
	 *          Time to linger (in millis)
	 */
	public void setLinger(int linger);

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
