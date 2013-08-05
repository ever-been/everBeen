package cz.cuni.mff.d3s.been.hostruntime;

/**
 * High-level exception for tasks. The exception awill be thrown when a task
 * cannot be properly handled by its Host Runtime.
 * 
 * @author Martin Sixta
 */
public class TaskException extends Exception {

	private int exitValue = -1;

	/** {@inheritDoc} */
	public TaskException() {
		super();
	}

	/** {@inheritDoc} */
	public TaskException(String message) {
		super(message);
	}

	public TaskException(String message, int exitValue) {
		super(message);
		this.exitValue = exitValue;
	}

	/** {@inheritDoc} */
	public TaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskException(String message, Throwable cause, int exitValue) {
		super(message, cause);
		this.exitValue = exitValue;
	}

	/** {@inheritDoc} */
	public TaskException(Throwable cause) {
		super(cause);
	}

	/** {@inheritDoc} */
	protected TaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public int getExitValue() {
		return exitValue;
	}

	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
}
