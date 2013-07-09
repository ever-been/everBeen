package cz.cuni.mff.d3s.been.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import cz.cuni.mff.d3s.been.core.persistence.Entity;

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

	public LogMessage(String name, int level, String message) {
		this.level = level;
		this.message = message;
		this.name = name;
	}

	public LogMessage withThroable(Throwable t) {
		if (t != null) {
			try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
				t.printStackTrace(pw);
				errorTrace = sw.toString();
			} catch (IOException e) {
				// quell, what else to do?
			}

		}

		return this;
	}

	public LogMessage withTimestamp() {
		setCreated(System.currentTimeMillis());
		return this;
	}

	public LogMessage withThreadName() {
		setThreadName(Thread.currentThread().getName());
		return this;
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

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

}
