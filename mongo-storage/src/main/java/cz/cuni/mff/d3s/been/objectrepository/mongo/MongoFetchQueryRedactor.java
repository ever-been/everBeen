package cz.cuni.mff.d3s.been.objectrepository.mongo;

import com.mongodb.DB;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.DAOException;

/**
 * Redactor for fetch queries for MongoDB
 *
 * @author darklight
 */
class MongoFetchQueryRedactor extends MongoQueryRedactor {

	MongoFetchQueryRedactor(EntityID entityID) {
		super(entityID);
	}

	@Override
	public MongoFetchQueryExecutor createExecutor(DB db) throws DAOException {
		if (getPath() == null || getPath().getKind() == null || getPath().getGroup() == null) {
			throw new DAOException("Path to collection (Entity ID) must be fully specified for fetch queries");
		}
		return new MongoFetchQueryExecutor(
				db.getCollection(getPath().getKind()).getCollection(getPath().getGroup()),
				getFilter(),
				getMapping());
	}
}
