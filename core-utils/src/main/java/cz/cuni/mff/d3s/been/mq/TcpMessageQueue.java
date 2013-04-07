package cz.cuni.mff.d3s.been.mq;

import org.jeromq.ZMQ;

/**
 * TCP-based message queue
 * 
 * @author Martin Sixta
 */
public class TcpMessageQueue implements IMessageQueue<String> {

	/**
	 * The context used to create sockets.
	 */
	private final ZMQ.Context context;

	/**
	 * The singleton receiver.
	 */
	private final TcpMessageReceiver receiver;

	/**
	 * Creates a tcp-based queue listening on a host and random port
	 * 
	 * @param host
	 */
	public TcpMessageQueue(String host) {
		this.context = ZMQ.context();
		this.receiver = new TcpMessageReceiver(context, host);
	}

	@Override
	public TcpMessageReceiver getReceiver() throws MessagingException {
		if (!receiver.isConnected()) {
			receiver.bind();
		}
		return receiver;
	}

	@Override
	public IMessageSender createSender() throws MessagingException {
		TcpMessageSender sender = getReceiver().createSender();
		sender.connect();

		return sender;
	}

	@Override
	public void terminate() {
		context.term();
	}
}
