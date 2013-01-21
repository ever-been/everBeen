package cz.cuni.mff.d3s.been.hostruntime;

import com.hazelcast.core.HazelcastInstance;
import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.task.TaskRunner;

import java.util.UUID;

/**
 * @author Martin Sixta
 */
public class HostRuntimes {
	private static HostRuntime hostRuntime = null;
	private static TaskRunner taskRunner = null;
	private static String uuid = null;

	public static synchronized IClusterService getRuntime(HazelcastInstance hazelcastInstance) {
		if (hostRuntime == null) {
			taskRunner = new TaskRunner();
			uuid = UUID.randomUUID().toString();
			hostRuntime = new HostRuntime(taskRunner, uuid);
		}
		return hostRuntime;
	}
}
