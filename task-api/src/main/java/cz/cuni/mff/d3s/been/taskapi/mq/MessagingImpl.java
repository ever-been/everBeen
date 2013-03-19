package cz.cuni.mff.d3s.been.taskapi.mq;

import org.jeromq.ZMQ;

/**
 * 
 * Messaging system implementation.
 * 
 * @author Martin Sixta
 */
final class MessagingImpl implements Messaging {

	private ZMQ.Socket sender;
	private ZMQ.Context context;
	private String SINK_CONN;

	MessagingImpl(String proto, String host, int port) {
		context = ZMQ.context();
		sender = context.socket(ZMQ.PUSH);

		SINK_CONN = String.format("%s://%s:%d", proto, host, port);

		boolean connected = sender.connect(SINK_CONN);

		if (!connected) {
			// TODO sixtam Proper Exception
			throw new IllegalStateException("Cannot bind to " + SINK_CONN);
		}
	}

	@Override
	public void send(String msg) throws IllegalStateException {
		boolean msgSent = sender.send(msg);

		if (!msgSent) {
			throw new IllegalStateException("Task Messaging system has already terminated!");
		}
	}

	public void disconnect() {
		sender.close();
		context.term();
	}
}
