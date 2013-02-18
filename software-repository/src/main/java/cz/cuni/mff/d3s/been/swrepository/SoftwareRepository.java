package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
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

	SoftwareRepository() {
	}
	
	/**
	 * Initialize the repository. HTTP server and data store must be set.
	 */
	public void init() {
		httpServer.getResolver().register("/bpk*",
				new BpkRequestHandler(dataStore));
		httpServer.getResolver().register("/artifact*",
				new ArtifactRequestHandler(dataStore));
	}

	@Override
	public void start() {
		httpServer.start();
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Set the HTTP server
	 * 
	 * @param httpServer
	 *            HTTP server to set
	 */
	public void setHttpServer(HttpServer httpServer) {
		this.httpServer = httpServer;
	}

	/**
	 * Set the persistence layer
	 * 
	 * @param dataStore
	 *            Data store to set
	 */
	public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}
}
