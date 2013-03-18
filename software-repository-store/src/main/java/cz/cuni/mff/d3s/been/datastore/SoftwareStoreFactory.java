package cz.cuni.mff.d3s.been.datastore;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SoftwareStoreFactory {
	private static final Logger log = LoggerFactory.getLogger(SoftwareStoreFactory.class);

	/**
	 * Dynamically load a DataStore according to current classpath.
	 * 
	 * @return The DataStore
	 */
	public static SoftwareStore getDataStore() {
		ServiceLoader<SoftwareStore> dataStoreLoader = ServiceLoader.load(SoftwareStore.class);
		Iterator<SoftwareStore> dsit = dataStoreLoader.iterator();

		if (!dsit.hasNext()) {
			log.error(String.format("Could not find implementation for %s. Software repository will not start.", SoftwareStore.class.toString()));
			return null;
		}
		return dsit.next();
	}
}
