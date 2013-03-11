package cz.cuni.mff.d3s.been.swrepository;

import java.net.InetAddress;
import java.net.UnknownHostException;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.ClusterContext;
import cz.cuni.mff.d3s.been.core.Names;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.datastore.DataStore;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;

/**
 * A cluster node that can store and provide BPKs and Maven artifacts through a
 * simple HTTP server.
 * 
 * @author darklight
 * 
 */
public class SoftwareRepository implements IClusterService {

	private HttpServer httpServer;
	private DataStore dataStore;
	private SWRepositoryInfo info;
	private final ClusterContext clusterCtx;

	SoftwareRepository(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
	}

	/**
	 * Initialize the repository. HTTP server and data store must be set.
	 */
	public void init() {
		httpServer.getResolver().register("/bpk*", new BpkRequestHandler(dataStore));
		httpServer.getResolver().register("/artifact*", new ArtifactRequestHandler(dataStore));
	}

	@Override
	public void start() {
		if (httpServer == null) {
			// TODO don't start
			return;
		}
		if (dataStore == null) {
			// TODO don't start
			return;
		}
		info = new SWRepositoryInfo();
		try {
			info.setHost(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO don't start
			return;
		}
		info.setHttpServerPort(httpServer.getPort());
		httpServer.start();
		clusterCtx.registerService(Names.SWREPOSITORY_SERVICES_MAP_KEY, info);
	}

	@Override
	public void stop() {
		clusterCtx.unregisterService(Names.SWREPOSITORY_SERVICES_MAP_KEY);
		httpServer.stop();
	}

	/**
	 * Set the HTTP server
	 * 
	 * @param httpServer
	 *          HTTP server to set
	 */
	public void setHttpServer(HttpServer httpServer) {
		this.httpServer = httpServer;
	}

	/**
	 * Set the persistence layer
	 * 
	 * @param dataStore
	 *          Data store to set
	 */
	public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}
}
