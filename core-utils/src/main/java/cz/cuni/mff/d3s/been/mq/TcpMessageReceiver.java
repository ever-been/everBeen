package cz.cuni.mff.d3s.been.mq;

import org.jeromq.ZMQ;
import org.jeromq.ZMQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Tcp-based message receiver.
 * 
 * @author Martin Sixta
 */
public class TcpMessageReceiver implements IMessageReceiver<String> {

	/** Logging */
	private static Logger log = LoggerFactory.getLogger(TcpMessageReceiver.class);

	/**
	 * ZMQ.Context to use for the connection.
	 */
	private final ZMQContext context;

	/**
	 * ZMQ.Socket to communicate with.
	 */
	private ZMQ.Socket socket;

	/**
	 * Connection string.
	 */
	private final String connection;

	/**
	 * Port the receiver is bind to.
	 */
	private int port;

	TcpMessageReceiver(final ZMQContext context, String host) {
		this.context = context;
		this.connection = Messaging.createTcpConnection(host);
	}

	@Override
	public boolean isConnected() {
		return socket != null;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public synchronized void close() {
		if (isConnected()) {
			socket.close();
			socket = null;
		}
	}

	public void bind() throws MessagingException {
		if (isConnected()) {
			return; // already connected
		}

		socket = context.socket(ZMQ.PULL);
		final PortRange range = RandomPortRangePicker.getRange();
		port = socket.bindToRandomPort(connection, range.getFrom(), range.getTo());

		if (port <= 0) {
			socket = null;
			throw new MessagingException("Cannot bind socket.");
		}

		log.debug("TcpMessageReceiver is bound to address {}:{} ", connection, port);

	}

	@Override
	public String receive() throws MessagingException {
		if (!isConnected()) {
			throw new MessagingException("Receive on unbound socket.");
		}

		try {
			byte[] bytes = socket.recv();
			return new String(bytes);
		} catch (ZMQException e) {
			throw new MessagingException("Receive failed", e);
		}

	}

	public TcpMessageSender createSender() {
		return new TcpMessageSender(context, String.format("%s:%d", connection, port));
	}

}
