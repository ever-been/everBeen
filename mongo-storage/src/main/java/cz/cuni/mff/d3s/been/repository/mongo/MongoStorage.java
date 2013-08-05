package cz.cuni.mff.d3s.been.repository.mongo;

import java.net.UnknownHostException;
import java.util.*;

import com.mongodb.*;
import com.mongodb.util.JSON;

import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.*;
import cz.cuni.mff.d3s.been.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A MongoDB adapter for BEEN result persistence layer.
 * 
 * @author darklight
 * 
 */
public final class MongoStorage implements Storage {

	private static final Logger log = LoggerFactory.getLogger(MongoStorage.class);

	private final boolean authenticate;
	private final String username;
	private final String password;
	private final String dbname;
	private final MongoClient client;
	private final MongoQueryRedactorFactory queryRedactorFactory;
	private final QueryTranslator queryTranslator;

	private DB db;
	private QueryExecutorFactory queryExecutorFactory;

	private MongoStorage(MongoClient client, String dbname) {
		this.client = client;
		this.dbname = dbname;
		this.authenticate = false;
		this.username = null;
		this.password = null;
		this.queryRedactorFactory = new MongoQueryRedactorFactory();
		this.queryTranslator = new QueryTranslator();
	}

	private MongoStorage(MongoClient client, String dbname, String username, String password) {
		this.client = client;
		this.dbname = dbname;
		this.authenticate = true;
		this.username = username;
		this.password = password;
		this.queryRedactorFactory = new MongoQueryRedactorFactory();
		this.queryTranslator = new QueryTranslator();
	}

	public static
			MongoStorage
			create(String hostname, String dbname, MongoClientOptions opts) throws UnknownHostException {
		final MongoClient client = new MongoClient(hostname, opts);
		return new MongoStorage(client, dbname);
	}

	public static MongoStorage create(List<ServerAddress> seeds, String dbname, MongoClientOptions opts) {
		final MongoClient client = new MongoClient(seeds, opts);
		return new MongoStorage(client, dbname);
	}

	public static MongoStorage create(String hostname, String dbname, String username, String password,
			MongoClientOptions opts) throws UnknownHostException {
		final MongoClient client = new MongoClient(hostname, opts);
		return new MongoStorage(client, dbname, username, password);
	}

	public static MongoStorage create(List<ServerAddress> seeds, String dbname, String username, String password,
			MongoClientOptions opts) {
		final MongoClient client = new MongoClient(seeds, opts);
		return new MongoStorage(client, dbname, username, password);
	}

	@Override
	public void start() throws StorageException {
		db = client.getDB(dbname);
		if (authenticate && !db.authenticate(username, password.toCharArray())) {
			throw new StorageException("Failed to authenticate against BEEN database");
		}
		if (!isConnected()) {
			throw new StorageException("Error getting DB stats. Mongo database is probably not running.");
		}
		queryExecutorFactory = new MongoQueryExecutorFactory(db);
	}

	@Override
	public void stop() {
		client.close();
	}

	@Override
	public void store(EntityID entityId, String entityJSON) throws DAOException {
		try {
			final WriteResult wr = mapEntity(entityId).insert((DBObject) JSON.parse(entityJSON));
			if (wr.getError() != null) {
				throw new DAOException(String.format(
						"Write on Entity ID %s resulted in the following error: %s.",
						entityId.toString(),
						wr.getError()));
			}
		} catch (MongoException e) {
			throw new DAOException(String.format("Failed to store persistent object (%s, %s)", entityId.toString(), entityJSON), e);
		}
	}

	@Override
	public SuccessAction<EntityCarrier> createPersistAction() {
		return StoragePersistAction.createForStore(this);
	}

	@Override
	public QueryAnswer query(Query query) {
		QueryRedactor queryRedactor;
		switch (query.getType()) {
			case FETCH:
				queryRedactor = queryRedactorFactory.fetch(query.getEntityID());
				break;
			case DELETE:
				queryRedactor = queryRedactorFactory.delete(query.getEntityID());
				break;
			default:
				return QueryAnswerFactory.badQuery();
		}

		try {
			queryTranslator.interpret(query, queryRedactor);
		} catch (DAOException e) {
			log.error("Unsupported query '{}'", query, e);
			return QueryAnswerFactory.badQuery();
		}


		final QueryExecutor queryExecutor;
		try {
			queryExecutor = queryExecutorFactory.createExecutor(queryRedactor);
		} catch (DAOException e) {
			log.error("Query '{}' is invalid.", query, e);
			return QueryAnswerFactory.badQuery();
		}

		try {
			return queryExecutor.execute();
		} catch (QueryExecutionException e) {
			log.error("Failed to execute query '{}': {}", query, e);
			return QueryAnswerFactory.queryExecutionFailed();
		} catch (MongoException e) {
			if (e instanceof MongoException.Network) {
				return QueryAnswerFactory.persistenceDown();
			} else {
				log.error("Unknown MongoDB exception processing query '{}'", query, e);
				return QueryAnswerFactory.unknownError();
			}
		} catch (Throwable t) {
			log.error("Unknown error processing query '{}'", query, t);
			return QueryAnswerFactory.unknownError();
		}
	}

	@Override
	public boolean isConnected() {
		try {
			CommandResult cr = db.getStats();
			log.info("MongoDB connected with stats {}", cr.toString());
			return true;
		} catch (MongoException e) {
			if (e instanceof MongoException.Network) {
				log.warn("MongoDB disconnected");
				return false;
			} else {
				return true;
			}
		}
	}

	private final DBCollection mapEntity(EntityID eid) throws DAOException {
		return db.getCollection(eid.getKind()).getCollection(eid.getGroup());
	}

}
