package cz.cuni.mff.d3s.been.mapstore.mongodb;

import java.net.UnknownHostException;
import java.util.Properties;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;
import com.mongodb.MongoClient;

/**
 * @author Martin Sixta
 */
public class MongoMapStoreFactory implements MapStoreFactory {

	private static MongoClient mongoClient;

	private static synchronized void initialize() throws UnknownHostException {
		if (mongoClient == null) {
			mongoClient = new MongoClient("localhost");
		}
	}

	public MongoMapStoreFactory() throws UnknownHostException {
		initialize();
	}

	@Override
	public MapLoader newMapStore(String mapName, Properties properties) {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "BEEN");
		MongoMapStore mongoMapStore = new MongoMapStore(mongoTemplate);

		return mongoMapStore;

	}
}
