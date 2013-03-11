package cz.cuni.mff.d3s.been.swrepository;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

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
    @Option(name = "-h", aliases = { "--host" }, usage = "Hostname of a cluster member to connect to")
    private String host = "localhost";

    @Option(name = "-p", aliases = { "--port" }, usage = "Port of the host")
    private int port = 5701;

    @Option(name = "-gn", aliases = { "--group-name" }, usage = "Group Name")
    private String groupName = "dev";

    @Option(name = "-gp", aliases = { "--group-password" }, usage = "Group Password")
    private String groupPassword = "dev-pass";

    private static final Logger log = LoggerFactory.getLogger(SoftwareRepositoryRunner.class);

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

        Instance.newNativeInstance(host, port, groupName, groupPassword);
		ClusterContext clusterCtx = new ClusterContext(Instance.getInstance());
		SoftwareRepository swRepo = new SoftwareRepository(clusterCtx);

		// FIXME port configuration
		// FIXME store the instance somewhere
		DataStore dataStore = DataStoreFactory.getDataStore();
        HttpServer httpServer = null;
        try {
            httpServer = new HttpServer(InetAddress.getByName("localhost"), 8000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            // FIXME
        }
        swRepo.setDataStore(dataStore);
		swRepo.setHttpServer(httpServer);
		swRepo.init();
		swRepo.start();
	}
}
