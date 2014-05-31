package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A failsafe error object for cases when Jackson serialization of protocol objects fail.
 * @author darklight
 */
public class ErrorObject implements ProtocolObject {
	@JsonProperty("error")
	private final String error;

	@JsonCreator
	public ErrorObject(@JsonProperty("error") String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return String.format("{\"error\": \"%s\"}", error);
	}
}
