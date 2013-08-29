package cz.cuni.mff.d3s.been.util;

/**
 * @author Martin Sixta
 */
@SuppressWarnings("serial")
public class JsonException extends Exception {

	public JsonException() {
		super();
	}

	public JsonException(String message) {
		super(message);
	}

	public JsonException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonException(Throwable cause) {
		super(cause);
	}

	protected JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
