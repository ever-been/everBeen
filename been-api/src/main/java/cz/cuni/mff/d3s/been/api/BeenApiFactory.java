package cz.cuni.mff.d3s.been.api;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * Factory for {@link BeenApi}.
 * 
 * @author Martin Sixta
 */
public final class BeenApiFactory {

	/**
	 * Creates {@link BeenApi} as client connection to the cluster.
	 * 
	 * @param host
	 *          Hazelcast host to connect to
	 * @param port
	 *          Port of the <code>host</code> to connect to
	 * @param groupName
	 *          Name of the Hazelcast group to connect to
	 * @param groupPassword
	 *          Password for the group
	 * @return BeenApi implementation
	 */
	public static BeenApi connect(final String host, final int port, final String groupName, final String groupPassword) {
		return new BeenApiImpl(host, port, groupName, groupPassword);
	}

	/**
	 * Creates {@link BeenApi} from existing {@link ClusterContext}.
	 * 
	 * @param clusterContext
	 *          Connection to the cluster return BeenApi implementation
	 * @return a new {@link BeenApi} object
	 */
	public static BeenApi fromContext(final ClusterContext clusterContext) {
		return new BeenApiImpl(clusterContext);
	}

}
