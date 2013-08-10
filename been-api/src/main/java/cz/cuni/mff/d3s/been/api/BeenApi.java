package cz.cuni.mff.d3s.been.api;

import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Member;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntry;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.debugassistant.DebugListItem;
import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.logging.ServiceLogMessage;
import cz.cuni.mff.d3s.been.logging.TaskLogMessage;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.QueryAnswer;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

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

	public
			void
			saveNamedTaskDescriptor(TaskDescriptor descriptor, String name, BpkIdentifier bpkId) throws BeenApiException;
	public
			void
			saveNamedContextDescriptor(TaskContextDescriptor descriptor, String name, BpkIdentifier bpkId) throws BeenApiException;
	public Map<String, TaskDescriptor> getNamedTaskDescriptorsForBpk(BpkIdentifier bpkIdentifier) throws BeenApiException;
	public
			Map<String, TaskContextDescriptor>
			getNamedContextDescriptorsForBpk(BpkIdentifier bpkIdentifier) throws BeenApiException;

	public Collection<ServiceLogMessage> getServiceLogsByBeenId(String beenId) throws BeenApiException;
	public Collection<ServiceLogMessage> getServiceLogsByHostRuntimeId(String hostRuntimeId) throws BeenApiException;
	public Collection<ServiceLogMessage> getServiceLogsByServiceName(String serviceName) throws BeenApiException;

	public Collection<ServiceLogMessage> getServiceLogsByDate(Date date) throws BeenApiException;

	public void clearPersistenceForTask(String taskId) throws BeenApiException;
	public void clearPersistenceForContext(String contextId) throws BeenApiException;
	public void clearPersistenceForBenchmark(String benchmarkId) throws BeenApiException;

	public Collection<String> getTasksWithState(TaskState state) throws BeenApiException;
	public Collection<String> getTasksWithStateFromContext(TaskState state, String contextId) throws BeenApiException;
	public Collection<String> getTasksWithStateFromBenchmark(TaskState state, String benchmarkId) throws BeenApiException;

	public TaskState getTaskState(String taskId) throws BeenApiException;
	public Map<String, TaskState> getTaskStatesForContext(String contextId) throws BeenApiException;
	public Map<String, TaskState> getTaskStatesForBenchmark(String benchmarkId) throws BeenApiException;

	public String submitTask(TaskDescriptor taskDescriptor) throws BeenApiException;
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor) throws BeenApiException;
	public
			String
			submitTaskContext(TaskContextDescriptor taskContextDescriptor, String benchmarkId) throws BeenApiException;
	public String submitBenchmark(TaskDescriptor benchmarkTaskDescriptor) throws BeenApiException;

	public void killTask(String taskId) throws BeenApiException;
	public void killTaskContext(String taskContextId) throws BeenApiException;
	public void killBenchmark(String benchmarkId) throws BeenApiException;

	public void removeTaskEntry(String taskId) throws BeenApiException;
	public void removeTaskContextEntry(String taskContextId) throws BeenApiException;
	public void removeBenchmarkEntry(String benchmarkId) throws BeenApiException;

	public CommandEntry deleteTaskWrkDirectory(String runtimeId, String taskWrkDir) throws BeenApiException;

	public Collection<RuntimeInfo> getRuntimes() throws BeenApiException;

	/**
	 * Returns Host Runtimes matching a criteria.
	 * 
	 * The criteria is specified using XPath and is applied on each and every
	 * RuntimeInfo.
	 * 
	 * @param xpath
	 *          criteria a HostInfo must match
	 * @return all runtimes matching the criteria
	 * @throws BeenApiException
	 *           if the look-up fails due to connection problems
	 */
	public Collection<RuntimeInfo> getRuntimes(String xpath) throws BeenApiException;
	public RuntimeInfo getRuntime(String id) throws BeenApiException;

	public Collection<TaskLogMessage> getLogsForTask(String taskId) throws BeenApiException;
	public void addLogListener(EntryListener<String, String> listener) throws BeenApiException;
	public void removeLogListener(EntryListener<String, String> listener) throws BeenApiException;

	public Collection<EvaluatorResult> getEvaluatorResults() throws BeenApiException;
	public EvaluatorResult getEvaluatorResult(String resultId) throws BeenApiException;
	public void deleteResult(String resultId) throws BeenApiException;

	public Collection<BpkIdentifier> getBpks() throws BeenApiException;
	public void uploadBpk(BpkHolder bpkFileHolder) throws BeenApiException;
	public InputStream downloadBpk(BpkIdentifier bpkIdentifier) throws BeenApiException;

	public Map<String, TaskDescriptor> getTaskDescriptors(BpkIdentifier bpkIdentifier) throws BeenApiException;
	public TaskDescriptor getTaskDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) throws BeenApiException;
	public
			Map<String, TaskContextDescriptor>
			getTaskContextDescriptors(BpkIdentifier bpkIdentifier) throws BeenApiException;

	public
			TaskContextDescriptor
			getTaskContextDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) throws BeenApiException;

	public Collection<DebugListItem> getDebugWaitingTasks() throws BeenApiException;

	public QueryAnswer queryPersistence(Query query) throws BeenApiException;

	public Collection<TaskEntry> listActiveTasks(String runtimeId) throws BeenApiException;

	public Collection<CommandEntry> listCommandEntries(String runtimeId) throws BeenApiException;

	public Collection<TaskEntry> listTasks(String runtimeId) throws BeenApiException;

	public boolean isConnected();

    boolean isSwRepositoryOnline() throws BeenApiException;

    interface LogListener {
		public void logAdded(String jsonLog);
	}
}
