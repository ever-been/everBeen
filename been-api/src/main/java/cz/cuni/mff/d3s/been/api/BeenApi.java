package cz.cuni.mff.d3s.been.api;

import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.LogMessage;
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

/**
 * User: donarus
 * Date: 4/27/13
 * Time: 11:40 AM
 */
public interface BeenApi {

	public Collection<TaskEntry> getTasks();
	public TaskEntry getTask(String id);
	public Collection<TaskContextEntry> getTaskContexts();
	public TaskContextEntry getTaskContext(String id);
	public String submitTask(TaskDescriptor taskDescriptor);
	public void killTask(String taskId);
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor);
	public void killTaskContext(String taskId);

	public Collection<RuntimeInfo> getRuntimes();
	public RuntimeInfo getRuntime(String id);

	public Collection<String> getLogSets();
	public Collection<LogMessage> getLogs(String setId);
	public void addLogListener(LogListener listener);
	public void removeLogListener(LogListener listener);

	public Collection<BpkIdentifier> getBpks();
	public void uploadBpk(InputStream bpkInputStream) throws BpkConfigurationException;
	public InputStream downloadBpk(BpkIdentifier bpkIdentifier);
	public void deleteBpk(BpkIdentifier bpkIdentifier);

    public Collection<TaskDescriptor> getTaskDescriptors(BpkIdentifier bpkIdentifier);
    public Collection<TaskDescriptor> getTaskDescriptors();
    public Collection<TaskContextDescriptor> getTaskContextDescriptors(BpkIdentifier bpkIdentifier);
    public Collection<TaskContextDescriptor> getTaskContextDescriptors();

	public Collection<DebugListItem> getDebugWaitingTasks();

	interface LogListener {
		public void logAdded(LogMessage log);
	}
}
