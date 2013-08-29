package cz.cuni.mff.d3s.been.objectrepository.mongo;

import java.util.ArrayList;
import java.util.Collection;

import com.mongodb.DB;
import com.mongodb.DBCollection;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.UnsupportedQueryException;

/**
 * Redactor for delete queries for MongoDB
 * 
 * @author darklight
 */
class MongoDeleteQueryRedactor extends MongoQueryRedactor {

	private static final String MONGO_SYSTEM_PREFIX = "system.";

	MongoDeleteQueryRedactor(EntityID entityID) {
		super(entityID);
	}

	@Override
	public MongoDeleteQueryExecutor createExecutor(DB db) throws UnsupportedQueryException {
		final Collection<DBCollection> targets = new ArrayList<DBCollection>();
		if (getPath() == null || getPath().getKind() == null) {
			for (String collName : db.getCollectionNames()) {
				if (collName.startsWith(MONGO_SYSTEM_PREFIX)) {
					// system namespace is reserved by MongoDB for internal use
					continue;
				}
				targets.add(db.getCollection(collName));
			}
		} else if (getPath().getGroup() == null) {
			final String pathPrefix = getPath().getKind() + ".";

			for (String collName : db.getCollectionNames()) {
				if (collName.startsWith(pathPrefix)) {
					targets.add(db.getCollection(collName));
				}
			}
		} else {
			targets.add(db.getCollection(getPath().getKind()).getCollection(getPath().getGroup()));
		}
		return new MongoDeleteQueryExecutor(targets, getFilter());
	}
}
