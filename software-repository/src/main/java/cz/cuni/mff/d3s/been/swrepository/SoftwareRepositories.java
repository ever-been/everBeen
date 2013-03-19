package cz.cuni.mff.d3s.been.swrepository;

import java.net.InetAddress;
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
	 * Creates a new {@link SoftwareRepository}.
	 * 
	 * @param ctx
	 *          Cluster context used to register the service.
	 * @param host
	 *          Host on which to listen for requests
	 * @param port
	 *          Port on which to listen for requests
	 * 
	 * @return SoftwareRepository ready to be started.
	 */
	public static SoftwareRepository createSWRepository(ClusterContext ctx,
			String host, int port) {
		SoftwareRepository swRepo = new SoftwareRepository(ctx);

		SoftwareStore dataStore = SoftwareStoreFactory.getDataStore();

		InetAddress myAddr = null;
		try {
			myAddr = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			String msg = String.format("Software Repository could not start: Failed to resolve local address %s. Cause was: %s", host, e.getMessage());

			log.error(msg);
			throw new IllegalArgumentException(msg);

		}

		HttpServer httpServer = new HttpServer(myAddr, port);
		swRepo.setDataStore(dataStore);
		swRepo.setHttpServer(httpServer);

		return swRepo;
	}
}
