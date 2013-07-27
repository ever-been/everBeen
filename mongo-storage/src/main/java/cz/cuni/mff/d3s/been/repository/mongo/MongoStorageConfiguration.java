package cz.cuni.mff.d3s.been.repository.mongo;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * Configuration of BEEN mongo storage
 *
 * @author darklight
 */
public class MongoStorageConfiguration extends BeenServiceConfiguration {
	private MongoStorageConfiguration() {}

	/** Property name of the username to use for MongoDB connection */
	public static final String MONGO_USERNAME = "mongodb.username";
	/** Default MongoDB user is the current user who is running BEEN */
	public static final String DEFAULT_MONGO_USERNAME = null;

	/** Property name of the password to use for MongoDB connection */
	public static final String MONGO_PASSWORD = "mongodb.password";
	/** Default password for the MongoDB connection is no password (because current user is used by default) */
	public static final String DEFAULT_MONGO_PASSWORD = null;

	/** Property name for the MongoDB hostname (full connection string including port). If no port is specified, default MongoDB port is used. */
	public static final String MONGO_HOSTNAME = "mongodb.hostname";
	/** Default MongoDB hostname is localhost on default MongoDB port. */
	public static final String DEFAULT_MONGO_HOSTNAME = "localhost";

	/** Property name for the name of the MongoDB database instance to use. */
	public static final String MONGO_DBNAME = "mongodb.dbname";
	/** Default MongoDB database instance name is 'BEEN' */
	public static final String DEFAULT_MONGO_DBNAME = "BEEN";
}
