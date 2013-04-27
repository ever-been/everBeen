package cz.cuni.mff.d3s.been.api;

import com.hazelcast.core.HazelcastInstance;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * User: donarus
 * Date: 4/27/13
 * Time: 11:50 AM
 */
public class BeenApiImpl implements BeenApi {

	private final ClusterContext clusterContext;

	public BeenApiImpl(String host, int port, String groupName, String groupPassword) {
	    HazelcastInstance instance = Instance.newNativeInstance(host, port, groupName, groupPassword);
	    clusterContext = new ClusterContext(instance);
    }

	@Override
	public Collection<TaskEntry> getTasks() {
		return clusterContext.getTasksUtils().getTasks();
	}

	@Override
	public TaskEntry getTask(String id) {
		return clusterContext.getTasksUtils().getTask(id);
	}

	@Override
	public Collection<TaskContextEntry> getTaskContexts() {
		return clusterContext.getTaskContextsUtils().getTaskContexts();
	}

	@Override
	public TaskContextEntry getTaskContext(String id) {
		return clusterContext.getTaskContextsUtils().getTaskContext(id);
	}
}
