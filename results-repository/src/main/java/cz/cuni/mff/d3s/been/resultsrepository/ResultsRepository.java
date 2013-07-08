package cz.cuni.mff.d3s.been.resultsrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.persistence.queue.QueueDrain;
import cz.cuni.mff.d3s.been.storage.Storage;

/**
 * A cluster node that can store and provide BPKs and Maven artifacts through a
 * simple HTTP server.
 * 
 * @author darklight
 * 
 */
public class ResultsRepository extends QueueDrain<EntityCarrier> implements IClusterService {

	/** Results repository logger */
	private static final Logger log = LoggerFactory.getLogger(ResultsRepository.class);

	/** The cluster instance in which this repository is running */
	private final ClusterContext clusterCtx;
	/** Data storage layer */
	private final Storage storage;

	ResultsRepository(ClusterContext clusterCtx, Storage storage) {
		super(clusterCtx, Names.RESULT_QUEUE_NAME, storage.createPersistAction());
		this.clusterCtx = clusterCtx;
		this.storage = storage;
	}

	public static ResultsRepository create(ClusterContext ctx, Storage storage) {
		return new ResultsRepository(ctx, storage);
	}

	@Override
	public void start() throws ServiceException {

		log.info("Starting results repository...");
		if (storage == null) {
			throw new ServiceException("Results Repository persistence layer is unavailable.");
		}
		storage.start();
		super.start();
		log.info("Results repository successfully started!");
	}
	@Override
	public void stop() {
		log.info("Stopping results repository...");
		super.stop();
		storage.stop();
		log.info("Results repository stopped.");
	}

	@Override
	public Reaper createReaper() {
		final Reaper reaper = new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				ResultsRepository.this.stop();
			}
		};
		return reaper;
	}
}
