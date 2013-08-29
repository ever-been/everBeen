package cz.cuni.mff.d3s.been.objectrepository.mongo;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.QueryRedactor;
import cz.cuni.mff.d3s.been.storage.QueryRedactorFactory;

/**
 * Redactor factory for mongo queries
 *
 * @author darklight
 */
final class MongoQueryRedactorFactory implements QueryRedactorFactory {

	@Override
	public QueryRedactor fetch(EntityID entityID) {
		return new MongoFetchQueryRedactor(entityID);
	}

	@Override
	public QueryRedactor delete(EntityID entityID) {
		return new MongoDeleteQueryRedactor(entityID);
	}
}
