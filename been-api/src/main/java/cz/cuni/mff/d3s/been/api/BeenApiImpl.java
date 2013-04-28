package cz.cuni.mff.d3s.been.api;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import cz.cuni.mff.d3s.been.bpk.*;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreFactory;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
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
	public Collection<BpkIdentifier> getBpks() {
		// TODO
		return new ArrayList<>();
	}

	@Override
	public void uploadBpk(File bpkFile) throws BpkConfigurationException {
		SWRepositoryInfo swInfo = clusterContext.getServicesUtils().getSWRepositoryInfo();
		SwRepoClient client = new SwRepoClientFactory(SoftwareStoreFactory.getDataStore()).getClient(swInfo.getHost(), swInfo.getHttpServerPort());

		BpkIdentifier bpkIdentifier = new BpkIdentifier();

		BpkConfiguration bpkConfiguration = BpkResolver.resolve(bpkFile);
		MetaInf metaInf = bpkConfiguration.getMetaInf();
		bpkIdentifier.setGroupId(metaInf.getGroupId());
		bpkIdentifier.setBpkId(metaInf.getBpkId());
		bpkIdentifier.setVersion(metaInf.getVersion());

		client.putBpk(bpkIdentifier, bpkFile);
	}

	@Override
	public String submitTask(TaskDescriptor taskDescriptor) {
		TaskContextDescriptor contextDescriptor = new TaskContextDescriptor();
		Task taskInTaskContext = new Task();
		Descriptor descriptorInTaskContext = new Descriptor();
		descriptorInTaskContext.setTaskDescriptor(taskDescriptor);
		taskInTaskContext.setDescriptor(descriptorInTaskContext);
		contextDescriptor.getTask().add(taskInTaskContext);

		TaskContextEntry taskContextEntry = clusterContext.getTaskContextsUtils().submit(contextDescriptor);

		if (taskContextEntry.getContainedTask().size() == 0) {
			throw new RuntimeException("Created task context does not contain a task.");
		}

		String taskId = taskContextEntry.getContainedTask().get(0);
		return taskId;
	}

	@Override
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor) {
		TaskContextEntry taskContextEntry = clusterContext.getTaskContextsUtils().submit(taskContextDescriptor);

		return taskContextEntry.getId();
	}
}
