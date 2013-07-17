package cz.cuni.mff.d3s.been.util;

/**
 * @author Martin Sixta
 */
@SuppressWarnings("serial")
public class JsonException extends Exception {

	/** {@inheritDoc} */
	public JsonException() {
		super();
	}
	/** {@inheritDoc} */
	public JsonException(String message) {
		super(message);
	}

	/** {@inheritDoc} */
	public JsonException(String message, Throwable cause) {
		super(message, cause);
	}

	/** {@inheritDoc} */
	public JsonException(Throwable cause) {
		super(cause);
	}

	/** {@inheritDoc} */
	protected JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
