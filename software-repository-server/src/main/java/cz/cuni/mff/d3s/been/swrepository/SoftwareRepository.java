package cz.cuni.mff.d3s.been.swrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.datastore.SoftwareStore;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;

/**
 * A cluster node that can store and provide BPKs and Maven artifacts through a
 * simple HTTP server.
 * 
 * @author darklight
 * 
 */
public class SoftwareRepository implements IClusterService {

	private static final Logger log = LoggerFactory.getLogger(SoftwareRepository.class);

	private HttpServer httpServer;
	private SoftwareStore softwareStore;
	private SWRepositoryInfo info;
	private final ClusterContext clusterCtx;

	SoftwareRepository(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
	}

	/**
	 * Initialize the repository. HTTP server and data store must be set.
	 */
	public void init() {
		BpkRequestHandler bpkRequestHandler = new BpkRequestHandler(softwareStore);
		httpServer.getResolver().register(UrlPaths.BPK_LIST_URI, bpkRequestHandler);
		httpServer.getResolver().register(UrlPaths.BPK_URI, bpkRequestHandler);
		httpServer.getResolver().register(UrlPaths.TASK_DESCRIPTOR_LIST_URI, bpkRequestHandler);
		httpServer.getResolver().register(UrlPaths.TASK_CONTEXT_DESCRIPTOR_LIST_URI, bpkRequestHandler);
		httpServer.getResolver().register(UrlPaths.ARTIFACT_URI, new ArtifactRequestHandler(softwareStore));
	}

	@Override
	public void start() throws ServiceException {
		if (httpServer == null) {
			log.error("Cannot start Software Repository - HTTP server is null.");
			return;
		}
		if (softwareStore == null) {
			log.error("Cannot start Software Repository - Software Store is null.");
			return;
		}

		info = new SWRepositoryInfo();
		info.setHost(httpServer.getHost().getHostName());
		info.setHttpServerPort(httpServer.getPort());

		httpServer.start();
		clusterCtx.registerService(Names.SWREPOSITORY_SERVICES_MAP_KEY, info);
	}

	@Override
	public void stop() {
		try {
			clusterCtx.unregisterService(Names.SWREPOSITORY_SERVICES_MAP_KEY);
		} catch (IllegalStateException e) {
			// unregistering over a Hazelcast instance that is no longer active
			log.warn(
					"Failed to unhook SoftwareRepository from the cluster. SoftwareRepository info is likely to linger.",
					e);
		}
		httpServer.stop();
	}

	@Override
	public Reaper createReaper() {
		return new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				SoftwareRepository.this.stop();
			}
		};
	}

	/**
	 * Set the HTTP server
	 *
	 * @param httpServer HTTP server to set
	 */
	public void setHttpServer(HttpServer httpServer) {
		this.httpServer = httpServer;
	}

	/**
	 * Set the persistence layer
	 *
	 * @param softwareStore Data store to set
	 */
	public void setDataStore(SoftwareStore softwareStore) {
		this.softwareStore = softwareStore;
	}
}
