package cz.cuni.mff.d3s.been.socketworks.twoway;

/**
 * Exception to indicate an error while doing requests.
 * 
 * @author Martin Sixta
 */
public class RequestException extends RuntimeException {

	/**
	 * Create a request exception
	 */
	public RequestException() {
		super();
	}

	/**
	 * Create a request exception with an error message
	 *
	 * @param message Error message
	 */
	public RequestException(String message) {
		super(message);
	}

	/**
	 * Create a request exception with an error message and a cause
	 *
	 * @param message Error message
	 * @param cause Cause of this exception
	 */
	public RequestException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a request exception with a cause
	 *
	 * @param cause Cause of this exception
	 */
	public RequestException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a request exception with a suppressible stack trace
	 *
	 * @param message Error message
	 * @param cause Cause of this exception
	 * @param enableSuppression Whether stack trace suppression should be enabled
	 * @param writableStackTrace Stack trace
	 */
	protected RequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
