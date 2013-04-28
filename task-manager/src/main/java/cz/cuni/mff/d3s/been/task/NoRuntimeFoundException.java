package cz.cuni.mff.d3s.been.task;

/**
 * @author Martin Sixta
 */
public class NoRuntimeFoundException extends Exception {
	/** {@inheritDoc} */
	public NoRuntimeFoundException() {
		super();
	}
	/** {@inheritDoc} */
	public NoRuntimeFoundException(String message) {
		super(message);
	}

	/** {@inheritDoc} */
	public NoRuntimeFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/** {@inheritDoc} */
	public NoRuntimeFoundException(Throwable cause) {
		super(cause);
	}

	/** {@inheritDoc} */
	protected NoRuntimeFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
