package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.persistence.queue.PersistentQueueDrain;
import cz.cuni.mff.d3s.been.persistence.queue.QueryQueueDrain;
import cz.cuni.mff.d3s.been.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic persistence layer for BEEN entities
 */
public final class Repository implements IClusterService {

	private static final Logger log = LoggerFactory.getLogger(Repository.class);

	private final ClusterContext ctx;
	private final Storage storage;
	private PersistentQueueDrain entityDrain;
	private QueryQueueDrain queryDrain;

	private Repository(ClusterContext ctx, Storage storage) {
		this.ctx = ctx;
		this.storage = storage;
	}

	/**
	 * Build a repository over a ready (but not running) persistence layer
	 *
	 * @param ctx Cluster context to work in (provides shared queues to work with)
	 * @param storage Persistence layer to use
	 *
	 * @return The repository
	 */
	public static Repository create(ClusterContext ctx, Storage storage) {
		return new Repository(ctx, storage);
	}

	@Override
	public void start() throws ServiceException {
		log.info("Starting Repository...");
		if (storage == null) {
			throw new ServiceException("Cannot start a repository over a null Storage");
		}
		entityDrain = PersistentQueueDrain.create(ctx, Names.PERSISTENCE_QUEUE_NAME, storage);
		queryDrain = QueryQueueDrain.create(ctx, storage);
		storage.start();
		entityDrain.start();
		queryDrain.start();
		log.info("Repository started.");
	}

	@Override
	public void stop() {
		log.info("Stopping Repository...");
		entityDrain.stop();
		queryDrain.stop();
		storage.stop();
		log.info("Repository stopped.");
	}

	@Override
	public Reaper createReaper() {
		final Reaper reaper = new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				Repository.this.stop();
			}
		};
		return reaper;
	}
}
