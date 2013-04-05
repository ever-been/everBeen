package cz.cuni.mff.d3s.been.task;

/**
 * 
 * @author Martin Sixta
 */
final class TaskActionException extends RuntimeException {

	/** {@inheritDoc} */
	public TaskActionException() {
		super();
	}
	/** {@inheritDoc} */
	public TaskActionException(String message) {
		super(message);
	}

	/** {@inheritDoc} */
	public TaskActionException(String message, Throwable cause) {
		super(message, cause);
	}

	/** {@inheritDoc} */
	public TaskActionException(Throwable cause) {
		super(cause);
	}

	/** {@inheritDoc} */
	protected TaskActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
