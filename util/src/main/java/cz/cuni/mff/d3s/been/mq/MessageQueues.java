package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.d3s.been.annotation.ThreadSafe;

/**
 * 
 * Experimental interface for accessing queues.
 * 
 * The idea is to gain access to message queues (inproc, tcp) by a known name
 * instead of passing references around.
 * 
 * It's a singleton.
 * 
 * 
 * @author Martin Sixta
 */
@ThreadSafe
public class MessageQueues {
	/**
	 * The singleton.
	 */
	private static MessageQueues ourInstance = new MessageQueues();

	/**
	 * Stores mapping to queues.
	 */
	private final Map<String, IMessageQueue> queues;

	/**
	 *
	 */
	private final Map<String, IMessageSender> defaultSenders;

	/**
	 * Private constructor, it's a singleton.
	 */
	private MessageQueues() {
		this.queues = new HashMap<>();
		this.defaultSenders = new HashMap<>();
	}

	/**
	 * Returns the only one and true MessageQueues object.
	 * 
	 * @return the singleton
	 */
	public static MessageQueues getInstance() {
		return ourInstance;
	}

	/**
	 * Creates named intra-procedural message queue (communication within the
	 * bounds of one process).
	 * 
	 * @param queueName
	 *          name of the qeueue
	 * @param <T>
	 *          type of messages
	 * @return Inproc message queue
	 * @throws MessagingException
	 *           if the queue cannot be created
	 */
	public synchronized
			<T extends Serializable>
			IMessageQueue<T>
			createInprocQueue(String queueName) throws MessagingException {
		if (queues.containsKey(queueName)) {
			String errorMsg = String.format("Queue %s already exists", queueName);
			throw new MessagingException(errorMsg);
		}

		IMessageQueue<T> queue = Messaging.createInprocQueue(queueName);

		queues.put(queueName, queue);

		return queue;

	}

	/**
	 * Creates named tcp message queue.
	 * 
	 * @param queueName name of the queue
	 * @return tcp message queue
	 * @throws MessagingException
	 *           When the queue already exists or if it cannot be created
	 */
	public synchronized IMessageQueue<String> createTcpQueue(String queueName, String hostname) throws MessagingException {
		if (queues.containsKey(queueName)) {
			String errorMsg = String.format("Queue %s already exists", queueName);
			throw new MessagingException(errorMsg);
		}

		IMessageQueue<String> queue = Messaging.createTcpQueue(hostname);

		queues.put(queueName, queue);

		return queue;

	}

	/**
	 * Creates sender associated with a named queue.
	 * 
	 * @param queueName
	 *          name of the queue
	 * @param <T>
	 *          type of messages
	 * @return sender The sender
	 *
	 * @throws MessagingException If the desired queue is not initialized
	 */
	public synchronized
			<T extends Serializable>
			IMessageSender<T>
			createSender(String queueName) throws MessagingException {
		if (!queues.containsKey(queueName)) {
			String errorMsg = String.format("Queue %s does not exist", queueName);
			throw new MessagingException(errorMsg);
		}

		return queues.get(queueName).createSender();

	}

	/**
	 * Returns receiver associated with a named queue.
	 * 
	 * @param queueName name of the queue
	 * @param <T> Type of messages this receiver will be using
	 *
	 * @return receiver The receiver
	 *
	 * @throws MessagingException When the desired queue is not initialized
	 */
	public synchronized
			<T extends Serializable>
			IMessageReceiver<T>
			getReceiver(String queueName) throws MessagingException {
		if (!queues.containsKey(queueName)) {
			String errorMsg = String.format("Queue %s does not exist", queueName);
			throw new MessagingException(errorMsg);
		}

		return queues.get(queueName).getReceiver();
	}

	/**
	 * Terminates a named queue.
	 * 
	 * @param queueName Name of the queue to terminate
	 *
	 * @throws MessagingException If the desired queue is not initialized
	 */
	public synchronized void terminate(String queueName) throws MessagingException {
		if (!queues.containsKey(queueName)) {
			String errorMsg = String.format("Queue %s does not exist", queueName);
			throw new MessagingException(errorMsg);
		}

		final IMessageSender sender = defaultSenders.get(queueName);

		if (sender != null) {
			sender.close();
			defaultSenders.remove(queueName);
		}

		queues.get(queueName).terminate();
		queues.remove(queueName);

	}

	/**
	 * Sends an object to a named queue.
	 * 
	 * WARNING: This method is not very fast since it is synchronized, use with
	 * care.
	 * 
	 * @param queueName
	 *          name of a queue
	 * @param serializable
	 *          object to send
	 * @throws MessagingException If the desired queue is not initialized
	 */
	public synchronized void send(String queueName, Serializable serializable) throws MessagingException {
		if (!queues.containsKey(queueName)) {
			if (!queues.containsKey(queueName)) {
				String errorMsg = String.format("Queue %s does not exist", queueName);
				throw new MessagingException(errorMsg);
			}
		}

		if (!defaultSenders.containsKey(queueName)) {
			defaultSenders.put(queueName, createSender(queueName));
		}

		final IMessageSender sender = defaultSenders.get(queueName);

		sender.send(serializable);
	}

}
