package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

import org.jeromq.ZMQ;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;

/**
 * 
 * Inter-process message queue.
 * 
 * @author Martin Sixta
 */
@NotThreadSafe
final class InprocMessageQueue<T extends Serializable> implements IMessageQueue<T> {
	/**
	 * The context used to create sockets.
	 */
	private final ZMQ.Context context;

	/**
	 * The singleton receiver.
	 */
	private final InprocMessageReceiver<T> receiver;

	/**
	 * Creates a named inter-process queue.
	 * 
	 * @param queueName
	 *          name of the queue
	 */
	InprocMessageQueue(String queueName) {
		this.context = Context.getReference();
		this.receiver = new InprocMessageReceiver<>(context, queueName);
	}

	@Override
	public InprocMessageReceiver<T> getReceiver() throws MessagingException {
		if (!receiver.isConnected()) {
			receiver.bind();
		}
		return receiver;
	}

	@Override
	public InprocMessageSender<T> createSender() throws MessagingException {
		InprocMessageSender<T> sender = getReceiver().createSender();
		sender.connect();

		return sender;
	}

	@Override
	public void terminate() {
		receiver.close();
		Context.releaseContext();
	}
}
