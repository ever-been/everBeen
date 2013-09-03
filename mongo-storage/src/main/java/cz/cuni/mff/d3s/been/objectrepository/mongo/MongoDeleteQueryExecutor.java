package cz.cuni.mff.d3s.been.objectrepository.mongo;

import java.util.Collection;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import cz.cuni.mff.d3s.been.persistence.QueryAnswer;
import cz.cuni.mff.d3s.been.persistence.QueryAnswerFactory;
import cz.cuni.mff.d3s.been.persistence.QueryExecutionException;
import cz.cuni.mff.d3s.been.storage.QueryExecutor;

/**
 * A delete query executor for MongoDB
 * 
 * @author darklight
 */
class MongoDeleteQueryExecutor implements QueryExecutor {

	private final Collection<DBCollection> targets;
	private final DBObject filter;

	/**
	 * Creates new MongoDeleteQueryExecutor.
	 * 
	 * @param targets
	 *          targets of the query
	 * @param filter
	 *          filter of objects
	 */
	MongoDeleteQueryExecutor(Collection<DBCollection> targets, DBObject filter) {
		this.targets = targets;
		this.filter = filter;
	}

	@Override
	public QueryAnswer execute() throws QueryExecutionException {
		for (DBCollection coll : targets) {
			coll.remove(filter);
		}
		return QueryAnswerFactory.deleted();
	}
}
