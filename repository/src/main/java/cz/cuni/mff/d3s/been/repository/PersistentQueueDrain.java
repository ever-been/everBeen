package cz.cuni.mff.d3s.been.repository;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Distributed queue drainer at the service of persistence layer.
 */
public final class PersistentQueueDrain extends QueueDrain<EntityCarrier> {

	private static final Logger log = LoggerFactory.getLogger(PersistentQueueDrain.class);
	private final Storage storage;

	private PersistentQueueDrain(ClusterContext ctx, String queueName, Storage storage) {
		super(ctx, queueName, storage.createPersistAction());
		this.storage = storage;
	}

	/**
	 * Create a persisting distributed queue drain
	 *
	 * @param ctx Cluster context to drain in
	 * @param queueName Name of the queue to drain
	 * @param storage Storage to use for persisting drained objects
	 *
	 * @return The persisting queue drain
	 */
	public static PersistentQueueDrain create(ClusterContext ctx, String queueName, Storage storage) {
		return new PersistentQueueDrain(ctx, queueName, storage);
	}
}
