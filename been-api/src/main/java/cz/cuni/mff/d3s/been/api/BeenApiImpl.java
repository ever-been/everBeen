package cz.cuni.mff.d3s.been.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
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
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreFactory;
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
		SwRepoClient client = new SwRepoClientFactory(SoftwareStoreFactory.getDataStore()).getClient(
				swInfo.getHost(),
				swInfo.getHttpServerPort());
		return client.listBpks();
	}

	@Override
	public void uploadBpk(InputStream bpkInputStream) throws BpkConfigurationException {
		SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
		SwRepoClient client = new SwRepoClientFactory(SoftwareStoreFactory.getDataStore()).getClient(
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
		SwRepoClient client = new SwRepoClientFactory(SoftwareStoreFactory.getDataStore()).getClient(
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
	public void deleteBpk(BpkIdentifier bpkIdentifier) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public String submitTask(TaskDescriptor taskDescriptor) {
		TaskContextDescriptor contextDescriptor = new TaskContextDescriptor();
		Task taskInTaskContext = new Task();
		taskInTaskContext.setName(taskDescriptor.getName());
		Descriptor descriptorInTaskContext = new Descriptor();
		descriptorInTaskContext.setTaskDescriptor(taskDescriptor);
		taskInTaskContext.setDescriptor(descriptorInTaskContext);
		contextDescriptor.getTask().add(taskInTaskContext);

		TaskContextEntry taskContextEntry = clusterContext.getTaskContexts().submit(contextDescriptor);

		if (taskContextEntry.getContainedTask().size() == 0) {
			throw new RuntimeException("Created task context does not contain a task.");
		}

		String taskId = taskContextEntry.getContainedTask().get(0);
		return taskId;
	}

	@Override
	public void killTask(String taskId) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor) {
		TaskContextEntry taskContextEntry = clusterContext.getTaskContexts().submit(taskContextDescriptor);

		return taskContextEntry.getId();
	}

	@Override
	public void killTaskContext(String taskId) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public Collection<DebugListItem> getDebugWaitingTasks() {
		DebugAssistant debugAssistant = new DebugAssistant(clusterContext);
		return debugAssistant.listWaitingProcesses();
	}

	@Override
	public Map<String, TaskDescriptor> getTaskDescriptors(BpkIdentifier bpkIdentifier) {


        SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
        SwRepoClient client = new SwRepoClientFactory(SoftwareStoreFactory.getDataStore()).getClient(
                swInfo.getHost(),
                swInfo.getHttpServerPort());

        return client.listTaskDescriptors(bpkIdentifier);





		/*// TODO, mock
		TaskDescriptor a = new TaskDescriptor();
		a.setBpkId(bpkIdentifier.getBpkId());
		a.setGroupId(bpkIdentifier.getGroupId());
		a.setVersion(bpkIdentifier.getVersion());
		a.setName("example-benchmark");
		a.setType(TaskType.BENCHMARK);
		a.setJava(new Java());
		a.getJava().setMainClass("cz.cuni.mff.d3s.been.task.ExampleBenchmark");

		TaskDescriptor b = new TaskDescriptor();
		b.setBpkId(bpkIdentifier.getBpkId());
		b.setGroupId(bpkIdentifier.getGroupId());
		b.setVersion(bpkIdentifier.getVersion());
		b.setName("example-single-task");
		b.setJava(new Java());
		b.getJava().setMainClass("cz.cuni.mff.d3s.been.task.ExampleTask");

		HashMap<String, TaskDescriptor> m = new HashMap<>();
		m.put("ExampleBenchmark.td.xml", a);
		m.put("ExampleTask.td.xml", b);
		return m;*/
	}

	@Override
	public TaskDescriptor getTaskDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) {
		// TODO, mock
		TaskDescriptor a = new TaskDescriptor();
		a.setBpkId(bpkIdentifier.getBpkId());
		a.setGroupId(bpkIdentifier.getGroupId());
		a.setVersion(bpkIdentifier.getVersion());
		a.setName("example-benchmark");
		a.setType(TaskType.BENCHMARK);
		a.setJava(new Java());
		a.getJava().setMainClass("cz.cuni.mff.d3s.been.task.ExampleBenchmark");
		return a;
	}

	@Override
	public Map<String, TaskContextDescriptor> getTaskContextDescriptors(BpkIdentifier bpkIdentifier) {
		// TODO, mock
		TaskDescriptor b = new TaskDescriptor();
		b.setBpkId(bpkIdentifier.getBpkId());
		b.setGroupId(bpkIdentifier.getGroupId());
		b.setVersion(bpkIdentifier.getVersion());
		b.setName("example-single-task");
		b.setJava(new Java());
		b.getJava().setMainClass("cz.cuni.mff.d3s.been.task.ExampleTask");

		TaskContextDescriptor tcd = new TaskContextDescriptor();
		tcd.setName("example-context");
		Task t = new Task();
		t.setName("example-task");
		t.setDescriptor(new Descriptor());
		t.getDescriptor().setTaskDescriptor(b);
		tcd.getTask().add(t);

		HashMap<String, TaskContextDescriptor> m = new HashMap<>();
		m.put("ExampleContext.tcd.xml", tcd);

		return m;
	}

	@Override
	public TaskContextDescriptor getTaskContextDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) {
		return null; // TODO
	}

}
