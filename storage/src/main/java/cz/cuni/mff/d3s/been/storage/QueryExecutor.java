package cz.cuni.mff.d3s.been.storage;

import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.QueryAnswer;
import cz.cuni.mff.d3s.been.persistence.QueryExecutionException;

/**
 * Query executor mixin
 *
 * @author darklight
 */
public interface QueryExecutor {

	/**
	 * Execute redacted query over a MongoDB instance
	 *
	 * @return Answer to the query
	 *
	 * @throws QueryExecutionException When query execution fails
	 */
	QueryAnswer execute() throws QueryExecutionException;
}
