package cz.cuni.mff.d3s.been.api;

import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Member;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntry;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.service.ServiceInfo;
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
import java.util.List;
import java.util.Map;

/**
 * The BeenApi interface provides access and API to the BEEN cluster.
 * All client access (web interface, command-line interface) to BEEN should be done
 * through this interface, which is implement int the {@link BeenApiImpl} class.
 *
 * This class serves a facade to the whole cluster and provides querying of
 * tasks, contexts, benchmark, the state of services and various other components.
 * Also provides control operations, such as submitting tasks and benchmarks.
 *
 * To use this API, instantiate a {@link BeenApiImpl} class, which will connect
 * to the cluster, then use this instance to communicate with the cluster. All methods
 * throw {@link BeenApiException} in case of a sudden disconnection or in case of any
 * internal inconsistency or invalid parameters.
 *
 * @author donarus
 */
public interface BeenApi {

	/**
	 * Shuts down the connection to the BEEN cluster. After calling this method,
	 * any further API calls will throw a {@link BeenApiException} exception. You should
	 * not use this instance of {@link BeenApi} after calling this method.
	 */
	public void shutdown();

	/**
	 * Returns a collection of currently connected cluster members. The item of the
	 * collection is a {@link Member} class from Hazelcast describing the properties
	 * of the member.
	 *
	 * @return collection of connected cluster members
	 * @throws BeenApiException in case of an internal exception, see {@link BeenApi} for discussion
	 */
	public Collection<Member> getClusterMembers() throws BeenApiException;

	/**
	 * Returns a list of records describing the current status of various BEEN services.
	 * This does not return information about core (required) components which always run,
	 * but only about optional/configurable components (e.g. software repository).
	 *
	 * @return list of available {@link ServiceInfo} records
	 * @throws BeenApiException in case of an internal exception, see {@link BeenApi} for discussion
	 */
	public List<ServiceInfo> getClusterServices() throws BeenApiException;

	/**
	 * Returns all tasks that are currently available in the cluster. A task always belongs
	 * to some task context, but doesn't necessarily belong to a benchmark. Note that finished
	 * tasks are automatically removed from the cluster after some time (see
	 * {@link cz.cuni.mff.d3s.been.cluster.context.TaskContextsConfiguration} for
	 * configuration of this interval). The returned collection is a copy of the map, so after some time
	 * it might not represent the current state of tasks.
	 *
	 * @return a collection of all task entries in the cluster
	 * @throws BeenApiException in case of an internal exception, see {@link BeenApi} for discussion
	 */
	public Collection<TaskEntry> getTasks() throws BeenApiException;

	/**
	 * Retrieves the current task entry object for the passed task ID. The returned object is a copy
	 * of the task entry and after some time might not represent the current state of the task.
	 *
	 * @param id ID of the task
	 * @return {@link TaskEntry} object representing the current state of the task or null if there is no
	 * task with the specified ID
	 * @throws BeenApiException in case of an internal exception, see {@link BeenApi} for discussion
	 */
	public TaskEntry getTask(String id) throws BeenApiException;

	/**
	 * Returns a collection of all currently available task contexts in the cluster. Note that successfully
	 * finished task contexts are automatically removed from the Hazelcast map after some time (see
	 * {@link cz.cuni.mff.d3s.been.cluster.context.TaskContextsConfiguration} for configuration of this
	 * interval). The returned collection is a copy of the map, so after some time
	 * it might not represent the current state.
	 *
	 * @return a collection of all task context entries in the cluster
	 * @throws BeenApiException in case of an internal exception, see {@link BeenApi} for discussion
	 */
	public Collection<TaskContextEntry> getTaskContexts() throws BeenApiException;

	/**
	 * Retrieves the current task context entry object for the specified task context ID. The returned
	 * object is a copy of the entry in the Hazelcast map, and after some time it might not represent
	 * the current state.
	 *
	 * @param id ID of the task context
	 * @return {@link TaskContextEntry} object representing the current state of the task context or null
	 * if there is no task context with the specified ID
	 * @throws BeenApiException in case of an internal exception, see {@link BeenApi} for discussion
	 */
	public TaskContextEntry getTaskContext(String id) throws BeenApiException;

	/**
	 * Returns a collection of all currently available benchmarks in the cluster. Benchmarks are *not*
	 * automatically removed, they can be deleted only using the {@link #removeBenchmarkEntry} method.
	 * The returned object is a copy of the entry in the Hazelcast map, and after some time it might not
	 * represent the current state.
	 *
	 * @return {@link BenchmarkEntry} object representing the current state of the benchmark
	 * @throws BeenApiException in case of an internal exception, see {@link BeenApi} for discussion
	 */
	public Collection<BenchmarkEntry> getBenchmarks() throws BeenApiException;

	/**
	 * Retrieves the current benchmark entry object for the specified benchmark ID. The returned
	 * object is a copy of the entry in the Hazelcast map, and after some time it might not represent
	 * the current state.
	 *
	 * @param id ID of the benchmark
	 * @return {@link BenchmarkEntry} object representing the current state of the benchmark or null if
	 * there is no benchmark with the specified ID
	 * @throws BeenApiException in case of an internal exception, see {@link BeenApi} for discussion
	 */
	public BenchmarkEntry getBenchmark(String id) throws BeenApiException;

	/**
	 * Returns a collection of all available task contexts in the specified benchmark. Note that task
	 * contexts are automatically removed from the Hazelcast map after some time (see {@link #getTaskContexts})
	 * so these removed contexts are not returned in the collection. The returned
	 * object is a copy of the entry in the Hazelcast map, and after some time it might not represent
	 * the current state.
	 *
	 * @param benchmarkId ID of the benchmark
	 * @return a collection of available task context entries in the specified benchmark
	 * @throws BeenApiException in case of an internal exception, see {@link BeenApi} for discussion
	 */
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

	public void disallowResubmitsForBenchmark(String benchmarkId) throws BeenApiException;

	interface LogListener {
		public void logAdded(String jsonLog);
	}
}
