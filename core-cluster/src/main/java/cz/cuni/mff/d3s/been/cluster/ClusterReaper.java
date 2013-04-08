package cz.cuni.mff.d3s.been.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

public class ClusterReaper extends Reaper {

	private static final Logger log = LoggerFactory.getLogger(ClusterReaper.class);

	/** Hazelcast instance to shutdown on signal */
	private final HazelcastInstance instance;

	/**
	 * Create a shutdown hook that correctly shuts down first the
	 * {@link IClusterService}s, then the {@link HazelcastInstance}.
	 * 
	 * @param service
	 * @param inst
	 */
	public ClusterReaper(HazelcastInstance instance) {
		this.instance = instance;
	}

	@Override
	protected void reap() throws InterruptedException {}

	@Override
	protected void shutdown() throws InterruptedException {
		log.info("Stopping Hazelczast instance {}", instance.toString());
		instance.getLifecycleService().shutdown();
		log.info("Cluster services stopped.");
		log.info("Instance stopped.");
	}
}
