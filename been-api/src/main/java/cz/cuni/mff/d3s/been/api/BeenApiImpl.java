package cz.cuni.mff.d3s.been.api;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import cz.cuni.mff.d3s.been.bpk.*;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.debugassistant.DebugListItem;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * User: donarus Date: 4/27/13 Time: 11:50 AM
 */
public class BeenApiImpl implements BeenApi {

	private static final Logger log = LoggerFactory.getLogger(BeenApiImpl.class);

	private final ClusterContext clusterContext;

	public BeenApiImpl(String host, int port, String groupName, String groupPassword) {
		HazelcastInstance instance = Instance.newNativeInstance(host, port, groupName, groupPassword);
		clusterContext = new ClusterContext(instance);
	}

	public BeenApiImpl(ClusterContext clusterContext) {
		this.clusterContext = clusterContext;
	}

	@Override
	public void shutdown() {
		Instance.shutdown();
	}

	@Override
	public Collection<TaskEntry> getTasks() {
		return clusterContext.getTasks().getTasks();
	}

	@Override
	public TaskEntry getTask(String id) {
		return clusterContext.getTasks().getTask(id);
	}

	@Override
	public Collection<TaskContextEntry> getTaskContexts() {
		return clusterContext.getTaskContexts().getTaskContexts();
	}

	@Override
	public TaskContextEntry getTaskContext(String id) {
		return clusterContext.getTaskContexts().getTaskContext(id);
	}

	@Override
	public Collection<BenchmarkEntry> getBenchmarks() {
		return clusterContext.getBenchmarks().getBenchmarksMap().values();
	}

	@Override
	public BenchmarkEntry getBenchmark(String id) {
		return clusterContext.getBenchmarks().get(id);
	}

	@Override
	public Collection<TaskContextEntry> getTaskContextsInBenchmark(String benchmarkId) {
		return clusterContext.getBenchmarks().getTaskContextsInBenchmark(benchmarkId);
	}

	@Override
	public Collection<TaskEntry> getTasksInTaskContext(String taskContextId) {
		return clusterContext.getTaskContexts().getTasksInTaskContext(taskContextId);
	}

	@Override
	public Collection<RuntimeInfo> getRuntimes() {
		return clusterContext.getRuntimes().getRuntimes();
	}

	@Override
	public RuntimeInfo getRuntime(String id) {
		return clusterContext.getRuntimes().getRuntimeInfo(id);
	}

	@Override
	public Collection<String> getLogSets() {
		MultiMap<String, LogMessage> logs = clusterContext.getInstance().getMultiMap(Names.LOGS_MULTIMAP_NAME);
		return logs.keySet();
	}

	@Override
	public void addLogListener(final LogListener listener) {
		// TODO, refactor
		EntryListener<String, LogMessage> entryListener = new EntryListener<String, LogMessage>() {

			@Override
			public void entryAdded(EntryEvent<String, LogMessage> event) {
				listener.logAdded(event.getValue());
			}

			@Override
			public void entryRemoved(EntryEvent<String, LogMessage> event) {}
			@Override
			public void entryUpdated(EntryEvent<String, LogMessage> event) {}
			@Override
			public void entryEvicted(EntryEvent<String, LogMessage> event) {}
		};
		MultiMap<String, LogMessage> logs = clusterContext.getInstance().getMultiMap(Names.LOGS_MULTIMAP_NAME);
		logs.addEntryListener(entryListener, true);
	}

	@Override
	public void removeLogListener(LogListener listener) {
		// TODO
	}

	@Override
	public Collection<LogMessage> getLogs(String setId) {
		MultiMap<String, LogMessage> logs = clusterContext.getInstance().getMultiMap(Names.LOGS_MULTIMAP_NAME);
		return logs.get(setId);
	}

	@Override
	public Collection<BpkIdentifier> getBpks() {
		SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
		SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
				swInfo.getHost(),
				swInfo.getHttpServerPort());
		return client.listBpks();
	}

	@Override
	public void uploadBpk(InputStream bpkInputStream) throws BpkConfigurationException {
		SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
		SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
				swInfo.getHost(),
				swInfo.getHttpServerPort());

		BpkIdentifier bpkIdentifier = new BpkIdentifier();

		ByteArrayOutputStream tempStream = new ByteArrayOutputStream();

		try {
			IOUtils.copy(bpkInputStream, tempStream);
		} catch (IOException e) {
			log.error("Cannot upload BPK.", e);
			return;
		}

		ByteArrayInputStream tempInputStream = new ByteArrayInputStream(tempStream.toByteArray());

		BpkConfiguration bpkConfiguration = BpkResolver.resolve(tempInputStream);
		MetaInf metaInf = bpkConfiguration.getMetaInf();
		bpkIdentifier.setGroupId(metaInf.getGroupId());
		bpkIdentifier.setBpkId(metaInf.getBpkId());
		bpkIdentifier.setVersion(metaInf.getVersion());

		tempInputStream.reset();

		client.putBpk(bpkIdentifier, tempInputStream);
	}

	@Override
	public InputStream downloadBpk(BpkIdentifier bpkIdentifier) {
		SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
		SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
				swInfo.getHost(),
				swInfo.getHttpServerPort());

		Bpk bpk = client.getBpk(bpkIdentifier);
		try {
			return bpk.getInputStream();
		} catch (IOException e) {
			log.error("Cannot get input stream from BPK.", e);
			return null;
		}
	}

	@Override
	public String submitTask(TaskDescriptor taskDescriptor) {
		return clusterContext.getTaskContexts().submitTaskInNewContext(taskDescriptor);
	}

	@Override
	public void killTask(String taskId) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor) {
		return clusterContext.getTaskContexts().submit(taskContextDescriptor, null);
	}

	@Override
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor, String benchmarkId) {
		return clusterContext.getTaskContexts().submit(taskContextDescriptor, benchmarkId);
	}

	@Override
	public void killTaskContext(String taskId) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public void removeTaskEntry(String taskId) {
		clusterContext.getTasks().remove(taskId);
	}

	@Override
	public void removeTaskContextEntry(String taskContextId) {
		clusterContext.getTaskContexts().remove(taskContextId);
	}

	@Override
	public void removeBenchmarkEntry(String benchmarkId) {
		clusterContext.getBenchmarks().remove(benchmarkId);
	}

	@Override
	public String submitBenchmark(TaskDescriptor benchmarkTaskDescriptor) {
		return clusterContext.getBenchmarks().submit(benchmarkTaskDescriptor);
	}

	@Override
	public Collection<DebugListItem> getDebugWaitingTasks() {
		DebugAssistant debugAssistant = new DebugAssistant(clusterContext);
		return debugAssistant.listWaitingProcesses();
	}

	@Override
	public Map<String, TaskDescriptor> getTaskDescriptors(BpkIdentifier bpkIdentifier) {
		SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
		SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
				swInfo.getHost(),
				swInfo.getHttpServerPort());

		return client.listTaskDescriptors(bpkIdentifier);
	}

	@Override
	public TaskDescriptor getTaskDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) {
		return getTaskDescriptors(bpkIdentifier).get(descriptorName);
	}

	@Override
	public Map<String, TaskContextDescriptor> getTaskContextDescriptors(BpkIdentifier bpkIdentifier) {
		SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
		SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
				swInfo.getHost(),
				swInfo.getHttpServerPort());

		return client.listTaskContextDescriptors(bpkIdentifier);
	}

	@Override
	public TaskContextDescriptor getTaskContextDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) {
		return getTaskContextDescriptors(bpkIdentifier).get(descriptorName);
	}

}
