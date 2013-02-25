package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.util.UUID;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.bpk.BpkResolver;
import cz.cuni.mff.d3s.been.core.RuntimeInfoUtils;
import cz.cuni.mff.d3s.been.core.ServicesUtils;
import cz.cuni.mff.d3s.been.core.TasksUtils;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
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

			TaskEntries taskEntries = new TaskEntries();
			TasksUtils tasksUtils = new TasksUtils(taskEntries);
			RuntimeInfoUtils runtimeInfoUtils = new RuntimeInfoUtils();
			ServicesUtils servicesUtils = new ServicesUtils();
			ProcessExecutor processExecutor = new ProcessExecutor();
			BpkResolver bpkResolver = new BpkResolver();
			File cepositoryCacheFolder = new File("/tmp/hostRuntime");
			SwRepoClientFactory swRepoClientFactory = new SwRepoClientFactory(cepositoryCacheFolder);// FIXME Tadeas - temporary situated to /tmp/hostRuntime ... figure out later
			String nodeId = UUID.randomUUID().toString();
			RuntimeInfo info = runtimeInfoUtils.newInfo(nodeId);
			ZipFileUtil zipFileUtil = new ZipFileUtil();
			hostRuntime = new HostRuntime(tasksUtils, taskEntries, servicesUtils, bpkResolver, info, swRepoClientFactory, zipFileUtil, processExecutor);
		}
		return hostRuntime;
	}
}
