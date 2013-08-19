package cz.cuni.mff.d3s.been.mapstore.mongodb;

import static cz.cuni.mff.d3s.been.mapstore.MapStoreConfiguration.*;

import java.net.UnknownHostException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

/**
 * {@link MapStoreFactory} for the BEEN implementation of MongoDB
 * {@link com.hazelcast.core.MapStore}.
 * 
 * To use the {@link com.hazelcast.core.MapStore} the factory must be specified
 * in Hazelcast configuration.
 * 
 * Usage of the {@link com.hazelcast.core.MapStore} is configurable by BEEN's
 * option been.cluster.mapstore.use=[true|false].
 * 
 * @author Martin Sixta
 */
public class MongoMapStoreFactory implements MapStoreFactory {

	private static Logger log = LoggerFactory.getLogger(MongoMapStoreFactory.class);

	/**
	 * Client connection to MongoDB.
	 * 
	 * The client is thread-safe and shared among maps.
	 */
	private static MongoClient mongoClient;

	/**
	 * Initializes the client connection to MongoDB
	 * 
	 * @throws UnknownHostException
	 * @param properties
	 *          connection properties
	 */
	private static synchronized
			void
			initialize(final String dbname, final Properties properties) throws UnknownHostException {
		if (mongoClient == null) {
			final String username = properties.getProperty(MAP_STORE_DB_USERNAME);
			final String password = properties.getProperty(MAP_STORE_DB_PASSWORD);
			final String hostname = properties.getProperty(MAP_STORE_DB_HOSTNAME);

			mongoClient = new MongoClient(hostname, new MongoClientOptions.Builder().build());

			final DB db = mongoClient.getDB(dbname);

			if (username != null && !username.isEmpty() && password != null) {
				if (!db.authenticate(username, password.toCharArray())) {
					throw new RuntimeException("Failed to authenticate against MapStore database");
				}
			}
		}
	}

	@Override
	public MapLoader newMapStore(String mapName, Properties properties) {
		final String dbname = properties.getProperty(MAP_STORE_DB_NAME);

		try {
			initialize(dbname, properties);
		} catch (Exception e) {
			log.error("Cannot connect MongoClient to the database", e);
			return null;
		}

		MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, dbname);
		return new MongoMapStore(mongoTemplate);

	}
}
