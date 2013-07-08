package cz.cuni.mff.d3s.been.core;

import cz.cuni.mff.d3s.been.core.persistence.Entity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Message used to log events on tasks. Java Task API.
 * 
 * @author Martin Sixta
 */
public class LogMessage extends Entity {

	/** Name of the logger */
	private String name;

	/** Log level */
	private int level;

	/** Message to log */
	private String message;

	/** Error trace in String form */
	private String errorTrace;

	/** ID of the logging Task */
	private String senderId;

	/** ID of the context the task is associated with */
	private String contextId;

	/** Name of the thread which logged the message. */
	private String threadName;

	/**
	 * Time when the log was issued in milliseconds as returned by
	 * System.currentTimeMillis()
	 */
	private long time;

	public LogMessage() {
		// make JSON deserializer happy
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getErrorTrace() {
		return errorTrace;
	}

	public void setErrorTrace(String errorTrace) {
		this.errorTrace = errorTrace;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
