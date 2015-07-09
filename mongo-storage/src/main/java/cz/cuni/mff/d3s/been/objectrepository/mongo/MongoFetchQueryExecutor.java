package cz.cuni.mff.d3s.been.objectrepository.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import cz.cuni.mff.d3s.been.persistence.QueryAnswer;
import cz.cuni.mff.d3s.been.persistence.QueryAnswerFactory;
import cz.cuni.mff.d3s.been.persistence.QueryExecutionException;
import cz.cuni.mff.d3s.been.storage.QueryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fetch query executor for MongoDB
 * 
 * @author darklight
 */
class MongoFetchQueryExecutor implements QueryExecutor {

	private static final Logger log = LoggerFactory.getLogger(MongoFetchQueryExecutor.class);

	private static final String DB_ID = "_id";

	private final DBCollection target;
	private final DBObject filter;
	private final DBObject mapping;

	/**
	 * Creates new MongoFetchQueryExecutor
	 * 
	 * @param target
	 *          target DB collection
	 * @param filter
	 *          filter of the query
	 * @param mapping
	 *          mappings for the query
	 */
	MongoFetchQueryExecutor(DBCollection target, DBObject filter, DBObject mapping) {
		this.target = target;
		this.filter = filter;
		this.mapping = mapping;
	}

	@Override
	public QueryAnswer execute() throws QueryExecutionException {
		final DBCursor cursor = target.find(filter, mapping);
		final Collection<String> results = new ArrayList<String>(cursor.size());
		final Iterator<DBObject> cursorIt = cursor.iterator();
		while (cursorIt.hasNext()) {
			final DBObject dbob = cursorIt.next();
			dbob.removeField(DB_ID);
			final String dbobString = dbob.toString();
			results.add(dbobString);
		}
		log.debug("Fetch query execution complete, returning data: {}", results);
		return QueryAnswerFactory.fetched(results);
	}
}
