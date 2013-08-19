package cz.cuni.mff.d3s.been.manager.action;

/**
 * 
 * @author Martin Sixta
 */
public final class TaskActionException extends RuntimeException {

	public TaskActionException() {
		super();
	}

	public TaskActionException(String message) {
		super(message);
	}

	public TaskActionException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskActionException(Throwable cause) {
		super(cause);
	}

	protected TaskActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
