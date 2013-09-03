package cz.cuni.mff.d3s.been.mq;

/**
 * TCP-based message queue
 * 
 * @author Martin Sixta
 */
public class TcpMessageQueue implements IMessageQueue<String> {

	/**
	 * The context used to create sockets.
	 */
	private final ZMQContext context;

	/**
	 * The singleton receiver.
	 */
	private final TcpMessageReceiver receiver;

	/**
	 * Creates a tcp-based queue listening on a host and random port
	 * 
	 * @param host Host to listen on
	 */
	public TcpMessageQueue(String host) {
		this.context = Context.getReference();
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
	public IMessageSender<String> createSender() throws MessagingException {
		TcpMessageSender sender = getReceiver().createSender();
		sender.connect();

		return sender;
	}

	@Override
	public void terminate() throws MessagingException {
		receiver.close();
		context.term();
	}
}
