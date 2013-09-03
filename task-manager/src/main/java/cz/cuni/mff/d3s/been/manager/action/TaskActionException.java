package cz.cuni.mff.d3s.been.manager.action;

/**
 * Exception denoting an error in task action execution
 * 
 * @author Martin Sixta
 */
public final class TaskActionException extends RuntimeException {

	/**
	 * Create a task action exception
	 */
	public TaskActionException() {
		super();
	}

	/**
	 * Create a task action exception with an error message
	 * 
	 * @param message
	 *          Error message
	 */
	public TaskActionException(String message) {
		super(message);
	}

	/**
	 * Create a task action exception with an error message and a cause
	 * 
	 * @param message
	 *          Error message
	 * @param cause
	 *          Cause of this exception
	 */
	public TaskActionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a task action exception with a cause
	 * 
	 * @param cause
	 *          Cause of this exception
	 */
	public TaskActionException(Throwable cause) {
		super(cause);
	}

}
