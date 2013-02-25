package cz.cuni.mff.d3s.been.swrepository;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.NodeType;
import cz.cuni.mff.d3s.been.core.ClusterUtils;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;

/**
 * 
 * @author donarus
 * 
 */
public class SoftwareRepositoryRunner {
	private static final Logger log = LoggerFactory.getLogger(SoftwareRepositoryRunner.class);

	/**
	 * Run a software repository node from command-line.
	 * 
	 * @param args
	 *          None recognized
	 */
	public static void main(String[] args) {
		SoftwareRepository swRepo = new SoftwareRepository();
		HazelcastInstance inst = Instance.newInstance(NodeType.DATA); // TODO change to lite

		ServiceLoader<DataStore> dataStoreLoader = ServiceLoader.load(DataStore.class);
		Iterator<DataStore> dsit = dataStoreLoader.iterator();

		if (!dsit.hasNext()) {
			log.error(String.format("Could not find implementation for %s. Software repository will not start.", DataStore.class.toString()));
			inst.getLifecycleService().shutdown();
			return;
		}
		DataStore dataStore = dsit.next();

		// FIXME port configuration
		// FIXME store the instance somewhere
		HttpServer httpServer = new HttpServer(ClusterUtils.getInetSocketAddress().getAddress(), 8000);
		swRepo.setDataStore(dataStore);
		swRepo.setHttpServer(httpServer);
		swRepo.init();
		swRepo.start();
	}
}
