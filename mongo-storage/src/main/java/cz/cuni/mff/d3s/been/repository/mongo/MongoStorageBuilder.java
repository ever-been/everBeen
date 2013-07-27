package cz.cuni.mff.d3s.been.repository.mongo;

import java.net.UnknownHostException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClientOptions;

import cz.cuni.mff.d3s.been.storage.Storage;
import cz.cuni.mff.d3s.been.storage.StorageBuilder;
import cz.cuni.mff.d3s.been.storage.StorageBuilderFactory;

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

	private static final String DEFAULT_HOSTNAME = "localhost";
	private static final String DEFAULT_DBNAME = "BEEN";

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
		final String user = properties.getProperty("mongodb.user");
		final String password = properties.getProperty("mongodb.password");
		final String hostname = properties.getProperty("mongodb.hostname");
		final String dbname = properties.getProperty("mongodb.dbname");

		try {
			if (user != null && password != null) {
				// if authentication is configured, use it
				return MongoStorage.create((hostname != null) ? hostname : DEFAULT_HOSTNAME, (dbname != null) ? dbname
						: DEFAULT_DBNAME, user, password, new MongoClientOptions.Builder().build());
			} else {
				// otherwise don't use authentication
				return MongoStorage.create((hostname != null) ? hostname : DEFAULT_HOSTNAME, (dbname != null) ? dbname
						: DEFAULT_DBNAME, new MongoClientOptions.Builder().build());
			}
		} catch (UnknownHostException e) {
			return null;
		}
	}
}
