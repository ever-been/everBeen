package cz.cuni.mff.d3s.been.swrepository;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;

/**
 * 
 * @author donarus
 * 
 */
public class SoftwareRepositoryRunner {
	private static final Logger log = LoggerFactory
			.getLogger(SoftwareRepositoryRunner.class);

	/**
	 * Run a software repository node from command-line.
	 * 
	 * @param args None recognized
	 */
	public static void main(String[] args) {
		SoftwareRepository swRepo = new SoftwareRepository();

		ServiceLoader<DataStore> dataStoreLoader = ServiceLoader
				.load(DataStore.class);
		Iterator<DataStore> dsit = dataStoreLoader.iterator();
		if (!dsit.hasNext()) {
			log.error("Could find implementation for %s. Software repository will not start.",
					DataStore.class.toString());
			return;
		}
		DataStore dataStore = dsit.next();

		// FIXME port configuration
		HttpServer httpServer = new HttpServer(8000);
		swRepo.setDataStore(dataStore);
		swRepo.setHttpServer(httpServer);
		swRepo.init();
		swRepo.start();
	}
}
