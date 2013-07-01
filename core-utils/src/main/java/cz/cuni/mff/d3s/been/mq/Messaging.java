package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

/**
 * Utility class for messaging.
 * 
 * @author Martin Sixta
 */
public final class Messaging {
	/**
	 * 0MQ protocol name for inter-process communication.
	 */
	private static final String INPROC_PROTO = "inproc";

	private static final String TCP_PROTO = "tcp";

	/**
	 * Format for connection string (protocol type, queue name)
	 */
	private static final String CONN_FORMAT = "%s://%s";

	/**
	 * Creates connection String for a named queue.
	 * 
	 * @param queue
	 *          name of the queue
	 * @return connection string which can be used in ZMQ.Socket calls.
	 */
	static String createInprocConnection(String queue) {
		return String.format(CONN_FORMAT, INPROC_PROTO, queue);
	}

	/**
	 * Creates connection String a tcp-based queue, random port
	 * 
	 * @param queue
	 *          name of the host to listen on
	 * @return connection string which can be used in ZMQ.Socket calls.
	 */
	static String createTcpConnection(String host) {
		return String.format(CONN_FORMAT, TCP_PROTO, host);
	}

	/**
	 * Creates named, inter-process message queue.
	 * 
	 * @param queue
	 *          name of the queue
	 * @param <T>
	 *          type of messages to send/receive (i.e. base class)
	 * @return named, inter-process message queue
	 */
	public static <T extends Serializable> IMessageQueue<T> createInprocQueue(
			String queue) {
		return new InprocMessageQueue<>(queue);
	}

	/**
	 * Creates tcp-based message queue listening on a random port.
	 * 
	 * Sends Strings.
	 * 
	 * 
	 * @param host
	 *          name of the host
	 * @return tcp-based message queue
	 */
	public static IMessageQueue<String> createTcpQueue(String host) {
		return new TcpMessageQueue(host);
	}

	/**
	 * 
	 * Returns IMessageQueue connecting a task to its Host Runtime.
	 * 
	 * Typically a task wants to create such a queue.
	 * 
	 * WARNING: the returned implementation does support receiving!
	 *
     * @param hostname
     *  hostname on which the Host Runtime is listening for task messages
     *
     * @param port
     *          port on which a Host Runtime listens for messages from tasks
     *
	 * @return
	 */
	public static IMessageQueue<String> createTaskQueue(String hostname, int port) {
		return new TaskMessageQueue(hostname, port);
	}

    /**
     * @see  #createTaskQueue(String, int)
     */
    public static IMessageQueue<String> createTaskQueue(String url) {
        return new TcpMessageQueue(url);
    }
}
