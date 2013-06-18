package cz.cuni.mff.d3s.been.taskapi;

import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.HR_COMM_PORT;
import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.Messaging;
import cz.cuni.mff.d3s.been.mq.MessagingException;

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
public final class Messages {

	private static IMessageQueue<String> taskMessageQueue = null;
	private static IMessageSender<String> defaultSender = null;
	private static int hostRuntimePort;

	/**
	 * Creates independent sender. Such a sender can be used in a different thread
	 * for a task with such needs.
	 * 
	 * 
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public static synchronized IMessageSender<String> createHRSender() throws MessagingException {
		if (taskMessageQueue == null) {
			hostRuntimePort = Integer.valueOf(System.getenv(HR_COMM_PORT));
			taskMessageQueue = Messaging.createTaskQueue(hostRuntimePort);
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
	 * @throws MessagingException
	 */
	public static synchronized void send(String msg) throws MessagingException {
		if (defaultSender == null) {
			defaultSender = createHRSender();
		}

		defaultSender.send(msg);

	}
}
