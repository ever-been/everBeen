package cz.cuni.mff.d3s.been.mq;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin Sixta
 */
public class TcpMessageSender implements IMessageSender<String> {
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
	private final String connection;

	public TcpMessageSender(ZMQ.Context context, String connection) {
		this.context = context;
		this.connection = connection;
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
			boolean connected;
			try {
				if (Thread.currentThread().isInterrupted()) {
					connected = socket.connect(connection);
					Thread.currentThread().interrupt();
				} else {
					connected = socket.connect(connection);
				}
			} catch (IllegalArgumentException e) {
				throw new MessagingException(String.format(
						"Failed to connect to %s",
						connection), e);
			}

			if (!connected) {
				throw new MessagingException(String.format(
						"Cannot connect to %s",
						connection));
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
	public void send(final String object) throws MessagingException {
		checkIsConnected();
		boolean sent = socket.send(object);

		if (!sent) {
			String msg = String.format("Cannot send {} to {}", object, connection);
			throw new MessagingException(msg);
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
					connection));
		}
	}

	@Override
	public String getConnection() {
		return connection;
	}
}
