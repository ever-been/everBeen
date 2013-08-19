package cz.cuni.mff.d3s.been.manager.selector;

/**
 * @author Martin Sixta
 */
public class NoRuntimeFoundException extends Exception {

	public NoRuntimeFoundException() {
		super();
	}

	public NoRuntimeFoundException(String message) {
		super(message);
	}

	public NoRuntimeFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoRuntimeFoundException(Throwable cause) {
		super(cause);
	}

	protected NoRuntimeFoundException(
			String message,
			Throwable cause,
			boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
