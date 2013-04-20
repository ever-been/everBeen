package cz.cuni.mff.d3s.been.taskapi;

/**
 * 
 * Exception to indicate an error while doing requests.
 * 
 * @author Martin Sixta
 */
public class RequestException extends RuntimeException {
	/** {@inheritDoc} */
	public RequestException() {
		super();
	}
	/** {@inheritDoc} */
	public RequestException(String message) {
		super(message);
	}

	/** {@inheritDoc} */
	public RequestException(String message, Throwable cause) {
		super(message, cause);
	}

	/** {@inheritDoc} */
	public RequestException(Throwable cause) {
		super(cause);
	}

	/** {@inheritDoc} */
	protected RequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
