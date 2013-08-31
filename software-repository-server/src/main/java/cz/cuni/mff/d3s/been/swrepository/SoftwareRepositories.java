package cz.cuni.mff.d3s.been.swrepository;

import static cz.cuni.mff.d3s.been.swrepository.SoftwareRepositoryConfiguration.DEFAULT_PORT;
import static cz.cuni.mff.d3s.been.swrepository.SoftwareRepositoryConfiguration.PORT;

import java.net.InetSocketAddress;
import java.util.Properties;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.datastore.SoftwareStore;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;
import cz.cuni.mff.d3s.been.util.PropertyReader;

/**
 * Utility class for creating SoftwareRepository instances.
 * 
 * The code is here to enable sharing.
 * 
 * @author Martin Sixta
 */
public class SoftwareRepositories {

	/**
	 * Creates a new {@link SoftwareRepository}.
	 * 
	 * 
	 * @param ctx
	 *          Cluster context used to register the service.
	 * 
	 * @param beenId
	 *          unique id of been service
	 * @return {@link SoftwareRepository} ready to be started.
	 */
	public static SoftwareRepository createSWRepository(ClusterContext ctx, String beenId) {
		final Properties props = ctx.getProperties();
		final PropertyReader propReader = PropertyReader.on(props);
		SoftwareRepository swRepo = new SoftwareRepository(ctx, beenId);

		final SoftwareStore dataStore = SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().withProperties(props).buildServer();
		final InetSocketAddress clusterSockAddr = ctx.getInetSocketAddress();
		final InetSocketAddress mySockAddr = new InetSocketAddress(clusterSockAddr.getAddress(), propReader.getInteger(
				PORT,
				DEFAULT_PORT));
		HttpServer httpServer = new HttpServer(mySockAddr);

		dataStore.init();
		swRepo.setDataStore(dataStore);
		swRepo.setHttpServer(httpServer);

		return swRepo;
	}
}
