package cz.cuni.mff.d3s.been.swrepository;

import java.net.*;
import java.util.*;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.datastore.SoftwareStore;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;
import cz.cuni.mff.d3s.been.util.PropertyReader;

import static cz.cuni.mff.d3s.been.swrepository.SoftwareRepositoryConfiguration.*;

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
	public static SoftwareRepository createSWRepository(ClusterContext ctx, String beenId) throws SocketException, UnknownHostException {
		final Properties props = ctx.getProperties();
		final PropertyReader propReader = PropertyReader.on(props);
		SoftwareRepository swRepo = new SoftwareRepository(ctx, beenId);

		final Integer port = propReader.getInteger(PORT, DEFAULT_PORT);
		final String ifacesString = propReader.getString(INTERFACE, DEFAULT_INTERFACE);

		Set<InetSocketAddress> socketAddresses = (VALUE_INTERFACE_ALL.equals(ifacesString)) ?
				resolveSockAddrs(NetworkInterface.getNetworkInterfaces(), port) :
				resolveSockAddrs(parseNetInterfaces(ifacesString), port);

		final SoftwareStore dataStore = SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().withProperties(props).buildServer();
		HttpServer httpServer = new HttpServer(socketAddresses);

		dataStore.init();
		swRepo.setDataStore(dataStore);
		swRepo.setHttpServer(httpServer);

		return swRepo;
	}

	private static Set<InetSocketAddress> resolveSockAddrs(Enumeration<NetworkInterface> ifaces, int port) {
		final Set<InetSocketAddress> sockAddrs = new HashSet<InetSocketAddress>();
		while (ifaces.hasMoreElements()) {
			final Enumeration<InetAddress> inetAddrs = ifaces.nextElement().getInetAddresses();
			while (inetAddrs.hasMoreElements()) {
				sockAddrs.add(new InetSocketAddress(inetAddrs.nextElement(), port));
			}
		}
		return sockAddrs;
	}

	private static Enumeration<NetworkInterface> parseNetInterfaces(String netInterfacesString) throws UnknownHostException, SocketException {
		final StringTokenizer ifaceTok = new StringTokenizer(netInterfacesString, ",");
		final List<NetworkInterface> networkInterfaces = new LinkedList<NetworkInterface>();
		while (ifaceTok.hasMoreTokens()) {
			final InetAddress inetAddr = InetAddress.getByName(ifaceTok.nextToken());
			networkInterfaces.add(NetworkInterface.getByInetAddress(inetAddr));
		}
		return Collections.enumeration(networkInterfaces);
	}

}
