package cz.everbeen.restapi.model;

/**
 * A failsafe error object for cases when Jackson serialization of model objects fail.
 * @author darklight
 */
public class ErrorObject implements ModelObject {
	private final String error;

	public ErrorObject(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return String.format("{error: '%s'}", error);
	}
}
