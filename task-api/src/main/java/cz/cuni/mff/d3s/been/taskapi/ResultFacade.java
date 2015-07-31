package cz.cuni.mff.d3s.been.taskapi;

import java.util.Collection;
import java.util.Map;

import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.results.DataSetResult;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.results.ResultMapping;

/**
 * Facade for result storage and retrieval.
 */
public interface ResultFacade {

	/**
	 * Create a custom {@link Result} instance with given type. Set necessary IDs.
	 * 
	 * @param resultClass Class of the result
	 * @param <T> Type of the result to create
	 * 
	 * @return The result instance, initialized with necessary IDs
	 *
	 * @throws DAOException When result creation fails (e.g. no available constructor)
	 */
	<T extends Result> T createResult(Class<T> resultClass) throws DAOException;

	/**
	 * Create a result persister bound to a specific entity (determines target
	 * collection in database)
	 * 
	 * @param group Name of the result group to store to
	 *
	 * @return The persister
	 * 
	 * @throws DAOException If the persister cannot be created
	 */
	Persister createResultPersister(String group) throws DAOException;

	/**
	 * Persist a single result. Note that a persister is internally created for
	 * this action. If you want to store multiple results into the same target
	 * collection, use {@link #createResultPersister(java.lang.String)}
	 * first and then use the persister object for storage.
	 * 
	 * @param result Result to store
	 * @param group Name of the result group to store to
	 *
	 * @throws DAOException If the result cannot be stored
	 */
	void persistResult(Result result, String group) throws DAOException;

	/**
	 * Retrieve results based on the id of targeted entity and a selector (query).
	 * 
	 * @param fetchQuery Provided selection criteria
	 * @param resultClass Class of the unmarshalled results
	 * @param <T> Type of the result
	 * 
	 * @return Return a collection of results corresponding (at the given query
	 *         time) to matching results present in the persistence layer
	 *
	 * @throws DAOException When the query fails
	 */
	<T extends Result> Collection<T> query(Query fetchQuery, Class<T> resultClass) throws DAOException;

	/**
	 * Retrieve an abstract result map based on the id of targeted entity and a selector (query).
	 *
	 * @param fetchQuery Selection criteria
	 * @param resultMapping Mapping definition of result fields
	 *
	 * @return A collection of typed <code>fieldName</code>-<code>fieldValue</code> maps (1 map per object)
	 *
	 * @throws DAOException When the query fails
	 */
	Collection<Map<String, Object>> query(Query fetchQuery, ResultMapping resultMapping) throws DAOException;

	/**
	 * Retrieve a data set from a previously stored {@link cz.cuni.mff.d3s.been.results.DataSetResult}
	 *
	 * @param datasetId ID of the dataset
	 *
	 * @return The dataset
	 *
	 * @throws DAOException When query to persistence layer fails
	 */
	DataSet query(String datasetId) throws DAOException;

	/**
	 * Delete some results. Actually, don't do this, it won't save you from the
	 * raptors.
	 *
	 * Note: the funny comments and the commented-out method are still here because deleting from tasks is prohibited.
	 * 
	 * @param deleteQuery Delete some results. Or something...
	 * 
	 * @throws DAOException
	 *           When delete fails. Or something more nefarious happens. Like
	 *           apocalypse. Muhaha...
	 */
	//void delete(Query deleteQuery) throws DAOException;
}
