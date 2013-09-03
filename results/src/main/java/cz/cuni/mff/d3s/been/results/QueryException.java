package cz.cuni.mff.d3s.been.results;

/**
 * This exception means that the specified query into the Results Repository is
 * either invalid or cannot be executed.
 * 
 * @author darklight
 */
public final class QueryException extends Exception {

	/** Version ID (serialization) */
	private static final long serialVersionUID = 706143744182473856L;

	/**
	 * Create a query exception
	 */
	public QueryException() {}

	/**
	 * Create a query exception with an error message
	 *
	 * @param message Error message
	 */
	public QueryException(String message) {
		super(message);
	}

	/**
	 * Create a query exception with a cause
	 *
	 * @param cause Cause of this exception
	 */
	public QueryException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a query exception with a message and a cause
	 *
	 * @param message Error message
	 * @param cause Cause of this exception
	 */
	public QueryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a query exception with a suppressible stack trace
	 *
	 * @param message Error message
	 * @param cause Cause of this exception
	 * @param enableSuppression Whether the stack trace should be suppressed
	 * @param writableStackTrace The stack trace
	 */
	public QueryException(
			String message,
			Throwable cause,
			boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
