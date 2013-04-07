package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Tcp-based message receiver.
 * 
 * @author Martin Sixta
 */
public class TcpMessageReceiver<T extends Serializable> implements IMessageReceiver<T> {

	/** Logging */
	private static Logger log = LoggerFactory.getLogger(TcpMessageReceiver.class);

	/**
	 * ZMQ.Context to use for the connection.
	 */
	private final ZMQ.Context context;

	/**
	 * Name of the host.
	 */
	private final String host;

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

	TcpMessageReceiver(final ZMQ.Context context, String host) {
		this.context = context;
		this.host = host;

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

	public void bind() throws MessagingException {
		if (isConnected()) {
			return; // already connected
		}

		socket = context.socket(ZMQ.PULL);
		port = socket.bindToRandomPort(connection);

		if (port <= 0) {
			socket = null;
			throw new MessagingException("Cannot bind socket.");
		}

		log.debug("TcpMessageReceiver is bound to address {} ", connection);

	}

	@Override
	public T receive() throws MessagingException {
		if (!isConnected()) {
			throw new MessagingException("Receive on unbound socket.");
		}

		Object netObject = null;
		try {
			byte[] bytes = socket.recv();
			netObject = SerializationUtils.deserialize(bytes);
		} catch (SerializationException e) {
			throw new MessagingException("Failed to deserialize marshalled object.", e);
		}
		try {
			return (T) netObject;
		} catch (SerializationException e) {
			throw new MessagingException("Cannot cast to a proper type.", e);
		}
	}
	public TcpMessageSender<T> createSender() {
		return new TcpMessageSender<>(context, host);
	}
}
