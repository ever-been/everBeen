package cz.cuni.mff.d3s.been.taskapi.mq;

import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.HR_COMM_PORT;

import org.jeromq.ZMQ;

/**
 * 
 * Class for inter-process messaging between tasks and Host Runtimes.
 * 
 * Part of the Java BEEN Task API.
 * 
 * @author Martin Sixta
 */
public class Messages {

	/**
	 * ZMQ.Context
	 */
	private volatile static ZMQ.Context context;

	/**
	 * Socket to send messages to Host Runtime. Singleton protected with
	 * double-checked locking.
	 */

	private volatile static ZMQ.Socket sender;

	/**
	 * Protocol used to exchange messages.
	 */
	public static String SINK_PROT = "tcp";

	/**
	 * Host to send messages to. Defaults to localhost.
	 */
	public static String SINK_HOST = "localhost";

	/**
	 * Lock object. Used in double-checked locking for {@link ZMQ.Socket}
	 * singleton.
	 */
	private static String __lock = "LOCK_OBJECT";

	/**
	 * System property name with the port on which the Host Runtime listens.
	 */

	/**
	 * 
	 * Returns {@link ZMQ.Socket} where to send messages to the Host Runtime.
	 * 
	 * @return writable socket connected to host runtime
	 */
	public static ZMQ.Socket getSocketToHostRuntime() {
		// double locking
		if (sender == null) {
			synchronized (__lock) {
				if (sender == null) {
					context = ZMQ.context();
					sender = context.socket(ZMQ.PUSH);

					int SINK_PORT = getHostRuntimePort();

					String SINK_CONN = String.format("%s://%s:%d", SINK_PROT, SINK_HOST, SINK_PORT);

					boolean connected = sender.connect(SINK_CONN);

					if (!connected) {
						// TODO sixtam Proper Exception
						throw new IllegalStateException("Cannot bind to a port: " + SINK_PORT);
					}
				}
			}
		}

		return sender;
	}

	/**
	 * Closes all opened socket connections to host runtime
	 */
	public synchronized static void shutdown() {
		if (context != null) {
			if (sender != null) {
				sender.close();
				sender = null;
			}
			context.term();
			context = null;
		}
	}

	private static int getHostRuntimePort() {
		int port = -1;
		try {
			port = Integer.valueOf(System.getenv(HR_COMM_PORT));
		} catch (Exception e) {
			// TODO sixtam Proper Exception
			throw new IllegalStateException(e);
		}

		return port;
	}

}
