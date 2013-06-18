package cz.cuni.mff.d3s.been.storage;

import java.util.Properties;
import java.util.ServiceLoader;

/**
 * A builder for {@link Storage} implementations. This interface gets loaded
 * with {@link ServiceLoader}. Any implementations of {@link Storage} need to
 * override this builder to provide their implementation, otherwise the
 * implementation will not be visible to BEEN.
 * 
 * @author darklight
 * 
 */
public interface StorageBuilder {
	/**
	 * Return an identical builder except for properties passed in the argument.
	 * The properties argument may contain values that are not specific to
	 * {@link Storage}.
	 * 
	 * @param properties
	 *          {@link Properties} available to configure this builder
	 * 
	 * @return An equivalent builder with updated properties.
	 */
	StorageBuilder withProperties(Properties properties);

	/**
	 * Build the storage using this builder's current configuration.
	 * 
	 * @return The {@link Storage}
	 */
	Storage build();
}
