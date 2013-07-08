package cz.cuni.mff.d3s.been.datastore;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory meant for dynamic loading of the current {@link SoftwareStoreBuilder} implementation.
 */
public final class SoftwareStoreBuilderFactory {
	private static final Logger log = LoggerFactory.getLogger(SoftwareStoreBuilderFactory.class);

	/**
	 * Dynamically load a {@link SoftwareStoreBuilder} according to current classpath.
	 * 
	 * @return The builder
	 */
	public static SoftwareStoreBuilder getSoftwareStoreBuilder() {
		ServiceLoader<SoftwareStoreBuilder> dataStoreLoader = ServiceLoader.load(SoftwareStoreBuilder.class);
		Iterator<SoftwareStoreBuilder> dsbit = dataStoreLoader.iterator();

		if (!dsbit.hasNext()) {
			log.error(String.format("Could not find implementation for %s. Software repository will not start.", SoftwareStore.class.toString()));
			return null;
		}
		return dsbit.next();
	}
}
