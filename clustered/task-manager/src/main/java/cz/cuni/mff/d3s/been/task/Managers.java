package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.HazelcastInstance;
import cz.cuni.mff.d3s.been.cluster.IClusterService;

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
	private static IClusterService clusterManager;

	public static IClusterService getManager(HazelcastInstance instance) {
		if (clusterManager == null) {
			clusterManager = new ClusterManager(instance);
		}

		return clusterManager;
	}
}