package cz.cuni.mff.d3s.been.objectrepository.mongo;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import cz.cuni.mff.d3s.been.persistence.QueryAnswer;
import cz.cuni.mff.d3s.been.persistence.QueryAnswerFactory;
import cz.cuni.mff.d3s.been.persistence.QueryExecutionException;
import cz.cuni.mff.d3s.been.storage.QueryExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A fetch query executor for MongoDB
 *
 * @author darklight
 */
class MongoFetchQueryExecutor implements QueryExecutor {
	final DBCollection target;
	final DBObject filter;
	final DBObject mapping;

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
			results.add(cursorIt.next().toString());
		}
		return QueryAnswerFactory.fetched(results);
	}
}
