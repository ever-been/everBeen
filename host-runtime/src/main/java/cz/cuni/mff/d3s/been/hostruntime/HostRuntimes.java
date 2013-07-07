package cz.cuni.mff.d3s.been.hostruntime;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.detectors.Detector;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * @author Martin Sixta
 */
// FIXME Martin Sixta .. why it is named HostRuntimes (name is misleading)
public class HostRuntimes {

	private static final String HR_DEFAULT_WRKDIR_NAME = ".HostRuntime";

	private static HostRuntime hostRuntime = null;

	/**
	 * This method returns singleton instance of {@link HostRuntime}. If runtime
	 * doesn't exists, this method creates one.
	 * 
	 * @param hazelcastInstance Hazelcast instance to build on
     * @param properties BEEN properties
     *
	 * @return A host runtime instance
	 */
	public static synchronized HostRuntime getRuntime(
			HazelcastInstance hazelcastInstance, Properties properties) {
		if (hostRuntime == null) {
			ClusterContext clusterContext = new ClusterContext(hazelcastInstance);
			SwRepoClientFactory swRepoClientFactory = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().withProperties(properties).buildCache());

			RuntimeInfo info = newRuntimeInfo(clusterContext);
			hostRuntime = new HostRuntime(clusterContext, swRepoClientFactory, info);
		}
		return hostRuntime;
	}

	/**
	 * Creates new {@link RuntimeInfo} and initializes all possible values.
	 * 
	 * @param clusterContext
	 * 
	 * @return initialized RuntimeInfo
	 */
	public static RuntimeInfo newRuntimeInfo(ClusterContext clusterContext) {
		RuntimeInfo ri = new RuntimeInfo();

		// get absolute path to ".HostRuntime" directory
		Path p = Paths.get(HR_DEFAULT_WRKDIR_NAME).toAbsolutePath();
		ri.setWorkingDirectory(p.toString());

		String nodeId = UUID.randomUUID().toString();
		ri.setId(nodeId);

		ri.setPort(clusterContext.getPort());
		ri.setHost(clusterContext.getHostName());

		Detector detector = new Detector();
		detector.detectAll(ri);
		ri.setExclusivity(TaskExclusivity.NON_EXCLUSIVE.toString());

		return ri;
	}
}
