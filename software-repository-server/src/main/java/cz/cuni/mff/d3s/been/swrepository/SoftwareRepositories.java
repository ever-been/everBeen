package cz.cuni.mff.d3s.been.swrepository;

import java.net.InetSocketAddress;
import java.util.Properties;

import cz.cuni.mff.d3s.been.core.PropertyReader;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.datastore.SoftwareStore;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;

/**
 * Utility class for creating SoftwareRepository instances.
 * 
 * The code is here to enable sharing.
 * 
 * @author Martin Sixta
 */
public class SoftwareRepositories {

    private static final int DEFAULT_SWREPO_PORT = 8000;

	private static final Logger log = LoggerFactory.getLogger(SoftwareRepositories.class);

	/**
	 * Creates a new {@link SoftwareRepository}.
	 * 
	 * @param ctx
	 *          Cluster context used to register the service.
	 *
	 * @return {@link SoftwareRepository} ready to be started.
	 */
	public static SoftwareRepository createSWRepository(ClusterContext ctx, Properties properties) {
        final PropertyReader propReader = PropertyReader.on(properties);
		SoftwareRepository swRepo = new SoftwareRepository(ctx);

		final SoftwareStore dataStore = SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().withProperties(properties).buildServer();
        final InetSocketAddress clusterSockAddr = ctx.getLocalMember().getInetSocketAddress();
        final InetSocketAddress mySockAddr = new InetSocketAddress(clusterSockAddr.getAddress(), propReader.getInteger("swrepository.port", DEFAULT_SWREPO_PORT));
		HttpServer httpServer = new HttpServer(mySockAddr);

		swRepo.setDataStore(dataStore);
		swRepo.setHttpServer(httpServer);

		return swRepo;
	}
}
