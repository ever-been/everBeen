package cz.cuni.mff.d3s.been.storage;

import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * Dynamic factory for persistence layer.
 * 
 * @author darklight
 * 
 */
public final class StorageBuilderFactory {

	private Properties properties;

	protected final Properties getProperties() {
		return properties;
	}

	/**
	 * Dynamically load the first {@link Storage} implementation found on the
	 * classpath. Use default {@link Properties}.
	 * 
	 * @return A {@link Storage} instance or null if none is found on the
	 *         classpath.
	 * 
	 * @throws StorageException
	 *           When no implementation of {@link StorageBuilderFactory} is found
	 *           on the classpath
	 */
	public static StorageBuilder createBuilder() throws StorageException {
		return createBuilder(new Properties());
	}
	/**
	 * Dynamically load the first {@link Storage} implementation found on the
	 * classpath. Use custom {@link Properties}.
	 * 
	 * @param properties
	 *          Properties to pass to the {@link Storage} implementation.
	 * 
	 * @return A {@link Storage} instance or null if none is found on the
	 *         classpath
	 * 
	 * @throws StorageException
	 *           When no implementation of {@link StorageBuilderFactory} is found
	 *           on the classpath
	 */
	public static StorageBuilder createBuilder(Properties properties) throws StorageException {
		Iterator<StorageBuilder> storageFactories = ServiceLoader.load(StorageBuilder.class).iterator();
		if (!storageFactories.hasNext()) {
			throw new StorageException(String.format(
					"Could not find implementation for %s. Storage won't be available.",
					StorageBuilder.class.getName()));
		} else {
			StorageBuilder builder = storageFactories.next();
			return builder.withProperties(properties);
		}
	}
}
