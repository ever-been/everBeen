package cz.cuni.mff.d3s.been.api;

import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.debugassistant.DebugListItem;

import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;

/**
 * User: donarus
 * Date: 4/27/13
 * Time: 11:40 AM
 */
public interface BeenApi {

	public void shutdown();

	public Collection<TaskEntry> getTasks();
	public TaskEntry getTask(String id);
	public Collection<TaskContextEntry> getTaskContexts();
	public TaskContextEntry getTaskContext(String id);
	public Collection<BenchmarkEntry> getBenchmarks();
	public BenchmarkEntry getBenchmark(String id);
	public Collection<TaskContextEntry> getTaskContextsInBenchmark(String benchmarkId);

	public String submitTask(TaskDescriptor taskDescriptor);
	public void killTask(String taskId);
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor);
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor, String benchmarkId);
	public void killTaskContext(String taskId);
	public String submitBenchmark(TaskDescriptor benchmarkTaskDescriptor);

	public Collection<RuntimeInfo> getRuntimes();
	public RuntimeInfo getRuntime(String id);

	public Collection<String> getLogSets();
	public Collection<LogMessage> getLogs(String setId);
	public void addLogListener(LogListener listener);
	public void removeLogListener(LogListener listener);

	public Collection<BpkIdentifier> getBpks();
	public void uploadBpk(InputStream bpkInputStream) throws BpkConfigurationException;
	public InputStream downloadBpk(BpkIdentifier bpkIdentifier);

	public Map<String, TaskDescriptor> getTaskDescriptors(BpkIdentifier bpkIdentifier);
	public TaskDescriptor getTaskDescriptor(BpkIdentifier bpkIdentifier, String descriptorName);
	public Map<String, TaskContextDescriptor> getTaskContextDescriptors(BpkIdentifier bpkIdentifier);
	public TaskContextDescriptor getTaskContextDescriptor(BpkIdentifier bpkIdentifier, String descriptorName);

	public Collection<DebugListItem> getDebugWaitingTasks();

	interface LogListener {
		public void logAdded(LogMessage log);
	}
}
