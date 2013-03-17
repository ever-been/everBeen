package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.taskapi.mq.Messages;
import org.jeromq.ZMQ;

import static cz.cuni.mff.d3s.been.core.TaskMessageType.LOG_MESSAGE;
import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.TASK_ID;

/**
 * @author Kuba Brecka
 */
public class TaskLogger extends TaskLoggerBase {

	private static String senderId;

	static {
		// TODO sixtam Handle property not set
		senderId = System.getenv(TASK_ID);
	}

	/**
	 *
	 * Logs messages to the Host Runtime.
	 *
	 * @param level
	 * @param message
	 * @param t
	 *
	 * @author Martin Sixta
	 */
	void log(int level, String message, Throwable t) {
		try {
			ZMQ.Socket socket = Messages.getSocketToHostRuntime();

			LogMessage logMsg = new LogMessage(level, message, t, senderId);

			String serializedMsg = JSONUtils.serialize(logMsg);

			socket.send(LOG_MESSAGE.toString() + "#" + serializedMsg);

		} catch (Exception e) {
			// TODO sixtm Proper Exception Handling
			e.printStackTrace();
		}
	}
}
