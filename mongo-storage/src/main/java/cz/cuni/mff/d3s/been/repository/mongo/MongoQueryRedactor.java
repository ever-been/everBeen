package cz.cuni.mff.d3s.been.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.QueryExecutionException;
import cz.cuni.mff.d3s.been.persistence.QueryRedactor;
import cz.cuni.mff.d3s.been.persistence.UnsupportedQueryException;
import cz.cuni.mff.d3s.been.storage.QueryExecutor;

import java.util.regex.Pattern;

/**
 * A skeletal redactor for mongo queries. Takes care of filter construction. Query construction itself is left to subclasses to handle.
 *
 * @author darklight
 */
abstract class MongoQueryRedactor implements QueryRedactor {
	private static final String ABOVE_OPERATOR = "$gte";
	private static final String BELOW_OPERATOR = "$lt";

	/** A projection that ensures the <code>_id</code> attribute generated by MongoDB is not propagated to query results */
	private static final DBObject NO_DBID_MAPPING;

	static {
		NO_DBID_MAPPING = new BasicDBObject("_id", 0);
	}

	private final DBObject filter;
	private final EntityID entityID;

	MongoQueryRedactor(EntityID entityID) {
		this.entityID = entityID;
		this.filter = new BasicDBObject();
	}

	@Override
	public void equalitySelector(String attributeName, Object value) {
		filter.put(attributeName, value);
	}

	@Override
	public void patternSelector(String attributeName, String pattern) {
		filter.put(attributeName, Pattern.compile(pattern));
	}

	@Override
	public void aboveSelector(String attributeName, Object lowBound) {
		final DBObject ge = new BasicDBObject();
		ge.put(ABOVE_OPERATOR, lowBound);
		filter.put(attributeName, ge);
	}

	@Override
	public void belowSelector(String attributeName, Object highBound) {
		final DBObject lt = new BasicDBObject();
		lt.put(BELOW_OPERATOR, highBound);
		filter.put(attributeName, lt);
	}

	@Override
	public void intervalSelector(String attributeName, Object lowBound, Object highBound) {
		final DBObject between = new BasicDBObject();
		between.put(ABOVE_OPERATOR, lowBound);
		between.put(BELOW_OPERATOR, highBound);
		filter.put(attributeName, between);
	}

	/**
	 * Return the filter to subclasses for query-specific handling
	 *
	 * @return The filter
	 */
	protected DBObject getFilter() {
		return filter;
	}

	/**
	 * Return the attribute mapping to subclasses for query-specific handling
	 *
	 * @return The attribute mapping
	 */
	protected DBObject getMapping() {
		return NO_DBID_MAPPING;
	}

	/**
	 * Return the path to the objects targeted by the query.
	 *
	 * @return The Entity ID (serves as path)
	 */
	protected EntityID getPath() {
		return entityID;
	}

	/**
	 * Factory method for {@link QueryExecutor} instances, with inferred {@link DB} instance
	 *
	 * @param db Database to use for querying
	 *
	 * @return The {@link QueryExecutor}
	 *
	 * @throws DAOException When a query cannot be constructed from provided information
	 */
	public abstract QueryExecutor createExecutor(DB db) throws DAOException;
}
