package cz.cuni.mff.d3s.been.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

public class StopClusterServiceHook extends Thread {

	private static final Logger log = LoggerFactory.getLogger(StopClusterServiceHook.class);

	/** Service to shutdown on SIGINT */
	private final IClusterService service;
	/** Hazelcast instance to shutdown on SIGINT */
	private final HazelcastInstance instance;

	/**
	 * Create a shutdown hook that catches SIGINT and correctly shuts down first
	 * the {@link IClusterService}, then the {@link HazelcastInstance}.
	 * 
	 * @param service
	 * @param inst
	 */
	public StopClusterServiceHook(
			IClusterService service,
			HazelcastInstance instance) {
		this.service = service;
		this.instance = instance;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		log.info("Stopping BEEN service {}", service.toString());
		service.stop();
		log.info("Stopping Hazelczast instance {}", instance.toString());
		instance.getLifecycleService().shutdown();
		log.info("Service stopped.");
	}
}
