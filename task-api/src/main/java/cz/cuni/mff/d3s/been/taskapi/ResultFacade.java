package cz.cuni.mff.d3s.been.taskapi;

import java.util.Collection;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.core.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.results.Result;

/**
 * Facade for result storage and retrieval.
 */
public interface ResultFacade {
	/**
	 * Create a result persister bound to a specific entity (determines target collection)
	 *
	 * @param entityId Id of the entity to bind to
	 *
	 * @return The persister
	 *
	 * @throws DAOException If the persister cannot be created
	 */
	ResultPersister createResultPersister(EntityID entityId) throws DAOException;

	/**
	 * Persist a single result. Note that a persister is internally created for this action. If you want to store multiple results into the same target collection, use {@link #createResultPersister(cz.cuni.mff.d3s.been.core.persistence.EntityID)} first and then use the persister object for storage.
	 *
	 * @param result Result to store
	 * @param entityId ID of the result entity (determines target collection)
	 *
	 * @throws DAOException If the result cannot be stored
	 */
	void persistResult(Result result, EntityID entityId) throws DAOException;

	/**
	 * Retrieve results based on the id of targeted entity and a selector (query).
	 *
	 * @param query Provided selection criteriae
	 * @param resultArrayClass Expected class of the array of selected results (not one result)
	 * @param <T> Type of the result
	 *
	 * @return Return a collection of results corresponding (at the given query time) to matching results present in the persistence layer
	 */
	<T extends Result> Collection<T> retrieveResults(Query query, Class<T[]> resultArrayClass) throws DAOException;

	/*
	Collection<String> listGroupsOfKind(String kind);
	Collection<Result> retrieveResults(EntityID entityId);
	Collection<Result> retrieveResults(EntityID entityId, Entity filter);
	*/
}
