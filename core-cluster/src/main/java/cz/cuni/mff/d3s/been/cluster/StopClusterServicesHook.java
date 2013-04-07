package cz.cuni.mff.d3s.been.cluster;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

public class StopClusterServicesHook extends Thread {

	private static final Logger log = LoggerFactory.getLogger(StopClusterServicesHook.class);

	/** Service to shutdown on SIGINT */
	private final Stack<IClusterService> services;
	/** Hazelcast instance to shutdown on SIGINT */
	private final HazelcastInstance instance;

	/**
	 * Create a shutdown hook that catches SIGINT and correctly shuts down first
	 * the {@link IClusterService}, then the {@link HazelcastInstance}.
	 * 
	 * @param service
	 * @param inst
	 */
	public StopClusterServicesHook(
			Stack<IClusterService> services,
			HazelcastInstance instance) {
		this.services = services;
		this.instance = instance;
	}

	@Override
	public void run() {
		super.run();
		while (!services.isEmpty()) {
			services.pop().stop();
		}
		log.info("Stopping Hazelczast instance {}", instance.toString());
		instance.getLifecycleService().shutdown();
		log.info("Cluster services stopped.");
	}
}
