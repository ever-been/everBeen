package cz.cuni.mff.d3s.been.socketworks.oneway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.QueueGuard;

/**
 * Keeper object for a read-only message queue and its listener thread.
 * 
 * @author darklight
 */
public class ReadOnlyGuard implements QueueGuard {

	private static final Logger log = LoggerFactory.getLogger(ReadOnlyGuard.class);

	/**
	 * THIS MESSAGE STRING IS FOR PRIVATE USE ONLY. Its purpose is to poison
	 * listener threads to signal them to stop listening.
	 */
	static final String STOP_MESSAGE = "XX1456123_STOP_RECEIVER_MESSAGE";

	final String queueName;
	final ReadOnlyListener listener;
	final IMessageReceiver<String> receiver;

	private ReadOnlyGuard(String queueName, IMessageReceiver<String> receiver, ReadOnlyHandler handler) {
		this.queueName = queueName;
		this.receiver = receiver;
		this.listener = ReadOnlyListener.create(receiver, handler);
	}

	public static ReadOnlyGuard create(String hostName, String qName, ReadOnlyHandler handler) throws MessagingException {
		final IMessageReceiver<String> receiver = MessageQueues.getInstance().createTcpQueue(qName, hostName).getReceiver();
		return new ReadOnlyGuard(qName, receiver, handler);
	}

	@Override
	public Integer getPort() {
		return receiver.getPort();
	}

	@Override
	public void listen() {
		listener.start();
	}

	@Override
	public void terminate() throws MessagingException {
		poisonReader();
		MessageQueues.getInstance().terminate(queueName);
	}

	private void poisonReader() throws MessagingException {
		IMessageSender<String> sender = MessageQueues.getInstance().createSender(queueName);
		try {
			sender.send(STOP_MESSAGE);
		} finally {
			sender.close();
		}
		try {
			listener.join();
		} catch (InterruptedException e) {
			log.warn("Poisoner joined interrupted thread", e);
		}
	}
}
