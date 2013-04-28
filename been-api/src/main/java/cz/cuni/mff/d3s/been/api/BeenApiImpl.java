package cz.cuni.mff.d3s.been.api;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;

import java.io.File;
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

	@Override
	public Collection<RuntimeInfo> getRuntimes() {
		return clusterContext.getRuntimesUtils().getRuntimes();
	}

	@Override
	public RuntimeInfo getRuntime(String id) {
		return clusterContext.getRuntimesUtils().getRuntimeInfo(id);
	}

	@Override
	public Collection<String> getLogSets() {
		MultiMap<String, LogMessage> logs = clusterContext.getInstance().getMultiMap(Names.LOGS_MULTIMAP_NAME);
		return logs.keySet();
	}

	@Override
	public Collection<LogMessage> getLogs(String setId) {
		MultiMap<String, LogMessage> logs = clusterContext.getInstance().getMultiMap(Names.LOGS_MULTIMAP_NAME);
		return logs.get(setId);
	}

	@Override
	public void uploadBpk(File bpkFile) {
		// TODO
		throw new UnsupportedOperationException("lol");
	}

	@Override
	public String submitTask(TaskDescriptor taskDescriptor) {
		// TODO
		throw new UnsupportedOperationException("lol");
	}

	@Override
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor) {
		// TODO
		throw new UnsupportedOperationException("lol");
	}
}
