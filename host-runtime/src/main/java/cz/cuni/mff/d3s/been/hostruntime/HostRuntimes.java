package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.util.UUID;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.core.ClusterContext;
import cz.cuni.mff.d3s.been.core.RuntimeInfoUtils;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * @author Martin Sixta
 */
// FIXME Martin Sixta .. why it is named HostRuntimes (name is misleading)
public class HostRuntimes {

	private static HostRuntime hostRuntime = null;

	/**
	 * This method returns singleton instance of {@link HostRuntime}. If runtime
	 * doesn't exists, this method creates one.
	 * 
	 * @param hazelcastInstance
	 * @return
	 */
	public static synchronized HostRuntime getRuntime(
			HazelcastInstance hazelcastInstance) {
		if (hostRuntime == null) {
			RuntimeInfoUtils runtimeInfoUtils = new RuntimeInfoUtils();
			// FIXME Tadeas - temporary situated to /tmp/hostRuntime ... figure out later
			File cepositoryCacheFolder = new File("/tmp/hostRuntime");
			SwRepoClientFactory swRepoClientFactory = new SwRepoClientFactory(cepositoryCacheFolder);
			String nodeId = UUID.randomUUID().toString();
			RuntimeInfo info = runtimeInfoUtils.newInfo(nodeId);
			ClusterContext clusterContext = new ClusterContext(hazelcastInstance);
			hostRuntime = new HostRuntime(clusterContext, swRepoClientFactory, info);
		}
		return hostRuntime;
	}
}
