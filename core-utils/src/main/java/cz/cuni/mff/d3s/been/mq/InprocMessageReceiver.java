package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;

/**
 * Receiver for a queue in inter-process communication.
 * 
 * The queue can send/receive only one type of an object, specified by the type
 * parameter.
 * 
 * We do not care who are the senders. The receiver's responsibility is just to
 * provide received messages to a higher level.
 * 
 * Implementation notes: Implemented using 0MQ PUSH-PULL model in 0MQ. This is
 * the PULL part.
 * 
 * @see cz.cuni.mff.d3s.been.mq.InprocMessageReceiver
 * @author Martin Sixta
 */
@NotThreadSafe
final class InprocMessageReceiver<T extends Serializable> implements IMessageReceiver<T> {
	/** Logging */
	private static Logger log = LoggerFactory.getLogger(InprocMessageReceiver.class);

	/**
	 * ZMQ.Context to use for the connection.
	 */
	private final ZMQContext context;

	/**
	 * Name of the queue.
	 */
	private final String queue;

	/**
	 * ZMQ.Socket to communicate with.
	 */
	private ZMQ.Socket socket;

	/**
	 * Connection string.
	 */
	private String INPROC_CONN;

	/**
	 * Denotes success from a ZMQ.Socket.bind call
	 */
	private static final int PORT_OK = 0;

	/**
	 * Creates a new receiver
	 * 
	 * The receiver is not bind to its queue and call to {@link #bind()} is
	 * required before receiving any messages.
	 * 
	 * @param context
	 * @param queue
	 */

	public InprocMessageReceiver(final ZMQContext context, final String queue) {
		this.context = context;
		this.queue = queue;
		INPROC_CONN = Messaging.createInprocConnection(queue);
	}

	public void bind() throws MessagingException {
		if (isConnected()) {
			return; // already connected
		}

		socket = context.socket(ZMQ.PULL);
		int port = socket.bind(INPROC_CONN);

		if (port != PORT_OK) {
			socket = null;
			throw new MessagingException("Cannot bind socket.");
		}

		log.debug("IMessageReceiver is bind to address {} ", INPROC_CONN);

	}

	@Override
	public T receive() throws MessagingException {
		if (!isConnected()) {
			throw new MessagingException("Receive on unbind socket.");
		}

		try {
			byte[] bytes = socket.recv();
			Object object = SerializationUtils.deserialize(bytes);
			return (T) object;
		} catch (ClassCastException | SerializationException | IllegalArgumentException e) {
			throw new MessagingException("Cannot cast to a proper type.", e);
		}
	}

	/**
	 * Returns connection status.
	 * 
	 * @return true if the sender is connected to its queue, false otherwise
	 */
	@Override
	public boolean isConnected() {
		return socket != null;
	}

	@Override
	public int getPort() {
		return PORT_OK;
	}

	@Override
	public void close() {
		if (isConnected()) {
			socket.close();
			socket = null;
		}
	}

	public InprocMessageSender<T> createSender() {
		return new InprocMessageSender<>(context, queue);
	}

}
