package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;

/**
 * Sender for a queue in inter-process communication.
 * 
 * The queue can send/receive only one type of an object, specified by the type
 * parameter.
 * 
 * Multiply senders can push messages to a receiver. We do not care who the
 * receiver is. We just know that it will be received and taken care of.
 * 
 * Implementation notes: Implemented using 0MQ PUSH-PULL model in 0MQ. This is
 * the PUSH part.
 * 
 * @see IMessageReceiver
 * @author Martin Sixta
 */
@NotThreadSafe
final class InprocMessageSender<T extends Serializable> implements IMessageSender<T> {
	/** Logging */
	private final static Logger log = LoggerFactory.getLogger(InprocMessageSender.class);

	/**
	 * ZMQ.Context to use for the connection.
	 */
	private final ZMQContext context;

	/**
	 * ZMQ.Socket to communicate with.
	 */
	private ZMQ.Socket socket = null;

	/**
	 * Connection string.
	 */
	private final String CONNECTION_STRING;

	/**
	 * 
	 * Creates new sender to the specified queue.
	 * 
	 * @param context
	 *          {@link org.jeromq.ZMQ.Context} to use
	 * @param queue
	 *          name of the queue to connect to
	 */
	public InprocMessageSender(final ZMQContext context, final String queue) {
		this.context = context;

		CONNECTION_STRING = Messaging.createInprocConnection(queue);
	}

	/**
	 * Connects the sender to the queue.
	 * 
	 * @throws cz.cuni.mff.d3s.been.mq.MessagingException
	 *           when connection cannot be established.
	 */
	public void connect() throws MessagingException {
		if (socket == null) {
			socket = context.socket(ZMQ.PUSH);
			boolean connected = socket.connect(CONNECTION_STRING);

			if (!connected) {
				String msg = String.format("Cannot connect to %s", CONNECTION_STRING);
				throw new MessagingException(msg);
			}
		}
	}

	/**
	 * Disconnects the sender from the queue. Subsequent calls to
	 * {@link #send(java.io.Serializable)} will throw an exception.
	 */
	@Override
	public void close() {
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}

	/**
	 * 
	 * Sends an object to a receiver(s).
	 * 
	 * Must call {@link #connect()} before sending any objects.
	 * 
	 * @param object
	 *          Serializable object to send
	 * @throws cz.cuni.mff.d3s.been.mq.MessagingException
	 *           when the object cannot be send
	 */
	@Override
	public void send(final T object) throws MessagingException {
		checkIsConnected();
		try {
			byte[] bytes = SerializationUtils.serialize(object);
			boolean sent = socket.send(bytes);

			if (!sent) {
				String msg = String.format("Cannot send {} to {}", object, CONNECTION_STRING);
				log.error(msg);
				throw new MessagingException(msg);
			}
		} catch (SerializationException e) {
			String msg = String.format("Cannot send {} to {}", object, CONNECTION_STRING);
			log.error(msg, e);
			throw new MessagingException(msg, e);
		}
	}

	/**
	 * Checks if the sender is properly connected.
	 * 
	 * @throws cz.cuni.mff.d3s.been.mq.MessagingException
	 *           if the sender is not connected
	 */
	private void checkIsConnected() throws MessagingException {
		if (socket == null) {
			throw new MessagingException(String.format("Not connected to %s!", CONNECTION_STRING));
		}
	}

	@Override
	public String getConnection() {
		return CONNECTION_STRING;
	}

	@Override
	public void setLinger(int linger) {
		socket.setLinger(linger);
	}

}
