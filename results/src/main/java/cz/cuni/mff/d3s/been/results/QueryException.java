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

	public QueryException() {}

	public QueryException(String message) {
		super(message);
	}

	public QueryException(Throwable cause) {
		super(cause);
	}

	public QueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryException(
			String message,
			Throwable cause,
			boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
