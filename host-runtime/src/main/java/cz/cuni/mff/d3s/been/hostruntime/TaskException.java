package cz.cuni.mff.d3s.been.hostruntime;

/**
 * High-level exception for tasks. The exception awill be thrown when a task
 * cannot be properly handled by its Host Runtime.
 * 
 * @author Martin Sixta
 */
public class TaskException extends Exception {

	private int exitValue = -1;

	/**
	 * Crates new TaskException
	 */
	public TaskException() {
		super();
	}

	/**
	 * Crates new TaskException
	 * 
	 * @param message
	 *          exception's message
	 */
	public TaskException(String message) {
		super(message);
	}

	/**
	 * Crates new TaskException
	 * 
	 * @param message
	 *          exception's message
	 * @param exitValue
	 *          exit value of a task
	 */
	public TaskException(String message, int exitValue) {
		super(message);
		this.exitValue = exitValue;
	}

	/**
	 * Crates new TaskException
	 * 
	 * @param message
	 *          exception's message
	 * @param cause
	 *          cause of the exception
	 */
	public TaskException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crates new TaskException
	 * 
	 * @param message
	 *          exception's message
	 * @param cause
	 *          cause of the exception
	 * @param exitValue
	 *          exit value of a task
	 */
	public TaskException(String message, Throwable cause, int exitValue) {
		super(message, cause);
		this.exitValue = exitValue;
	}

	/**
	 * Crates new TaskException
	 * 
	 * @param cause
	 *          cause of the exception
	 */
	public TaskException(Throwable cause) {
		super(cause);
	}

	/**
	 * Returns exit value of a failed task.
	 * 
	 * @return exit value of a failed task
	 */
	public int getExitValue() {
		return exitValue;
	}

	/**
	 * Sets exit value of a failed task.
	 * 
	 * @param exitValue
	 *          exit value to set
	 */
	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
}
