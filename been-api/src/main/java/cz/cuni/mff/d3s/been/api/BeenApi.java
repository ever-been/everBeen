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
import cz.cuni.mff.d3s.been.core.task.TaskState;
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

	public Collection<Member> getClusterMembers() throws BeenApiException;
	public Map<String, String> getClusterServices() throws BeenApiException;

	public Collection<TaskEntry> getTasks() throws BeenApiException;
	public TaskEntry getTask(String id) throws BeenApiException;
	public Collection<TaskContextEntry> getTaskContexts() throws BeenApiException;
	public TaskContextEntry getTaskContext(String id) throws BeenApiException;
	public Collection<BenchmarkEntry> getBenchmarks() throws BeenApiException;
	public BenchmarkEntry getBenchmark(String id) throws BeenApiException;
	public Collection<TaskContextEntry> getTaskContextsInBenchmark(String benchmarkId) throws BeenApiException;
	public Collection<TaskEntry> getTasksInTaskContext(String taskContextId) throws BeenApiException;

	public void saveTaskDescriptor(TaskDescriptor descriptor, String taskId, String contextId, String benchmarkId) throws DAOException;
	public void saveNamedTaskDescriptor(TaskDescriptor descriptor, String name, BpkIdentifier bpkId) throws DAOException;
	public void saveContextDescriptor(TaskContextDescriptor descriptor, String taskId, String contextId, String benchmarkId) throws DAOException;
	public void saveNamedContextDescriptor(TaskContextDescriptor descriptor, String name, BpkIdentifier bpkId) throws DAOException;
    public TaskDescriptor getDescriptorForTask(String taskId) throws DAOException;
    public TaskContextDescriptor getDescriptorForContext(String contextId) throws DAOException;
    public Map<String, TaskDescriptor> getNamedTaskDescriptorsForBpk(BpkIdentifier bpkIdentifier) throws DAOException;
    public Map<String, TaskContextDescriptor> getNamedContextDescriptorsForBpk(BpkIdentifier bpkIdentifier) throws DAOException;

    public Collection<ServiceLogMessage> getServiceLogsByBeenId(String beenId) throws BeenApiException;
    public Collection<ServiceLogMessage> getServiceLogsByHostRuntimeId(String hostRuntimeId) throws BeenApiException;
    public Collection<ServiceLogMessage> getServiceLogsByServiceName(String serviceName) throws BeenApiException;
    
	public void clearPersistenceForTask(String taskId) throws DAOException;
	public void clearPersistenceForContext(String contextId) throws DAOException;
	public void clearPersistenceForBenchmark(String benchmarkId) throws DAOException;

	public Collection<String> getTasksWithFinalState(TaskState state) throws DAOException;
	public Collection<String> getTasksWithFinalStateFromContext(TaskState state, String contextId) throws DAOException;
	public Collection<String> getTasksWithFinalStateFromBenchmark(TaskState state, String benchmarkId) throws DAOException;

	public TaskState getFinalTaskState(String taskId) throws DAOException;
	public Map<String, TaskState> getFinalTaskStatesForContext(String contextId) throws DAOException;
	public Map<String, TaskState> getFinalTaskStatesForBenchmark(String benchmarkId) throws DAOException;

	public String submitTask(TaskDescriptor taskDescriptor) throws BeenApiException;
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor) throws BeenApiException;
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor, String benchmarkId) throws BeenApiException;
	public String submitBenchmark(TaskDescriptor benchmarkTaskDescriptor) throws BeenApiException;

	public void killTask(String taskId) throws BeenApiException;
	public void killTaskContext(String taskContextId) throws BeenApiException;
	public void killBenchmark(String benchmarkId) throws BeenApiException;

	public void removeTaskEntry(String taskId) throws BeenApiException;
	public void removeTaskContextEntry(String taskContextId) throws BeenApiException;
	public void removeBenchmarkEntry(String benchmarkId) throws BeenApiException;

    public CommandEntry deleteTaskWrkDirectory(String runtimeId, String taskWrkDir) throws BeenApiException;

	public Collection<RuntimeInfo> getRuntimes() throws BeenApiException;
	public RuntimeInfo getRuntime(String id) throws BeenApiException;

	public Collection<TaskLogMessage> getLogsForTask(String taskId) throws BeenApiException;
	public void addLogListener(LogListener listener);
	public void removeLogListener(LogListener listener);

	public Collection<EvaluatorResult> getEvaluatorResults() throws DAOException, BeenApiException;
	public EvaluatorResult getEvaluatorResult(String resultId) throws DAOException, BeenApiException;
    public void deleteResult(String resultId) throws BeenApiException;

	public Collection<BpkIdentifier> getBpks() throws BeenApiException;
	public void uploadBpk(InputStream bpkInputStream) throws BpkConfigurationException, BeenApiException;
	public InputStream downloadBpk(BpkIdentifier bpkIdentifier) throws BeenApiException;

	public Map<String, TaskDescriptor> getTaskDescriptors(BpkIdentifier bpkIdentifier) throws BeenApiException;
	public TaskDescriptor getTaskDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) throws BeenApiException;
	public Map<String, TaskContextDescriptor> getTaskContextDescriptors(BpkIdentifier bpkIdentifier) throws BeenApiException;

	public TaskContextDescriptor getTaskContextDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) throws BeenApiException;

	public Collection<DebugListItem> getDebugWaitingTasks() throws BeenApiException;

	public QueryAnswer queryPersistence(Query query) throws BeenApiException;

    public Collection<TaskEntry> listActiveTasks(String runtimeId) throws BeenApiException;

    public Collection<CommandEntry> listCommandEntries(String runtimeId) throws BeenApiException;

    public Collection<TaskEntry> listTasks(String runtimeId) throws BeenApiException;

    public boolean isConnected();

	interface LogListener {
		public void logAdded(String jsonLog);
	}
}
