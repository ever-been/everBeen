package cz.cuni.mff.d3s.been.datastore;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataStoreFactory {
	private static final Logger log = LoggerFactory.getLogger(DataStoreFactory.class);

	/**
	 * Dynamically load a DataStore according to current classpath.
	 * 
	 * @return The DataStore
	 */
	public static DataStore getDataStore() {
		ServiceLoader<DataStore> dataStoreLoader = ServiceLoader.load(DataStore.class);
		Iterator<DataStore> dsit = dataStoreLoader.iterator();

		if (!dsit.hasNext()) {
			log.error(String.format("Could not find implementation for %s. Software repository will not start.", DataStore.class.toString()));
			return null;
		}
		return dsit.next();
	}
}
