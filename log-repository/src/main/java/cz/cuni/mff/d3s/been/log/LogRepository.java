package cz.cuni.mff.d3s.been.log;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.persistence.queue.QueueDrain;
import cz.cuni.mff.d3s.been.storage.Storage;

/**
 * A log drainpipe (persisting sink).
 */
public class LogRepository extends QueueDrain<EntityCarrier> implements IClusterService {
	private final Storage storage;
	private final ClusterContext ctx;

	LogRepository(ClusterContext ctx, Storage storage) {
		super(ctx, Names.LOG_QUEUE_NAME, storage.createPersistAction());
		this.ctx = ctx;
		this.storage = storage;
	}

	public static LogRepository create(ClusterContext ctx, Storage storage) {
		return new LogRepository(ctx, storage);
	}
	@Override
	public void start() throws ServiceException {
		storage.start();
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		storage.stop();
	}

	@Override
	public Reaper createReaper() {
		return new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				LogRepository.this.stop();
			}
		};
	}
}
