package cz.cuni.mff.d3s.been.mapstore;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * Configuration of MapStore Hazelcast integration.
 * 
 * @author Martin Sixta
 */
public class MapStoreConfiguration implements BeenServiceConfiguration {

	/**
	 * Property name of the username to use for
	 * {@code com.hazelcast.core.MapStore} connection
	 */
	public static final String MAP_STORE_DB_USERNAME = "been.cluster.mapstore.db.username";
	/** Default value for {@link #MAP_STORE_DB_USERNAME}. */
	public static final String DEFAULT_MAP_STORE_DB_USERNAME = null;

	/**
	 * Property name of the password to use for
	 * {@code com.hazelcast.core.MapStore} connection
	 */
	public static final String MAP_STORE_DB_PASSWORD = "been.cluster.mapstore.db.password";

	/** Default value for {@link #MAP_STORE_DB_PASSWORD}. */
	public static final String DEFAULT_MAP_STORE_DB_PASSWORD = null;

	/**
	 * Property name for the {@code com.hazelcast.core.MapStore} hostname (full
	 * connection string including port). If no port is specified, default port is
	 * used.
	 */
	public static final String MAP_STORE_DB_HOSTNAME = "been.cluster.mapstore.db.hostname";
	/** Default value for {@link #MAP_STORE_DB_HOSTNAME}. */
	public static final String DEFAULT_MAP_STORE_DB_HOSTNAME = "localhost";

	/**
	 * Property name for the name of the {@code com.hazelcast.core.MapStore}
	 * database instance to use.
	 */
	public static final String MAP_STORE_DB_NAME = "been.cluster.mapstore.db.dbname";
	/** Default value for {@link #MAP_STORE_DB_NAME}. */
	public static final String DEFAULT_MAP_STORE_DB_NAME = "BEEN_MAPSTORE";
}
