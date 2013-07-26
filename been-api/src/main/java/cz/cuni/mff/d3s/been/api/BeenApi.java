package cz.cuni.mff.d3s.been.api;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import com.hazelcast.core.Member;
import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntry;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntryState;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.debugassistant.DebugListItem;
import cz.cuni.mff.d3s.been.logging.ServiceLogMessage;
import cz.cuni.mff.d3s.been.logging.TaskLogMessage;
import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.QueryAnswer;


/**
 * User: donarus Date: 4/27/13 Time: 11:40 AM
 */
public interface BeenApi {

	public void shutdown();

	public Collection<Member> getClusterMembers();
	public Map<String, String> getClusterServices();

	public Collection<TaskEntry> getTasks();
	public TaskEntry getTask(String id);
	public Collection<TaskContextEntry> getTaskContexts();
	public TaskContextEntry getTaskContext(String id);
	public Collection<BenchmarkEntry> getBenchmarks();
	public BenchmarkEntry getBenchmark(String id);
	public Collection<TaskContextEntry> getTaskContextsInBenchmark(String benchmarkId);
	public Collection<TaskEntry> getTasksInTaskContext(String taskContextId);

	public void saveTaskDescriptor(TaskDescriptor descriptor, String taskId, String contextId, String benchmarkId) throws DAOException;
	public void saveNamedTaskDescriptor(TaskDescriptor descriptor, String name, BpkIdentifier bpkId) throws DAOException;
	public void saveContextDescriptor(TaskContextDescriptor descriptor, String taskId, String contextId, String benchmarkId) throws DAOException;
	public void saveNamedContextDescriptor(TaskContextDescriptor descriptor, String name, BpkIdentifier bpkId) throws DAOException;
    public TaskDescriptor getDescriptorForTask(String taskId) throws DAOException;
    public TaskContextDescriptor getDescriptorForContext(String contextId) throws DAOException;
    public Map<String, TaskDescriptor> getNamedTaskDescriptorsForBpk(BpkIdentifier bpkIdentifier) throws DAOException;
    public Map<String, TaskContextDescriptor> getNamedContextDescriptorsForBpk(BpkIdentifier bpkIdentifier) throws DAOException;

    public Collection<ServiceLogMessage> getServiceLogsByBeenId(String beenId) throws DAOException;
    public Collection<ServiceLogMessage> getServiceLogsByHostRuntimeId(String hostRuntimeId) throws DAOException;
    public Collection<ServiceLogMessage> getServiceLogsByServiceName(String serviceName) throws DAOException;

	public String submitTask(TaskDescriptor taskDescriptor);
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor);
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor, String benchmarkId);
	public String submitBenchmark(TaskDescriptor benchmarkTaskDescriptor);

	public void killTask(String taskId);
	public void killTaskContext(String taskContextId);
	public void killBenchmark(String benchmarkId);

	public void removeTaskEntry(String taskId);
	public void removeTaskContextEntry(String taskContextId);
	public void removeBenchmarkEntry(String benchmarkId);

    public CommandEntry deleteTaskWrkDirectory(String runtimeId, String taskWrkDir) throws CommandTimeoutException;

	public Collection<RuntimeInfo> getRuntimes();
	public RuntimeInfo getRuntime(String id);

	public Collection<TaskLogMessage> getLogsForTask(String taskId) throws DAOException;
	public void addLogListener(LogListener listener);
	public void removeLogListener(LogListener listener);
	
	public Collection<EvaluatorResult> getEvaluatorResults() throws DAOException;
	public EvaluatorResult getEvaluatorResult(String resultId) throws DAOException;

	public Collection<BpkIdentifier> getBpks();
	public void uploadBpk(InputStream bpkInputStream) throws BpkConfigurationException;
	public InputStream downloadBpk(BpkIdentifier bpkIdentifier);

	public Map<String, TaskDescriptor> getTaskDescriptors(BpkIdentifier bpkIdentifier);
	public TaskDescriptor getTaskDescriptor(BpkIdentifier bpkIdentifier, String descriptorName);
	public Map<String, TaskContextDescriptor> getTaskContextDescriptors(BpkIdentifier bpkIdentifier);
	public TaskContextDescriptor getTaskContextDescriptor(BpkIdentifier bpkIdentifier, String descriptorName);

	public Collection<DebugListItem> getDebugWaitingTasks();

	public QueryAnswer queryPersistence(Query query);

    public Collection<TaskEntry> listActiveTasks(String runtimeId);

    public Collection<CommandEntry> listCommandEntries(String runtimeId);

    public Collection<TaskEntry> listTasks(String runtimeId);

    interface LogListener {
		public void logAdded(String jsonLog);
	}
}
