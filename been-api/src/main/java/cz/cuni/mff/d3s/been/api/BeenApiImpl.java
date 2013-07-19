package cz.cuni.mff.d3s.been.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.QueryAnswer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import cz.cuni.mff.d3s.been.bpk.*;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.debugassistant.DebugListItem;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;


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



	// --------------------
	// CONFIG PERSISTENCE
	// ------------------


	@Override
	public void saveTaskDescriptor(TaskDescriptor descriptor, String taskId, String contextId, String benchmarkId) throws DAOException {
		clusterContext.getPersistence().asyncPersist(PersistentDescriptors.TASK_DESCRIPTOR, PersistentDescriptors.wrapTaskDescriptor(descriptor, taskId, contextId, benchmarkId));
	}

	@Override
	public void saveNamedTaskDescriptor(TaskDescriptor descriptor, String name, String taskId, String contextId, String benchmarkId) throws DAOException {
		clusterContext.getPersistence().asyncPersist(PersistentDescriptors.NAMED_TASK_DESCRIPTOR, PersistentDescriptors.wrapNamedTaskDescriptor(descriptor, name, taskId, contextId, benchmarkId));
	}

	@Override
	public void saveContextDescriptor(TaskContextDescriptor descriptor, String taskId, String contextId, String benchmarkId) throws DAOException {
		clusterContext.getPersistence().asyncPersist(PersistentDescriptors.CONTEXT_DESCRIPTOR, PersistentDescriptors.wrapContextDescriptor(descriptor, taskId, contextId, benchmarkId));
	}

	@Override
	public void saveNamedContextDescriptor(TaskContextDescriptor descriptor, String name, String taskId, String contextId, String benchmarkId) throws DAOException {
		clusterContext.getPersistence().asyncPersist(PersistentDescriptors.NAMED_CONTEXT_DESCRIPTOR, PersistentDescriptors.wrapNamedContextDescriptor(descriptor, name, taskId, contextId, benchmarkId));
	}




	@Override
	public Collection<String> getLogSets() {
		// TODO logs must be fetched from Results Repository
		log.warn("Logs must be fetched from Results Repository!");
		return Collections.EMPTY_LIST;
	}

	@Override
	public void addLogListener(final LogListener listener) {
		EntryListener<String, String> logsListener = new EntryListener<String, String>() {
			@Override
			public void entryAdded(EntryEvent<String, String> event) {
				listener.logAdded(event.getValue());
			}

			@Override
			public void entryRemoved(EntryEvent<String, String> event) {}

			@Override
			public void entryUpdated(EntryEvent<String, String> event) {
				listener.logAdded(event.getValue());
			}

			@Override
			public void entryEvicted(EntryEvent<String, String> event) {}
		};

		clusterContext.<String, String> getMap(Names.LOGS_TASK_MAP_NAME).addEntryListener(logsListener, true);
	}

	@Override
	public void removeLogListener(LogListener listener) {
		// TODO
	}

	@Override
	public Collection<LogMessage> getLogs(String setId) {
		// TODO logs must be fetched from Results Repository
		log.warn("Logs must be fetched from Results Repository!");
		return Collections.EMPTY_LIST;
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

	@Override
	public QueryAnswer queryPersistence(Query query) {
		try {
			return clusterContext.getPersistence().query(query);
		} catch (DAOException e) {
			log.error("Interrupted when trying to execute persistence query '{}'", query.toString(), e);
			return null;
		}
	}

}
