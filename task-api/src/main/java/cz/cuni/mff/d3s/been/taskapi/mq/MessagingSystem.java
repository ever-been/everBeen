package cz.cuni.mff.d3s.been.taskapi.mq;

import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.HR_COMM_PORT;

/**
 * 
 * Utility class for inter-process messaging between tasks and Host Runtimes.
 * 
 * Part of the Java BEEN Task API.
 * 
 * To use the messaging system first {@link #connect()} must be called, then you
 * can obtain reference via {@link #getMessaging()}, when you are done call
 * {@link #disconnect()}.
 * 
 * Since proper initialization and terminating of the messaging system would be
 * too complicated and deadlock-prone the responsibility is handled to the user
 * (who may not to use the system at all).
 * 
 * 
 * <b>WARNING</b> To be able to log messages to a Host Runtime the messaging
 * system MUST be connected.
 * 
 * <b>WARNING</b> When you are done you MUST call {@link #disconnect()}
 * otherwise the task will not finish!
 * 
 * 
 * @author Martin Sixta
 */
public class MessagingSystem {

	/**
	 * Implementation of the messaging system.
	 */
	private static volatile MessagingImpl messaging;

	/**
	 * Protocol used to exchange messages.
	 */
	private static String SINK_PROTO = "tcp";

	/**
	 * Host to send messages to. Defaults to localhost.
	 */
	private static String SINK_HOST = "localhost";

	/**
	 * Port on which the host listens.
	 */
	private static int SINK_PORT = -1;

	static {
		try {
			SINK_PORT = Integer.valueOf(System.getenv(HR_COMM_PORT));
		} catch (Exception e) {
			// quell the exception, connect will not work.
		}
	}

	/**
	 * Tells if the messaging system is running.
	 * 
	 * @return true if messaging system is running
	 */
	public static boolean isConnected() {
		return (messaging != null);
	}

	/**
	 * Set-ups the messaging system.
	 */
	public static synchronized void connect() {
		if (messaging != null) {
			return;
		}

		if (SINK_PORT <= 0) {
			throw new IllegalStateException("Cannot connect. Unknown port!");
		}

		messaging = new MessagingImpl(SINK_PROTO, SINK_HOST, SINK_PORT);

	}

	/**
	 * Terminates the messaging system.
	 */
	public static synchronized void disconnect() {
		if (messaging != null) {
			messaging.disconnect();
			messaging = null;
		}
	}

	public static synchronized Messaging getMessaging() throws IllegalStateException {
		if (messaging == null) {
			throw new IllegalArgumentException("Messaging system is not running! Call connect() first!");
		}

		return messaging;
	}

}
