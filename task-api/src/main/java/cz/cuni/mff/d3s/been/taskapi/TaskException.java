package cz.cuni.mff.d3s.been.taskapi;

/**
 * 
 * General exception for task to throw.
 * 
 * Provided for convenience.
 * 
 * @author Martin Sixta
 */
public class TaskException extends Exception {
	/** {@inheritDoc} */
	public TaskException() {
		super();
	}

	/** {@inheritDoc} */
	public TaskException(String message) {
		super(message);
	}

	/** {@inheritDoc} */
	public TaskException(String message, Throwable cause) {
		super(message, cause);
	}

	/** {@inheritDoc} */
	public TaskException(Throwable cause) {
		super(cause);
	}

	/** {@inheritDoc} */
	protected TaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
