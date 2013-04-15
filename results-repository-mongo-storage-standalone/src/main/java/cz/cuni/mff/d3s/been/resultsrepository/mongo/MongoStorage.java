package cz.cuni.mff.d3s.been.resultsrepository.mongo;

import java.net.UnknownHostException;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.ResultContainerId;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;
import cz.cuni.mff.d3s.been.resultsrepository.storage.StorageException;

public final class MongoStorage implements Storage {
	private static final Logger log = LoggerFactory.getLogger(MongoStorage.class);

	private static final String DB_NAME_RESULTS = "results";
	private static final String DB_NAME_BINARIES = "binaries";

	private final String hostname;
	private final ObjectMapper objectMapper;
	private MongoClient client;

	private MongoServerStandalone mongoServer;

	public MongoStorage() {
		this.hostname = "localhost:12345";
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public void start() throws StorageException {
		mongoServer = new MongoServerStandalone();

		mongoServer.start();

		try {

			this.client = new MongoClient(hostname);
		} catch (UnknownHostException e) {
			throw new StorageException(String.format("Unable to resolve MongoDB hostname %s", hostname), e);
		}
	}

	@Override
	public void stop() {
		client.close();
		mongoServer.stop();
	}

	@Override
	public void storeResult(ResultContainerId containerId, String json) throws DAOException {
		DB resdb = client.getDB(containerId.getDatabaseName());
		DBCollection coll = resdb.getCollection(containerId.getCollectionName());
		coll.insert(new BasicDBObject(containerId.getEntityName(), json));
	}
}
