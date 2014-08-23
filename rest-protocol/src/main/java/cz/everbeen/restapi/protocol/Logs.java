package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;

/**
 * Protocol object representing a task's log entries
 *
 * @author darklight
 */
public class Logs implements ProtocolObject {

	@JsonProperty("messages")
	private Collection<LogEntry> messages;

	@JsonCreator
	public Logs(
			@JsonProperty("messages") Collection<LogEntry> messages
	) {
		this.messages = messages;
	}

	public Collection<LogEntry> getMessages() {
		return messages;
	}
}
