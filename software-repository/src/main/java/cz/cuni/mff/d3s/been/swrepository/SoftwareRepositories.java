package cz.cuni.mff.d3s.been.swrepository;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.datastore.SoftwareStore;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreFactory;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;

/**
 * Utility class for creating SoftwareRepository instances.
 * 
 * The code is here to enable sharing.
 * 
 * @author Martin Sixta
 */
public class SoftwareRepositories {

	private static final Logger log = LoggerFactory.getLogger(SoftwareRepositories.class);

    /**
     * Create a new {@link SoftwareRepository}, pick a port at random.
     *
     * @param ctx Cluster context used to register the service
     *
     * @return {@link SoftwareRepository} ready to be started.
     */
    public static SoftwareRepository createSWRepository(ClusterContext ctx) {
        return createSWRepository(ctx, 0);
    }

	/**
	 * Creates a new {@link SoftwareRepository}.
	 * 
	 * @param ctx
	 *          Cluster context used to register the service.
	 * @param port
	 *          Port on which to listen for requests
	 * 
	 * @return {@link SoftwareRepository} ready to be started.
	 */
	public static SoftwareRepository createSWRepository(ClusterContext ctx, int port) {
		SoftwareRepository swRepo = new SoftwareRepository(ctx);

		SoftwareStore dataStore = SoftwareStoreFactory.getDataStore();
        final InetSocketAddress clusterSockAddr = ctx.getLocalMember().getInetSocketAddress();
        final InetSocketAddress mySockAddr = new InetSocketAddress(clusterSockAddr.getAddress(), port);
		HttpServer httpServer = new HttpServer(mySockAddr);
		swRepo.setDataStore(dataStore);
		swRepo.setHttpServer(httpServer);

		return swRepo;
	}
}
