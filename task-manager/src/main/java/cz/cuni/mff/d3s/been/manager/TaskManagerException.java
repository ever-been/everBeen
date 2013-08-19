package cz.cuni.mff.d3s.been.manager;

/**
 * High-level exception for all things that can go wrong inside TaskManager.
 * 
 * @author Martin Sixta
 */
public class TaskManagerException extends Exception {

	/** {@inheritDoc} */
	public TaskManagerException() {
		super();
	}
	/** {@inheritDoc} */
	public TaskManagerException(String message) {
		super(message);
	}

	/** {@inheritDoc} */
	public TaskManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	/** {@inheritDoc} */
	public TaskManagerException(Throwable cause) {
		super(cause);
	}

	/** {@inheritDoc} */
	protected TaskManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
