package cz.cuni.mff.d3s.been.socketworks.oneway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.QueueGuard;

import java.net.URI;
import java.net.URL;

/**
 * Keeper object for a read-only message queue and its listener thread.
 * 
 * @author darklight
 */
class ReadOnlyGuard implements QueueGuard {

	private static final Logger log = LoggerFactory.getLogger(ReadOnlyGuard.class);

	/**
	 * THIS MESSAGE STRING IS FOR PRIVATE USE ONLY. Its purpose is to poison
	 * listener threads to signal them to stop listening.
	 */
	static final String STOP_MESSAGE = "XX1456123_STOP_RECEIVER_MESSAGE";

    private final String hostname;
	private final String queueName;
	private final ReadOnlyListener listener;
	private final IMessageReceiver<String> receiver;

	private ReadOnlyGuard(String hostname, String queueName, IMessageReceiver<String> receiver, ReadOnlyHandler handler) {
        this.hostname = hostname;
		this.queueName = queueName;
		this.receiver = receiver;
		this.listener = ReadOnlyListener.create(receiver, handler);
	}

	public static ReadOnlyGuard create(String hostname, String qName, ReadOnlyHandler handler) throws MessagingException {
		final IMessageReceiver<String> receiver = MessageQueues.getInstance().createTcpQueue(qName, hostname).getReceiver();
		return new ReadOnlyGuard(hostname, qName, receiver, handler);
	}

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
	public Integer getPort() {
		return receiver.getPort();
	}

    @Override
    public String getConnection() {
        return String.format("tcp://%s:%d", getHostname(), getPort());
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
			log.warn("Poisoner thread interrupted", e);
		}
	}
}
