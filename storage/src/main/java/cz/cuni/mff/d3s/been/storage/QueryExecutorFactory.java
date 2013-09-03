package cz.cuni.mff.d3s.been.storage;

import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.QueryRedactor;

/**
 * Factory for {@link QueryExecutor} instances. Uses configured {@link QueryRedactor} to create executors. The main purpose of this factory is to inject a database instance for the executors to use when querying.
 *
 * @author darklight
 */
public interface QueryExecutorFactory {

	/**
	 * Use a configured {@link QueryRedactor} to create a {@link QueryExecutor}
	 *
	 * @param redactor Redactor to inspect for query executor creation
	 *
	 * @return The {@link QueryExecutor}
	 *
	 * @throws DAOException When the executor cannot be created
	 */
	QueryExecutor createExecutor(QueryRedactor redactor) throws DAOException;
}
