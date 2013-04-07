package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin Sixta
 */
public class TcpMessageSender<T extends Serializable> implements IMessageSender<T> {
	/** Logging */
	private final static Logger log = LoggerFactory.getLogger(TcpMessageSender.class);

	/**
	 * ZMQ.Context to use for the connection.
	 */
	private final ZMQ.Context context;

	/**
	 * ZMQ.Socket to communicate with.
	 */
	private ZMQ.Socket socket = null;

	/**
	 * Connection string.
	 */
	private final String CONNECTION_STRING;

	public TcpMessageSender(ZMQ.Context context, String queue) {
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
				throw new MessagingException(String.format(
						"Cannot send {} to {}",
						object,
						CONNECTION_STRING));
			}
		} catch (SerializationException e) {
			throw new MessagingException(String.format(
					"Cannot send {} to {}",
					object,
					CONNECTION_STRING), e);
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
			throw new MessagingException(String.format(
					"Not connected to %s!",
					CONNECTION_STRING));
		}
	}

	@Override
	public String getConnection() {
		return CONNECTION_STRING;
	}
}
