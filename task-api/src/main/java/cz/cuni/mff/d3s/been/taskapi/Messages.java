package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.Messaging;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.NamedSockets;

/**
 * Set of functions for a task to easily send messages to it's Host Runtime.
 * 
 * For simple usage {@link #send(String)} will suffice.
 * 
 * If you need more senders (ie. multiply threads) create them with
 * {@link #createHRSender()}.
 * 
 * @author Martin Sixta
 */
final class Messages {

	private static IMessageQueue<String> taskMessageQueue = null;
	private static IMessageSender<String> defaultSender = null;

	/**
	 * Creates independent sender. Such a sender can be used in a different thread
	 * for a task with such needs.
	 * 
	 * @return The sender
	 * 
	 * @throws MessagingException
	 *           When the sender cannot be created (queue reference broken)
	 */
	public static synchronized IMessageSender<String> createHRSender() throws MessagingException {
		if (taskMessageQueue == null) {
			taskMessageQueue = Messaging.createTaskQueue(NamedSockets.TASK_LOG_0MQ.getConnection());
		}

		return taskMessageQueue.createSender();

	}

	public static synchronized void terminate() throws MessagingException {
		if (defaultSender != null) {
			defaultSender.close();
		}

		if (taskMessageQueue != null) {
			taskMessageQueue.terminate();
		}
	}

	/**
	 * 
	 * Sends a message to the Host Runtime.
	 * 
	 * The method is synchronized, if you send a lot messages consider creating a
	 * sender with {@link #createHRSender()}.
	 * 
	 * @param msg
	 *          Message to send
	 * 
	 * @throws MessagingException
	 *           On transport error when sending
	 */
	public static synchronized void send(String msg) throws MessagingException {
		if (defaultSender == null) {
			defaultSender = createHRSender();
		}

		defaultSender.send(msg);

	}
}
