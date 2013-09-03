package cz.cuni.mff.d3s.been.manager.selector;

/**
 * Exception indicating no suitable <em>Host Runtime</em> was found for a <em>task</em>
 * @author Martin Sixta
 */
public class NoRuntimeFoundException extends Exception {

	/**
	 * Create a <em>No Runtime</em> exception
	 */
	public NoRuntimeFoundException() {
		super();
	}

	/**
	 * Create a <em>No Runtime</em> exception with an error message
	 *
	 * @param message Error message
	 */
	public NoRuntimeFoundException(String message) {
		super(message);
	}

	/**
	 * Create a <em>No Runtime</em> exception with an error message and a cause
	 *
	 * @param message Error message
	 * @param cause Cause of this exception
	 */
	public NoRuntimeFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a <em>No Runtime</em> exception with a cause
	 *
	 * @param cause Cause of this exception
	 */
	public NoRuntimeFoundException(Throwable cause) {
		super(cause);
	}

	protected NoRuntimeFoundException(
			String message,
			Throwable cause,
			boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
