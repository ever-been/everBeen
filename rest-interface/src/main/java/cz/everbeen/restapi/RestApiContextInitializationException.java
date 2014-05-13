package cz.everbeen.restapi;

/**
 * An exception indicating that the initialization of the rest API context failed
 *
 * @author darklight
 */
public class RestApiContextInitializationException extends RuntimeException {

	/**
	 * Create a {@link cz.everbeen.restapi.RestApiContextInitializationException} with a message and a cause
	 * @param message Describing message
	 * @param cause Cause of this exception
	 */
	public RestApiContextInitializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
