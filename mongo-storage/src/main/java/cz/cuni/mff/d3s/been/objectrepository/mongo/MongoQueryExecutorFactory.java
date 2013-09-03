package cz.cuni.mff.d3s.been.objectrepository.mongo;

import com.mongodb.DB;

import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.QueryRedactor;
import cz.cuni.mff.d3s.been.storage.QueryExecutor;
import cz.cuni.mff.d3s.been.storage.QueryExecutorFactory;

/**
 * @author darklight
 */
public class MongoQueryExecutorFactory implements QueryExecutorFactory {
	private final DB db;

	/**
	 * Created new MongoQueryExecutorFactory.
	 * 
	 * @param db
	 *          DB connection
	 */
	MongoQueryExecutorFactory(DB db) {
		this.db = db;
	}

	@Override
	public QueryExecutor createExecutor(QueryRedactor redactor) throws DAOException {
		return ((MongoQueryRedactor) redactor).createExecutor(db);
	}
}
