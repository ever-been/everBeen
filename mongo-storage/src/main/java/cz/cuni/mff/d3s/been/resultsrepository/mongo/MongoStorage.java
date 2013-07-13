package cz.cuni.mff.d3s.been.resultsrepository.mongo;

import java.net.UnknownHostException;
import java.util.*;

import com.mongodb.*;
import com.mongodb.util.JSON;

import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.core.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.SuccessAction;
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
		db = client.getDB("BEEN");
		if (authenticate && !db.authenticate(username, password.toCharArray())) {
			throw new StorageException("Failed to authenticate against BEEN database");
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
	public Collection<String> query(Query query) {
		final DBObject filter = new BasicDBObject();
		filter.putAll(query.getSelectors());
		return get(query.getEntityID(), filter);
	}

	private final DBCollection mapEntity(EntityID eid) {
		return db.getCollection(eid.getKind()).getCollection(eid.getGroup());
	}

	private final Collection<String> get(EntityID entityId, DBObject filter) {
		log.debug("Querying {} with filter {} and projection {}", entityId.toString(), filter.toString(), PROJECT_IGNORE_MONGO_ID.toString());
		final DBCursor cursor = mapEntity(entityId).find(filter, PROJECT_IGNORE_MONGO_ID);
		List<String> result = new ArrayList<String>(cursor.count());
		while (cursor.hasNext()) {
			result.add(cursor.next().toString());
		}
		log.debug("Query result: {}", result.toString());
		return result;
	}
}
