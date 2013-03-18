package cz.cuni.mff.d3s.been.swrepository;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.datastore.SoftwareStore;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreFactory;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;

/**
 * 
 * @author donarus
 * 
 */
public class SoftwareRepositoryRunner {
	private static final Logger log = LoggerFactory.getLogger(SoftwareRepositoryRunner.class);

	@Option(name = "-h", aliases = { "--host" }, usage = "Hostname of a cluster member to connect to")
	private final String host = "localhost";

	@Option(name = "-p", aliases = { "--port" }, usage = "Port of the host")
	private final int port = 5701;

	@Option(name = "-gn", aliases = { "--group-name" }, usage = "Group Name")
	private final String groupName = "dev";

	@Option(name = "-gp", aliases = { "--group-password" }, usage = "Group Password")
	private final String groupPassword = "dev-pass";

	@Option(name = "-tn", aliases = { "--http-name" }, usage = "Hostname for the HTTP server to bind, defaults to \"localhost\"")
	private final String httpHost = "localhost";

	@Option(name = "-tp", aliases = { "--http-port" }, usage = "Port for the HTTP server to bind, defaults to 8000")
	private final int httpPort = 8000;

	/**
	 * Run a software repository node from command-line.
	 * 
	 * @param args
	 *          None recognized
	 */
	public static void main(String[] args) {
		new SoftwareRepositoryRunner().doMain(args);
	}

	public void doMain(String[] args) {

		HazelcastInstance inst = Instance.newNativeInstance(
				host,
				port,
				groupName,
				groupPassword);
		ClusterContext clusterCtx = new ClusterContext(inst);

		SoftwareRepository swRepo = new SoftwareRepository(clusterCtx);
		SoftwareStore dataStore = SoftwareStoreFactory.getDataStore();
		InetAddress myAddr = null;
		try {
			myAddr = InetAddress.getByName(httpHost);
		} catch (UnknownHostException e) {
			log.error(
					"Software Repository could not start: Failed to resolve local address {}. Cause was: {}",
					httpHost,
					e.getMessage());
		}
		HttpServer httpServer = new HttpServer(myAddr, httpPort);
		swRepo.setDataStore(dataStore);
		swRepo.setHttpServer(httpServer);
		swRepo.init();
		swRepo.start();
	}
}
