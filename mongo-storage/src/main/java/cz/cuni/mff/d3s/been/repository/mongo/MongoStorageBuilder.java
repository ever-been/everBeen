package cz.cuni.mff.d3s.been.repository.mongo;

import java.net.UnknownHostException;
import java.util.Properties;

import cz.cuni.mff.d3s.been.core.PropertyReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClientOptions;

import cz.cuni.mff.d3s.been.storage.Storage;
import cz.cuni.mff.d3s.been.storage.StorageBuilder;
import cz.cuni.mff.d3s.been.storage.StorageBuilderFactory;

import static cz.cuni.mff.d3s.been.repository.mongo.MongoStorageConfiguration.*;

/**
 * A {@link StorageBuilderFactory} that creates a connection to a Mongodb
 * {@link Storage}. Accepts these parameters in properties.
 * 
 * @author darklight
 * 
 */
public class MongoStorageBuilder implements StorageBuilder {

	/** slf4j logger for this builder */
	private static final Logger log = LoggerFactory.getLogger(MongoStorageBuilder.class);
	private Properties properties = new Properties();

	@Override
	public MongoStorageBuilder withProperties(Properties properties) {
		if (properties == null) {
			log.warn("Attempt to infer null Properties to {} will be ignored.", MongoStorageBuilder.class.getName());
			return this;
		}
		this.properties = properties;
		return this;
	}

	@Override
	public Storage build() {
		final PropertyReader propertyReader = PropertyReader.on(properties);
		final String user = propertyReader.getString(MONGO_USERNAME, DEFAULT_MONGO_USERNAME);
		final String password = propertyReader.getString(MONGO_PASSWORD, DEFAULT_MONGO_PASSWORD);
		final String hostname = propertyReader.getString(MONGO_HOSTNAME, DEFAULT_MONGO_HOSTNAME);
		final String dbname = propertyReader.getString(MONGO_DBNAME, DEFAULT_MONGO_DBNAME);

		try {
			if (user != null && password != null) {
				// if authentication is configured, use it
				return MongoStorage.create(hostname, dbname, user, password, new MongoClientOptions.Builder().build());
			} else {
				// otherwise don't use authentication
				return MongoStorage.create(hostname, dbname, new MongoClientOptions.Builder().build());
			}
		} catch (UnknownHostException e) {
			return null;
		}
	}
}
