package cz.cuni.mff.d3s.been.mq;

import org.jeromq.ZMQ;

/**
 * Message sender for a task to send messages to its Host Runtime.
 * 
 * @author Martin Sixta
 */
public class TaskMessageSender implements IMessageSender<String> {
	private final ZMQ.Context context;
	private final String connection;
	private ZMQ.Socket socket;

	public TaskMessageSender(ZMQ.Context context, String connection) {
		this.context = context;
		this.connection = connection;
	}

	@Override
	public void send(String object) throws MessagingException {
		checkIsConnected();

		boolean sent = socket.send(object);

		if (!sent) {
			String msg = String.format("Cannot send {} to {}", object, connection);
			throw new MessagingException(msg);
		}

	}

	@Override
	public String getConnection() {
		return connection;
	}

	@Override
	public void close() {
		if (socket != null) {
			socket.close();
			socket = null;
		}
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
			boolean connected = socket.connect(connection);

			if (!connected) {
				String msg = String.format("Cannot connect to %s", connection);
				throw new MessagingException(msg);
			}
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
			throw new MessagingException(String.format("Not connected to %s!", connection));
		}
	}
}
