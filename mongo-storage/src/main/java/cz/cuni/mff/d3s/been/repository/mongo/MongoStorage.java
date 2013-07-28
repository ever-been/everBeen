package cz.cuni.mff.d3s.been.repository.mongo;

import java.net.UnknownHostException;
import java.util.*;

import com.mongodb.*;
import com.mongodb.util.JSON;

import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.*;
import cz.cuni.mff.d3s.been.storage.Storage;
import cz.cuni.mff.d3s.been.storage.StorageException;
import cz.cuni.mff.d3s.been.storage.StoragePersistAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A mongoDB adapter for BEEN result persistence layer.
 * 
 * @author darklight
 * 
 */
public final class MongoStorage implements Storage {
	private static final Logger log = LoggerFactory.getLogger(MongoStorage.class);

	private static final DBObject PROJECT_IGNORE_MONGO_ID = new BasicDBObject();

	private final boolean authenticate;
	private final String username;
	private final String password;
	private final String dbname;
	private final MongoClient client;
	private DB db;

	static {
		PROJECT_IGNORE_MONGO_ID.put("_id", 0);
	}

	private MongoStorage(MongoClient client, String dbname) {
		this.client = client;
		this.dbname = dbname;
		this.authenticate = false;
		this.username = null;
		this.password = null;
	}

	private MongoStorage(MongoClient client, String dbname, String username, String password) {
		this.client = client;
		this.dbname = dbname;
		this.authenticate = true;
		this.username = username;
		this.password = password;
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
		try {
			final CommandResult statRes = db.getStats();
			if (!statRes.ok()) {
				throw new StorageException("Failed to get DB stats. Mongo database is probably not running.");
			}
			log.info("Mongo connection initalized. Current Mongo stats:\n{}", statRes.toString());
		} catch (MongoException e) {
			throw new StorageException("Error getting DB stats. Mongo database is probably not running.", e);
		}
	}

	@Override
	public void stop() {
		client.close();
	}

	@Override
	public void store(EntityID entityId, String entityJSON) throws DAOException {
		final WriteResult wr = mapEntity(entityId).insert((DBObject) JSON.parse(entityJSON));
		if (wr.getError() != null) {
			throw new DAOException(String.format(
					"Write on Entity ID %s resulted in the following error: %s.",
					entityId.toString(),
					wr.getError()));
		}
	}

	@Override
	public SuccessAction<EntityCarrier> createPersistAction() {
		return StoragePersistAction.createForStore(this);
	}

	@Override
	public QueryAnswer query(Query query) {
		final DBObject filter = new BasicDBObject();
		for (String selectorName: query.getSelectorNames()) {
			filter.put(selectorName, query.getSelector(selectorName));
		}
		switch (query.getType()) {
			case FETCH:
				try {
					return QueryAnswerFactory.fetched(fetch(query.getEntityID(), filter));
				} catch (DAOException e) {
					return QueryAnswerFactory.unknownError();
				}
			case DELETE:
				try {
					if (query.getEntityID() != null) {
						delete(query.getEntityID(), filter);
					} else {
						delete(filter);
					}
					return QueryAnswerFactory.deleted();
				} catch (DAOException e) {
					return QueryAnswerFactory.unknownError();
				}
			case NATIVE:
				try {
					NativeQuery nq = (NativeQuery)query;
					return QueryAnswerFactory.fetched(nativeQuery(nq.getJsFunction()));
				} catch (DAOException e) {
					return QueryAnswerFactory.unknownError();
				}
			default:
				return QueryAnswerFactory.badQuery();
		}
	}

	private final DBCollection mapEntity(EntityID eid) throws DAOException {
		return db.getCollection(eid.getKind()).getCollection(eid.getGroup());
	}

	private final Collection<String> fetch(EntityID entityId, DBObject filter) throws DAOException {
		log.debug("Querying {} with filter {} and projection {}", entityId.toString(), filter.toString(), PROJECT_IGNORE_MONGO_ID.toString());
		final DBCursor cursor = mapEntity(entityId).find(filter, PROJECT_IGNORE_MONGO_ID);
		List<String> result = new ArrayList<String>(cursor.count());
		while (cursor.hasNext()) {
			result.add(cursor.next().toString());
		}
		log.debug("Query result: {}", result.toString());
		return result;
	}

	private final void delete(DBObject filter) {
		for (String collName: db.getCollectionNames()) {
			db.getCollection(collName).remove(filter);
		}
	}

	private final void delete(EntityID entityID, DBObject filter) throws DAOException {
		mapEntity(entityID).remove(filter);
	}

	private Collection<String> nativeQuery(String jsFunction) throws DAOException {
		CommandResult commandResult = db.doEval(jsFunction);
		Object retval = commandResult.get("retval");
		if (retval instanceof BasicDBList) {
			BasicDBList list = (BasicDBList) retval;
			List<String> result = new ArrayList<>();
			for (Object o : list) {
				result.add(o.toString());
			}
			return result;
		} else if (retval instanceof BasicDBObject) {
			List<String> result = new ArrayList<>();
			result.add(retval.toString());
			return result;
		} else {
			throw new DAOException("Invalid retval from evaluated query: " + retval.toString());
		}
	}
}
