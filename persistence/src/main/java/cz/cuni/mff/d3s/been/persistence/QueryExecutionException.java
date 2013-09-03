package cz.cuni.mff.d3s.been.persistence;

/**
 * An exception indicating that a query has failed
 *
 * @author darklight
 */
public class QueryExecutionException extends DAOException {

	private final Query query;

	/**
	 * Create a <em>Query Execution Exception</em> with an error message
	 *
	 * @param query The query that failed
	 * @param message Failure description
	 */
	public QueryExecutionException(Query query, String message) {
		super(message);
		this.query = query;
	}

	/**
	 * Create a <em>Query Execution Exception</em> with an error message and a cause
	 *
	 * @param query The query that failed
	 * @param message Failure description
	 * @param cause Reason of the failure
	 */
	public QueryExecutionException(Query query, String message, Throwable cause) {
		super(message, cause);
		this.query = query;
	}

	@Override
	public String getMessage() {
		return (query == null) ?
				super.getMessage() :
				String.format("Failed execution on query '%s' - %s", query.toString(), super.getMessage());
	}
}
