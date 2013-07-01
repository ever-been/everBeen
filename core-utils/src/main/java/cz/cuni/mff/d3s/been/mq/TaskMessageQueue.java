package cz.cuni.mff.d3s.been.mq;

/**
 * 
 * Message queue for a task to communicate with it's Host Runtime.
 * 
 * WARNING: it can only send messages, not receive! {@link #getReceiver()} will
 * throw runtime exception. This is for tasks needs!
 * 
 * @author Martin Sixta
 */
final class TaskMessageQueue implements IMessageQueue<String> {

	private ZMQContext context;
	private final String SINK_CONN;
	private final String SINK_CONN_FORMAT = "tcp://%s:%d";

    TaskMessageQueue(String url) {
        SINK_CONN = url;
    }

	TaskMessageQueue(String host, int port) {
		context = Context.getReference();
		SINK_CONN = String.format(SINK_CONN_FORMAT, host, port);
	}

	@Override
	public IMessageReceiver<String> getReceiver() throws MessagingException {
		throw new UnsupportedOperationException("Cannot receive messages!");
	}

	@Override
	public IMessageSender<String> createSender() throws MessagingException {
		TaskMessageSender sender = new TaskMessageSender(context, SINK_CONN);
		sender.connect();

		return sender;
	}

	@Override
	public void terminate() throws MessagingException {
		context.term();
	}

}
