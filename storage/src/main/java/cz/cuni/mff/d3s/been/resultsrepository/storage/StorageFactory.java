package cz.cuni.mff.d3s.been.resultsrepository.storage;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StorageFactory {

	private static final Logger log = LoggerFactory.getLogger(StorageFactory.class);

	public static Storage createStorage() {
		Iterator<Storage> storages = ServiceLoader.load(Storage.class).iterator();
		if (!storages.hasNext()) {
			log.error(
					"Could not find implementation for {}. The Results Repository storage won't be available.",
					Storage.class.getName());
			return null;
		} else {
			return storages.next();
		}
	}

}
