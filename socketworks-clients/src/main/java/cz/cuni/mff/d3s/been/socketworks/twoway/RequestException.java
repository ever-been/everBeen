package cz.cuni.mff.d3s.been.socketworks.twoway;

/**
 * 
 * Exception to indicate an error while doing requests.
 * 
 * @author Martin Sixta
 */
public class RequestException extends RuntimeException {

	public RequestException() {
		super();
	}

	public RequestException(String message) {
		super(message);
	}

	public RequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public RequestException(Throwable cause) {
		super(cause);
	}

	protected RequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
