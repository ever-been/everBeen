package cz.cuni.mff.d3s.been.storage;

import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.QueryAnswer;
import cz.cuni.mff.d3s.been.persistence.SuccessAction;

import java.util.Collection;

/**
 * A generic persistence layer for BEEN.
 * 
 * @author darklight
 * 
 */
public interface Storage extends Service {

	/**
	 * @return Create a {@link cz.cuni.mff.d3s.been.persistence.SuccessAction} which denotes what is to be done
	 *         with an {@link EntityCarrier} when it is decided that it should be
	 *         stored.
	 */
	SuccessAction<EntityCarrier> createPersistAction();

	/**
	 * Store a serialized {@link Entity} to a container determined by the
	 * {@link EntityID} argument.
	 * 
	 * @param entityId
	 *          {@link EntityID} that denotes the container which should hold the
	 *          provided entity
	 * @param JSON
	 *          The provided entity, serialized into JSON
	 * 
	 * @throws DAOException
	 *           If anything goes wrong with the persisting action
	 */
	void store(EntityID entityId, String JSON) throws DAOException;

	/**
	 * Query the persistence, returning JSON representations of matching objects.
	 *
	 * @param query Query to execute
	 *
	 * @return The result of the query (a {@link QueryAnswer})
	 */
	QueryAnswer query(Query query);
}
