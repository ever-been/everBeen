package cz.cuni.mff.d3s.been.storage;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.QueryRedactor;

/**
 * @author darklight
 */
public interface QueryRedactorFactory {

	/**
	 * Create a new fetch query redactor
	 *
	 * @param entityID ID of the entity to be fetched
	 *
	 * @return The redactor
	 */
	QueryRedactor fetch(EntityID entityID);

	/**
	 * Create a new delete query redactor
	 *
	 * @param entityID ID of the entity to be deleted
	 *
	 * @return The redactor
	 */
	QueryRedactor delete(EntityID entityID);
}
