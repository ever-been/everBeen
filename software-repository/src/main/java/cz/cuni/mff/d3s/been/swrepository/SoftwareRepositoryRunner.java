package cz.cuni.mff.d3s.been.swrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.NodeType;
import cz.cuni.mff.d3s.been.core.ClusterContext;
import cz.cuni.mff.d3s.been.datastore.DataStore;
import cz.cuni.mff.d3s.been.datastore.DataStoreFactory;
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
		HazelcastInstance inst = Instance.newInstance(NodeType.DATA); // TODO change to lite
		ClusterContext clusterCtx = new ClusterContext(inst);
		SoftwareRepository swRepo = new SoftwareRepository(clusterCtx);

		// FIXME port configuration
		// FIXME store the instance somewhere
		DataStore dataStore = DataStoreFactory.getDataStore();
		HttpServer httpServer = new HttpServer(clusterCtx.getInetSocketAddress().getAddress(), 8000);
		swRepo.setDataStore(dataStore);
		swRepo.setHttpServer(httpServer);
		swRepo.init();
		swRepo.start();
	}
}
