package cz.cuni.mff.d3s.been.manager;

/**
 * High-level exception for all things that can go wrong inside TaskManager.
 * 
 * @author Martin Sixta
 */
public class TaskManagerException extends Exception {

	public TaskManagerException() {
		super();
	}

	public TaskManagerException(String message) {
		super(message);
	}

	public TaskManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskManagerException(Throwable cause) {
		super(cause);
	}

	protected TaskManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
