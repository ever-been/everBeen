package cz.cuni.mff.d3s.been.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Message used to log events on tasks. Java Task API.
 * 
 * @author Martin Sixta
 */
public class LogMessage {

	/** Name of the logger */
	public String name;

	/** Log level */
	public int level;

	/** Message to log */
	public String message;

	/** Error trace in String form */
	public String errorTrace;

	/** ID of the logging Task */
	public String senderId;

	/** ID of the context the task is associated with */
	public String contextId;

	/** Name of the thread which logged the message. */
	public String threadName;

	/**
	 * Time when the log was issued in milliseconds as returned by
	 * System.currentTimeMillis()
	 */
	public long time;

	public LogMessage(String name, int level, String message, Throwable t, String senderId, String contextId) {
		this.level = level;
		this.message = message;
		this.senderId = senderId;
		this.name = name;
		this.contextId = contextId;

		if (t != null) {
			try (StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw)) {
				t.printStackTrace(pw);
				errorTrace = sw.toString();
			} catch (IOException e) {
				// quell, what else to do?
			}

		}

		this.time = System.currentTimeMillis();
		this.threadName = Thread.currentThread().getName();

	}
}
