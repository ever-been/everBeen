package cz.cuni.mff.d3s.been.util;

/**
 * @author Martin Sixta
 */
@SuppressWarnings("serial")
public class JsonException extends Exception {

	/**
	 * Create a JSON exception
	 */
	public JsonException() {
		super();
	}

	/**
	 * Create a JSON exception with an error message
	 *
	 * @param message Error message
	 */
	public JsonException(String message) {
		super(message);
	}

	/**
	 * Create a JSON exception with an error message and a cause
	 *
	 * @param message Error message
	 * @param cause Cause of this exception
	 */
	public JsonException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a JSON exception with a cause
	 *
	 * @param cause Cause of this exception
	 */
	public JsonException(Throwable cause) {
		super(cause);
	}

	protected JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
