package cz.cuni.mff.d3s.been.datastore;

import java.util.Properties;

/**
 * A builder for parametrized {@link SoftwareStore} creation
 */
public interface SoftwareStoreBuilder {

	/**
	 * Get a {@link SoftwareStoreBuilder} with passed properties
	 * 
	 * @param properties
	 *          Properties to set
	 * 
	 * @return A {@link SoftwareStoreBuilder} with new settings
	 */
	SoftwareStoreBuilder withProperties(Properties properties);

	/**
	 * Create a new server-mode {@link SoftwareStore}
	 * 
	 * @return A {@link SoftwareStore} in server mode
	 */
	SoftwareStore buildServer();

	/**
	 * Create a new cache-mode {@link SoftwareStore}
	 * 
	 * @return A {@link SoftwareStore} in cache mode
	 */
	SoftwareStore buildCache();
}
