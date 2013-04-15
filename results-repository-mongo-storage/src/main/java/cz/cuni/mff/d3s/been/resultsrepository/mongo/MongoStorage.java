package cz.cuni.mff.d3s.been.resultsrepository.mongo;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.ResultContainerId;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;
import cz.cuni.mff.d3s.been.resultsrepository.storage.StorageException;

/**
 * A mongoDB adapter for BEEN result persistence layer.
 * 
 * @author darklight
 * 
 */
public final class MongoStorage implements Storage {
	private final String hostname;
	private MongoClient client;

	public MongoStorage() {
		this.hostname = "localhost";
	}

	@Override
	public void start() throws StorageException {
		try {
			this.client = new MongoClient(hostname);
		} catch (UnknownHostException e) {
			throw new StorageException(String.format(
					"Unable to resolve MongoDB hostname \"%s\"",
					hostname), e);
		}
	}

	@Override
	public void stop() {
		client.close();
	}

	@Override
	public void storeResult(ResultContainerId containerId, String json) throws DAOException {
		final DB resdb = client.getDB(containerId.getDatabaseName());
		final DBCollection coll = resdb.getCollection(containerId.getCollectionName());
		final DBObject dbob = (DBObject) JSON.parse(json);
		dbob.put("entity", containerId.getEntityName());
		coll.insert(dbob);
	}
}
