package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

/**
 * Protocol object representing a single log message
 *
 * @author darklight
 */
public class LogEntry implements ProtocolObject {

	@JsonProperty("thread")
	private final String threadName;
	@JsonProperty("time")
	private final Date timeStamp;
	@JsonProperty("message")
	private final String message;
	@JsonProperty("trace")
	private final String stackTrace;

	@JsonCreator
	public LogEntry(
			@JsonProperty("thread") String threadName,
			@JsonProperty("time") Date timeStamp,
			@JsonProperty("message") String message,
			@JsonProperty("trace") String stackTrace
	) {
		this.threadName = threadName;
		this.timeStamp = timeStamp;
		this.message = message;
		this.stackTrace = stackTrace;
	}

	public String getThreadName() {
		return threadName;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public String getStackTrace() {
		return stackTrace;
	}
}
