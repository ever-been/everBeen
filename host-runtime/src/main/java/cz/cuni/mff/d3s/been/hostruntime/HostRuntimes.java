package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.util.UUID;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.core.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.detectors.Detector;
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
	public static synchronized HostRuntime getRuntime(HazelcastInstance hazelcastInstance) {
		if (hostRuntime == null) {
            ClusterContext clusterContext = new ClusterContext(hazelcastInstance);
            // FIXME Tadeas - temporary situated to /tmp/hostRuntime ... figure out later
            File cepositoryCacheFolder = new File("/tmp/hostRuntime");
            SwRepoClientFactory swRepoClientFactory = new SwRepoClientFactory(cepositoryCacheFolder);

			RuntimeInfo info = newRuntimeInfo(clusterContext);
			hostRuntime = new HostRuntime(clusterContext, swRepoClientFactory, info);
		}
		return hostRuntime;
	}

    /**
     * Creates new {@link RuntimeInfo} and initializes all possible values.
     *
     * @return initialized RuntimeInfo
     */
    public static RuntimeInfo newRuntimeInfo(ClusterContext clusterContext) {
        RuntimeInfo ri = new RuntimeInfo();

        String nodeId = UUID.randomUUID().toString();
        ri.setId(nodeId);

        ri.setPort(clusterContext.getPort());
        ri.setHost(clusterContext.getHostName());

        Detector detector = new Detector();
        detector.detectAll(ri);

        return ri;
    }
}
