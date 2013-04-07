package cz.cuni.mff.d3s.been.hostruntime;

import java.util.HashMap;
import java.util.Map;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.TaskMessageType;

/**
 * Purpose of this service is to allow communication between HostRuntime and
 * running tasks.
 * 
 * Usage of this class
 * 
 * @author Tadeáš Palusga
 * 
 */
public class TaskMessageDispatcher {

	static final Logger log = LoggerFactory.getLogger(TaskMessageDispatcher.class);

	/**
	 * Address on which the receiver is bound
	 */
	private static final String BIND_ADDR = "tcp://localhost";

	/**
	 * THIS MESSAGE STRING IS FOR PRIVATE USE ONLY. Purpose of this message is to
	 * kill the receiver immediately.
	 */
	static final String STOP_MESSAGE = "XX1456123_STOP_RECEIVER_MESSAGE";

	/**
	 * identification of not bounded port
	 */
	static final int NOT_BOUND_PORT = -1;

	/**
	 * Message queue context for sender and receiver.
	 */
	volatile ZMQ.Context context;

	/**
	 * Random generated port on which the receiver is running
	 */
	int receiverPort = NOT_BOUND_PORT;

	Thread msgQueueReader;

	/**
	 * Registered message listeners (key is messageType, value is listener itself)
	 */
	private Map<TaskMessageType, MessageListener> listeners = new HashMap<>();

	/**
	 * Starts new receiver thread if not already running.
	 */
	public void start() {
		context = ZMQ.context();
		final ZMQ.Socket receiver = context.socket(ZMQ.PULL);
		receiverPort = receiver.bindToRandomPort(BIND_ADDR);

		msgQueueReader = new QueueReaderThread(this, receiver);
		msgQueueReader.start();

		Runtime.getRuntime().addShutdownHook(createShutdownHook());

	}
	private Thread createShutdownHook() {
		return new Thread() {
			@Override
			public void run() {
				TaskMessageDispatcher.this.sendStopMessage();
			}
		};
	}

	/**
	 * Processes given message. Uses applicable registered message listener to
	 * process the message.
	 * 
	 * @param message
	 *          to be processed
	 */
	void processMessage(String message) {
		String messageType = getMessageType(message);
		getMessageBody(message);
		MessageListener listener = listeners.get(messageType);
		if (listener != null) {
			listener.processMessage(message);
		} else {
			// FIXME possibility to resent message into HC cluster 
		}
	}

	/**
	 * Returns message type of the given message.
	 * 
	 * @param message
	 * @return message type
	 */
	private String getMessageType(String message) {
		// TODO ...

		return null;
	}

	/**
	 * Returns message type of the given message.
	 * 
	 * @param message
	 * @return message body
	 */
	private String getMessageBody(String message) {
		// TODO ...

		return null;
	}

	/**
	 * Stop the task message listener.
	 */
	public void stop() {
		sendStopMessage();
		try {
			msgQueueReader.join();
		} catch (InterruptedException e) {
			log.warn("Interrupted while closing task message listener connections.");
		}
		context.term();
		context = null;
		receiverPort = TaskMessageDispatcher.NOT_BOUND_PORT;
	}

	/**
	 * Send a poison message to running receiver thread.
	 */
	private void sendStopMessage() {
		synchronized (this) {
			if (context != null) {
				ZMQ.Socket sender = context.socket(ZMQ.PUSH);
				sender.connect(String.format("%s:%d", BIND_ADDR, receiverPort));
				sender.send(STOP_MESSAGE);
				sender.close();
			}
		}
	}

	/**
	 * Registers given message listener. If listener of the same type (type
	 * property of listener) is already registered, it will be replaced.
	 * 
	 * @param listener
	 *          to be registered
	 * @throws NullPointerException
	 *           if the listener itself is NULL or type of the listener is NULL
	 */
	public void addMessageListener(MessageListener listener) throws NullPointerException {
		if (listener.getMessageType() == null) {
			throw new NullPointerException("Message type on listener can't be NULL");
		}
		listeners.put(listener.getMessageType(), listener);
	}

	/**
	 * Unregisters listener of given type.
	 * 
	 * @param listenerType
	 *          type of the listener to be removed
	 */
	public void removeMessageListener(TaskMessageType listenerType) {
		listeners.remove(listenerType);
	}

	/**
	 * Returns port on which the running receiver has been started
	 * 
	 * @return port on which the receiver is running
	 */
	public int getReceiverPort() {
		return receiverPort;
	}
}
