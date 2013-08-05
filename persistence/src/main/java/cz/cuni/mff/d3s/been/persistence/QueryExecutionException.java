package cz.cuni.mff.d3s.been.persistence;

/**
 * An exception indicating that a query has failed
 *
 * @author darklight
 */
public class QueryExecutionException extends DAOException {

	private final Query query;

	public QueryExecutionException(Query query, String message) {
		super(message);
		this.query = query;
	}

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
