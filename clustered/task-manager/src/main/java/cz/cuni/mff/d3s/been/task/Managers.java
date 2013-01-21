package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.HazelcastInstance;

/**
 * Static factory for the IManager implementation.
 *
 * Goal: hide implementation of the ClusterManager
 *
 *  TODO: The implementation is suboptimal:
 * 	- the creation is not thread safe
 * 	- getManager takes an instance, what if the instance is different than the first one?
 *
 * @author Martin Sixta
 */
public final class Managers {
	private static IManager clusterManager;

	public static IManager getManager(HazelcastInstance instance) {
		if (clusterManager == null) {
			clusterManager = new ClusterManager(instance);
		}

		return clusterManager;
	}
}