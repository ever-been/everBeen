package cz.cuni.mff.d3s.been.manager;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * Factory for manager implementations.
 * 
 * @author Martin Sixta
 */
public final class Managers {
	private static IClusterService clusterManager;

	/**
	 * Returns Task Manager implementation as a service.
	 * 
	 * @param ctx
	 *          connection to the cluster.
	 * 
	 * @return Task Manager implementation as a service
	 */
	public static IClusterService getManager(ClusterContext ctx) {
		if (clusterManager == null) {
			clusterManager = new ClusterManager(ctx);
		}

		return clusterManager;
	}
}
