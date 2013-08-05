package cz.cuni.mff.d3s.been.repository.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.UnsupportedQueryException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Redactor for delete queries for MongoDB
 *
 * @author darklight
 */
class MongoDeleteQueryRedactor extends MongoQueryRedactor {

	MongoDeleteQueryRedactor(EntityID entityID) {
		super(entityID);
	}

	@Override
	public MongoDeleteQueryExecutor createExecutor(DB db) throws UnsupportedQueryException {
		final Collection<DBCollection> targets = new ArrayList<DBCollection>();
		if (getPath() == null || getPath().getKind() == null) {
			for (String collName: db.getCollectionNames()) {
				targets.add(db.getCollection(collName));
			}
		} else if (getPath().getGroup() == null) {
			final String pathPrefix = getPath().getKind() + ".";

			for (String collName: db.getCollectionNames()) {
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
