package cz.everbeen.restapi.protocol;

/**
 * A failsafe error object for cases when Jackson serialization of protocol objects fail.
 * @author darklight
 */
public class ErrorObject implements ProtocolObject {
	private final String error;

	public ErrorObject(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return String.format("{error: '%s'}", error);
	}
}
