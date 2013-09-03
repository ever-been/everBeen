package cz.cuni.mff.d3s.been.logging;

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
	private String name;

	/** Log level */
	private int level;

	/** Message to log */
	private String message;

	/** Error trace in String form */
	private String errorTrace;

	/** Name of the thread which logged the message. */
	private String threadName;

	/** Time at which this message was logged */
	private long time;

	/** Creates new LogMessage */
	public LogMessage() {
		// make JSON deserializer happy
	}

	/**
	 * Creates new LogMessage.
	 * 
	 * @param name
	 *          name of the logger
	 * @param level
	 *          log level
	 * @param message
	 *          message associated with the LogMessage
	 */
	public LogMessage(String name, int level, String message) {
		this.level = level;
		this.message = message;
		this.name = name;
	}

	/**
	 * Sets Throwable for the message
	 * 
	 * @param t
	 *          the Throwable
	 * @return this object
	 */
	public LogMessage withThrowable(Throwable t) {
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

	/**
	 * Sets the current Thread name
	 * 
	 * @return this object
	 */
	public LogMessage withThreadName() {
		setThreadName(Thread.currentThread().getName());
		return this;
	}

	/**
	 * Returns name associated with the LogMessage
	 * 
	 * @return name associated with the LogMessage
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name associated with the LogMessage.
	 * 
	 * @param name
	 *          the name which should be associated with the LogMessage
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the message associated with the LogMessage
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message
	 * 
	 * @param message
	 *          the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns LogMessage level
	 * 
	 * @return log level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets log level
	 * 
	 * @param level
	 *          levet to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Returns the error trace
	 * 
	 * @return associated error trace or null
	 */
	public String getErrorTrace() {
		return errorTrace;
	}

	/**
	 * Sets error trace associated with the LogMessage
	 * 
	 * @param errorTrace
	 *          the trace to set
	 */
	public void setErrorTrace(String errorTrace) {
		this.errorTrace = errorTrace;
	}

	/**
	 * Returns the thread name associated with the LogMessage.
	 * 
	 * @return the thread name associated with the LogMessage
	 * 
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * Sets the thread name associated with the LogMessage
	 * 
	 * @param threadName
	 *          the thread name to be associated with the LogMessage
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	/**
	 * Returns formatted string of the message
	 * 
	 * @return Formatted string of the message
	 */
	public String toDownloadableString() {
		return String.format("[%s %s] (%s) %s %s", name, threadName, level, message, errorTrace);
	}
}
