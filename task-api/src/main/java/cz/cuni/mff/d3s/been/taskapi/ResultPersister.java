package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.results.Result;

/**
 * Persister unit for results
 */
public interface ResultPersister extends AutoCloseable {

	/**
	 * Persist a result
	 *
	 * @param result Result to persist
	 *
	 * @throws DAOException When the result cannot be persisted
	 */
	void persist(Result result) throws DAOException;

	/**
	 * Close the persister, declaring no persisting is happening through it anymore
	 */
	void close();
}
