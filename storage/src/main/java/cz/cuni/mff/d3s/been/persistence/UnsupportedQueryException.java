package cz.cuni.mff.d3s.been.persistence;

/**
 * {@link DAOException} marking that a query has been received that cannot be processed.
 *
 * @author darklight
 */
public class UnsupportedQueryException extends DAOException {

	private final Query query;

	/**
	 * Create an <em>Unsupported Query Exception</em> with an error message
	 *
	 * @param query Query that was not supported
	 * @param message Description of what happened
	 */
	public UnsupportedQueryException(Query query, String message) {
		super(message);
		this.query = query;
	}

	/**
	 * Create an <em>Unsupported Query Exception</em> with an error message and a cause
	 *
	 * @param query Query that was not supported
	 * @param message Description of what happened
	 * @param cause Reason why the query was not supported
	 */
	public UnsupportedQueryException(Query query, String message, Throwable cause) {
		super(message, cause);
		this.query = query;
	}

	@Override
	public String getMessage() {
		return (query == null) ?
		super.getMessage() :
		String.format("Query '%s' invalid - %s", query.toString(), super.getMessage());
	}
}
