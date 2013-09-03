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

	/**
	 * Create a task exception
	 */
	public TaskException() {
		super();
	}

	/**
	 * Create a task exception with an error message
	 *
	 * @param message Error message
	 */
	public TaskException(String message) {
		super(message);
	}

	/**
	 * Create a task exception with an error message and a cause
	 *
	 * @param message Error message
	 * @param cause Cause of this exception
	 */
	public TaskException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a task exception with a cause
	 *
	 * @param cause Cause of this exception
	 */
	public TaskException(Throwable cause) {
		super(cause);
	}

	protected TaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
