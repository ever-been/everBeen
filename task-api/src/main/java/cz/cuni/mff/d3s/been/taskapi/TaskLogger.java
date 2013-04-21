package cz.cuni.mff.d3s.been.taskapi;

import static cz.cuni.mff.d3s.been.core.TaskMessageType.LOG_MESSAGE;
import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.TASK_CONTEXT_ID;
import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.TASK_ID;
import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;

/**
 * @author Kuba Brecka
 */
public class TaskLogger extends TaskLoggerBase {

	private static String senderId;
	private static String contextId;

	private String name;

	static {
		senderId = System.getenv(TASK_ID);
		contextId = System.getenv(TASK_CONTEXT_ID);
	}

	public TaskLogger(String name) {

		this.name = name;
	}

	/**
	 * 
	 * Logs messages.
	 * 
	 * If Messaging is up the message will be sent to the Host Runtime, otherwise
	 * the message will be redirected to standard error.
	 * 
	 * @param level
	 * @param message
	 * @param t
	 * 
	 * @author Martin Sixta
	 */
	void log(int level, String message, Throwable t) {
		try {

			LogMessage logMsg = new LogMessage(name, level, message, t, senderId, contextId);
			String serializedMsg = JSONUtils.serialize(logMsg);
			String msg = String.format("%s#%s", LOG_MESSAGE.toString(), serializedMsg);

			Messages.send(msg);

		} catch (Exception e) {
			// TODO sixtm Proper Exception Handling
			e.printStackTrace();
		}
	}
}
