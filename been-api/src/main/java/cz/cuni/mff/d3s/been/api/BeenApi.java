package cz.cuni.mff.d3s.been.api;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

/**
 * The BeenApi interface provides access and API to the BEEN cluster. All client
 * access (web interface, command-line interface) to BEEN should be done through
 * this interface.
 * 
 * This class serves a facade to the whole cluster and provides querying of
 * tasks, contexts, benchmark, the state of services and various other
 * components. Also provides control operations, such as submitting tasks and
 * benchmarks.
 * 
 * To use this API, instantiate a {@link BeenApi} instance through
 * {@link BeenApiFactory}, then use this instance to communicate with the
 * cluster. All methods throw {@link BeenApiException} in case of a sudden
 * disconnection or in case of any internal inconsistency or invalid parameters.
 * 
 * @author donarus
 */
public interface BeenApi {

	/**
	 * Shuts down the connection to the BEEN cluster. After calling this method,
	 * any further API calls will throw a {@link BeenApiException} exception. You
	 * should not use this instance of {@link BeenApi} after calling this method.
	 */
	public void shutdown();

	/**
	 * Returns a collection of currently connected cluster members. The item of
	 * the collection is a {@link Member} class from Hazelcast describing the
	 * properties of the member.
	 * 
	 * @return collection of connected cluster members
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<Member> getClusterMembers() throws BeenApiException;

	/**
	 * Returns a list of records describing the current status of various BEEN
	 * services. This does not return information about core (required) components
	 * which always run, but only about optional/configurable components (e.g.
	 * software repository).
	 * 
	 * @return list of available {@link ServiceInfo} records
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public List<ServiceInfo> getClusterServices() throws BeenApiException;

	/**
	 * Returns all tasks that are currently available in the cluster. A task
	 * always belongs to some task context, but doesn't necessarily belong to a
	 * benchmark. Note that finished tasks are automatically removed from the
	 * cluster after some time (see
	 * {@link cz.cuni.mff.d3s.been.cluster.context.TaskContextsConfiguration} for
	 * configuration of this interval). The returned collection is a copy of the
	 * map, so after some time it might not represent the current state of tasks.
	 * 
	 * @return a collection of all task entries in the cluster
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<TaskEntry> getTasks() throws BeenApiException;

	/**
	 * Retrieves the current task entry object for the passed task ID. The
	 * returned object is a copy of the task entry and after some time might not
	 * represent the current state of the task.
	 * 
	 * @param id
	 *          ID of the task
	 * @return {@link TaskEntry} object representing the current state of the task
	 *         or null if there is no task with the specified ID
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public TaskEntry getTask(String id) throws BeenApiException;

	/**
	 * Returns a collection of all currently available task contexts in the
	 * cluster. Note that successfully finished task contexts are automatically
	 * removed from the Hazelcast map after some time (see
	 * {@link cz.cuni.mff.d3s.been.cluster.context.TaskContextsConfiguration} for
	 * configuration of this interval). The returned collection is a copy of the
	 * map, so after some time it might not represent the current state.
	 * 
	 * @return a collection of all task context entries in the cluster
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<TaskContextEntry> getTaskContexts() throws BeenApiException;

	/**
	 * Retrieves the current task context entry object for the specified task
	 * context ID. The returned object is a copy of the entry in the Hazelcast
	 * map, and after some time it might not represent the current state.
	 * 
	 * @param id
	 *          ID of the task context
	 * @return {@link TaskContextEntry} object representing the current state of
	 *         the task context or null if there is no task context with the
	 *         specified ID
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public TaskContextEntry getTaskContext(String id) throws BeenApiException;

	/**
	 * Returns a collection of all currently available benchmarks in the cluster.
	 * Benchmarks are *not* automatically removed, they can be deleted only using
	 * the {@link #removeBenchmarkEntry} method. The returned object is a copy of
	 * the entry in the Hazelcast map, and after some time it might not represent
	 * the current state.
	 * 
	 * @return {@link BenchmarkEntry} object representing the current state of the
	 *         benchmark
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<BenchmarkEntry> getBenchmarks() throws BeenApiException;

	/**
	 * Retrieves the current benchmark entry object for the specified benchmark
	 * ID. The returned object is a copy of the entry in the Hazelcast map, and
	 * after some time it might not represent the current state.
	 * 
	 * @param id
	 *          ID of the benchmark
	 * @return {@link BenchmarkEntry} object representing the current state of the
	 *         benchmark or null if there is no benchmark with the specified ID
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public BenchmarkEntry getBenchmark(String id) throws BeenApiException;

	/**
	 * Returns a collection of all available task contexts in the specified
	 * benchmark. Note that task contexts are automatically removed from the
	 * Hazelcast map after some time (see {@link #getTaskContexts}) so these
	 * removed contexts are not returned in the collection. The returned object is
	 * a copy of the entry in the Hazelcast map, and after some time it might not
	 * represent the current state.
	 * 
	 * @param benchmarkId
	 *          ID of the benchmark
	 * @return a collection of available task context entries in the specified
	 *         benchmark
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<TaskContextEntry> getTaskContextsInBenchmark(String benchmarkId) throws BeenApiException;

	/**
	 * Returns a collection of all available tasks in the specified task context.
	 * Note that tasks are automatically removed from the Hazelcast map after some
	 * time (see {@link #getTasks}) so these removed tasks are not returned in the
	 * collection. The returned object is a copy of the entry in the Hazelcast
	 * map, and after some time it might not represent the current state.
	 * 
	 * @param taskContextId
	 *          ID of the task context
	 * @return a collection of available task entries in the specified task
	 *         context
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<TaskEntry> getTasksInTaskContext(String taskContextId) throws BeenApiException;

	/**
	 * Stores the specified task descriptor as a template for later reuse. The
	 * descriptor with be saved under the specified name and BPK identifier and
	 * subsequent calls to {@link #getTaskDescriptors} will return it along with
	 * the "regular" task descriptors.
	 * 
	 * @param descriptor
	 *          the task descriptor to save
	 * @param name
	 *          the name under which the descriptor should be saved
	 * @param bpkId
	 *          the BPK identifier under which the descriptor should be saved
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public
			void
			saveNamedTaskDescriptor(TaskDescriptor descriptor, String name, BpkIdentifier bpkId) throws BeenApiException;

	/**
	 * Stores the specified task context descriptor as a template for later reuse.
	 * The descriptor with be saved under the specified name and BPK identifier
	 * and subsequent calls to {@link #getTaskContextDescriptors} will return it
	 * along with the "regular" task context descriptors.
	 * 
	 * @param descriptor
	 *          the task context descriptor to save
	 * @param name
	 *          the name under which the descriptor should be saved
	 * @param bpkId
	 *          the BPK identifier under which the descriptor should be saved
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public
			void
			saveNamedContextDescriptor(TaskContextDescriptor descriptor, String name, BpkIdentifier bpkId) throws BeenApiException;

	/**
	 * Lists all saved task descriptors (templates) under the specified BPK
	 * identifier. The result is a map between names (specified when saving the
	 * template using {@link #saveNamedTaskDescriptor}) and descriptors.
	 * 
	 * @param bpkIdentifier
	 *          the BPK identifier
	 * @return a map containing all saved task descriptors under the specified BPK
	 *         identifier
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Map<String, TaskDescriptor> getNamedTaskDescriptorsForBpk(BpkIdentifier bpkIdentifier) throws BeenApiException;

	/**
	 * Lists all saved task context descriptors (templates) under the specified
	 * BPK identifier. The result is a map between names (specified when saving
	 * the template using {@link #saveNamedContextDescriptor}) and descriptors.
	 * 
	 * @param bpkIdentifier
	 *          the BPK identifier
	 * @return a map containing all saved task context descriptors under the
	 *         specified BPK identifier
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public
			Map<String, TaskContextDescriptor>
			getNamedContextDescriptorsForBpk(BpkIdentifier bpkIdentifier) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param participantId
	 * @return
	 * @throws BeenApiException
	 */
	public Collection<ServiceLogMessage> getServiceLogsByBeenId(String participantId) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param hostRuntimeId
	 * @return
	 * @throws BeenApiException
	 */
	public Collection<ServiceLogMessage> getServiceLogsByHostRuntimeId(String hostRuntimeId) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param serviceName
	 * @return
	 * @throws BeenApiException
	 */
	public Collection<ServiceLogMessage> getServiceLogsByServiceName(String serviceName) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param date
	 * @return
	 * @throws BeenApiException
	 */
	public Collection<ServiceLogMessage> getServiceLogsByDate(Date date) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param taskId
	 * @throws BeenApiException
	 */
	public void clearPersistenceForTask(String taskId) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param contextId
	 * @throws BeenApiException
	 */
	public void clearPersistenceForContext(String contextId) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param benchmarkId
	 * @throws BeenApiException
	 */
	public void clearPersistenceForBenchmark(String benchmarkId) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param state
	 * @return
	 * @throws BeenApiException
	 */
	public Collection<String> getTasksWithState(TaskState state) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param state
	 * @param contextId
	 * @return
	 * @throws BeenApiException
	 */
	public Collection<String> getTasksWithStateFromContext(TaskState state, String contextId) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param state
	 * @param benchmarkId
	 * @return
	 * @throws BeenApiException
	 */
	public Collection<String> getTasksWithStateFromBenchmark(TaskState state, String benchmarkId) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param taskId
	 * @return
	 * @throws BeenApiException
	 */
	public TaskState getTaskState(String taskId) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param contextId
	 * @return
	 * @throws BeenApiException
	 */
	public Map<String, TaskState> getTaskStatesForContext(String contextId) throws BeenApiException;

	/**
	 * TODO
	 * 
	 * @param benchmarkId
	 * @return
	 * @throws BeenApiException
	 */
	public Map<String, TaskState> getTaskStatesForBenchmark(String benchmarkId) throws BeenApiException;

	/**
	 * Submits a new task with the specified task descriptor. This creates a new
	 * task entry in the Hazelcast map and returns the generated ID of the newly
	 * submitted task. Calling this method only submits the task (its state is
	 * {@link TaskState#SUBMITTED}) and returns. There are no guarantees about
	 * when or if the task will be planned, accepted or run.
	 * 
	 * This submits a *single task*, for which a new task context will be created
	 * (and will contain only this new task). This context will not belong to any
	 * benchmark.
	 * 
	 * @param taskDescriptor
	 *          the task descriptor of the task
	 * @return ID of the newly submitted task
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public String submitTask(TaskDescriptor taskDescriptor) throws BeenApiException;

	/**
	 * Submits a new task context with the specified task context descriptor. This
	 * creates a new task context entry in the Hazelcast map and returns the
	 * generated ID of the newly created task context. All the tasks described in
	 * the descriptor are also created and submitted. Calling this method only
	 * submits the task context and its tasks and returns immediately. There are
	 * no guarantees about when or if the tasks will be planned, accepted or run.
	 * 
	 * This submits a standalone task context, which will not belong to any
	 * benchmark.
	 * 
	 * @param taskContextDescriptor
	 *          the task context descriptor to be submitted
	 * @return ID of the newly created task context
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public String submitTaskContext(TaskContextDescriptor taskContextDescriptor) throws BeenApiException;

	/**
	 * Submits a new task context with the specified task context descriptor under
	 * the specified benchmark. This API should not be used directly, because
	 * submitting new context within a benchmark is done automatically by the
	 * benchmark manager. See {@link #submitTaskContext} for more info about
	 * submitting task contexts.
	 * 
	 * @param taskContextDescriptor
	 *          the task context descriptor to be submitted
	 * @param benchmarkId
	 *          ID of the benchmark under which the new context should be
	 *          submitted
	 * @return ID of the newly created task context
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public
			String
			submitTaskContext(TaskContextDescriptor taskContextDescriptor, String benchmarkId) throws BeenApiException;

	/**
	 * Submits a new benchmark. The specified task descriptor must be a benchmark
	 * task descriptor, and it will be used as a generator task for the newly
	 * created benchmark. This will only submit the generator task and return
	 * immediately, there are no guarantees about when or if the task will be run.
	 * 
	 * @param benchmarkTaskDescriptor
	 *          task descriptor of the generator task
	 * @return ID of the newly created benchmark
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public String submitBenchmark(TaskDescriptor benchmarkTaskDescriptor) throws BeenApiException;

	/**
	 * Kills the task with the specified ID. This method only "starts" the killing
	 * operation and returns immediately. The actual killing will be performed
	 * later and possibly by a different member of the cluster and there are no
	 * guarantees about when the task will be really killed.
	 * 
	 * @param taskId
	 *          ID of the task to kill
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public void killTask(String taskId) throws BeenApiException;

	/**
	 * Kills the task context with the specified ID. This kills all the tasks that
	 * are contained in the task context. Note that this only "starts" the killing
	 * and returns immediately. There are no guarantees about when the tasks or
	 * the whole context will actually be killed.
	 * 
	 * @param taskContextId
	 *          ID of the task context to kill
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public void killTaskContext(String taskContextId) throws BeenApiException;

	/**
	 * Kills the benchmark with the specified ID. This only kills the currently
	 * running generator task and disallows further resubmit (effectively
	 * cancelling the benchmark), but it does not affect any running task contexts
	 * or the tasks contained within them. Note that this only "starts" the
	 * killing and returns immediately. There are no guarantees about when the
	 * generator task will actually be killed.
	 * 
	 * @param benchmarkId
	 *          ID of the benchmark to kill
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public void killBenchmark(String benchmarkId) throws BeenApiException;

	/**
	 * Removes the task entry with the specified task ID from the Hazelcast map.
	 * The task must be in a final state (finished or aborted) otherwise a
	 * {@link BeenApiException} is thrown. If the the task with the specified ID
	 * does not exist, this method does nothing.
	 * 
	 * @param taskId
	 *          ID of the task entry to remove
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public void removeTaskEntry(String taskId) throws BeenApiException;

	/**
	 * Removes the task context entry with the specified ID from the Hazelcast
	 * map. The task context must be in a final state (finished or failed) as well
	 * as all of its tasks, otherwise a {@link BeenApiException} is thrown. This
	 * method also removes all of the contained tasks, just as if you called
	 * {@link #removeTaskEntry} for all tasks within the context.
	 * 
	 * @param taskContextId
	 *          ID of the task context entry to remove
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public void removeTaskContextEntry(String taskContextId) throws BeenApiException;

	/**
	 * Removes the benchmark entry with the specified ID from the Hazelcast map.
	 * The benchmark's generator task must be in a final state (finished or
	 * aborted) or already removed, and all of the task contexts within this
	 * benchmark must also be in a final state (finished or failed), otherwise a
	 * {@link BeenApiException} is thrown. Also removes all existing task contexts
	 * that belong to this benchmark. Also removes all "old generators", which
	 * have failed and were resubmitted, and the current generator task.
	 * 
	 * @param benchmarkId
	 *          ID of the benchmark entry to remove
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public void removeBenchmarkEntry(String benchmarkId) throws BeenApiException;

	/**
	 * Deletes a leftover task working directory from the specified runtime. There
	 * can be some data left e.g. when the host runtime is incorrectly shutdown,
	 * these directories can be listed through the {@link #getRuntimes} method,
	 * which returns {@link RuntimeInfo} object, which contains a list called
	 * {@link RuntimeInfo#taskDirs}. This call implicitly waits up to 30 seconds
	 * for the operation to finish and return a {@link CommandEntry} object that
	 * represents the result of the operation.
	 * 
	 * @param runtimeId
	 *          ID of the runtime
	 * @param taskWrkDir
	 *          working directory which should be deleted
	 * @return a {@link CommandEntry} object that represents the result of the
	 *         operation
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public CommandEntry deleteTaskWrkDirectory(String runtimeId, String taskWrkDir) throws BeenApiException;

	/**
	 * Returns a list of all available host runtimes in the cluster. The returned
	 * object is a copy of the entry in the Hazelcast map, and after some time it
	 * might not represent the current state.
	 * 
	 * @return a collection of {@link RuntimeInfo} object representing the states
	 *         of host runtimes
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<RuntimeInfo> getRuntimes() throws BeenApiException;

	/**
	 * Returns host runtimes matching the specified criteria. The criteria is
	 * specified as an XPath expression and is applied on each {@link RuntimeInfo}
	 * object representing the current states of host runtimes. This method
	 * returns a collection of {@link RuntimeInfo} objects that had passed the
	 * filter (for which the XPath expression returned 'true' or a value that
	 * evaluates to 'true').
	 * 
	 * @param xpath
	 *          criteria a {@link RuntimeInfo} must match
	 * @return all host runtimes matching the criteria
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<RuntimeInfo> getRuntimes(String xpath) throws BeenApiException;

	/**
	 * Returns the {@link RuntimeInfo} object representing the host runtime with
	 * the specified ID.
	 * 
	 * @param id
	 *          ID of the host runtime
	 * @return a {@link RuntimeInfo} of the host runtime or null if there is no
	 *         runtime with the specified ID
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public RuntimeInfo getRuntime(String id) throws BeenApiException;

	/**
	 * Returns a collection of all created task log messages from the task with
	 * the specified ID. Note that logs can be automatically removed after some
	 * time.
	 * 
	 * @param taskId
	 *          ID of the task
	 * @return a collection of all available log message for the specified task
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<TaskLogMessage> getLogsForTask(String taskId) throws BeenApiException;

	/**
	 * Adds a global log listener, that will subsequently receive a notification
	 * about every newly created log message.
	 * 
	 * @param listener
	 *          the log listener to add
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public void addLogListener(EntryListener<String, String> listener) throws BeenApiException;

	/**
	 * Removes the specified log listener, which will no longer receive log
	 * messages. If this log listener is not registered, returns silently.
	 * 
	 * @param listener
	 *          the log listener to remove
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public void removeLogListener(EntryListener<String, String> listener) throws BeenApiException;

	/**
	 * Returns a collection of all available evaluator results. These results are
	 * created by evaluator tasks and are stored in a single collection in the
	 * persistence layer.
	 * 
	 * @return a collection of all available evaluator results
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<EvaluatorResult> getEvaluatorResults() throws BeenApiException;

	/**
	 * Returns a single evaluator result with the specified ID. Throws
	 * {@link BeenApiException} if there is no such result.
	 * 
	 * @param resultId
	 *          the ID of the result to retrieve
	 * @return the result with the specified ID
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public EvaluatorResult getEvaluatorResult(String resultId) throws BeenApiException;

	/**
	 * Permanently deletes the evaluator result with the specified ID. Returns
	 * silently if there is no such result.
	 * 
	 * @param resultId
	 *          ID of the evaluator result to delete
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public void deleteResult(String resultId) throws BeenApiException;

	/**
	 * Lists the identifiers of all available BPK packages in the software
	 * repository. The software repository must be running, otherwise a
	 * {@link BeenApiException} is thrown. The BPKs can be uploaded into the
	 * software repository using the {@link #uploadBpk} method.
	 * 
	 * @return a collection of all available BPK packages
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Collection<BpkIdentifier> getBpks() throws BeenApiException;

	/**
	 * Uploads a new BPK package into the software repository. The passed
	 * {@link BpkHolder} object specifies the BPK identifier under which the
	 * package should be stored. If there is already a package with the same
	 * identifier, is will be overwritten.
	 * 
	 * @param bpkFileHolder
	 *          the object describing the package and holding its data
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public void uploadBpk(BpkHolder bpkFileHolder) throws BeenApiException;

	/**
	 * Downloads a package with the specified BPK identifier from the software
	 * repository. If successful, returns a {@link InputStream} object holding the
	 * stream to the data of the package. If there is no such package with the
	 * specified BPK identifier, a {@link BeenApiException} is thrown.
	 * 
	 * @param bpkIdentifier
	 *          the BPK identifier of the package
	 * @return a {@link InputStream} with the data of the package
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public InputStream downloadBpk(BpkIdentifier bpkIdentifier) throws BeenApiException;

	/**
	 * Retrieves task descriptors contained withing the BPK package with the
	 * specified BPK identifier. The result is a map from the names of the task
	 * descriptors (filenames) and the actual task descriptors.
	 * 
	 * @param bpkIdentifier
	 *          the BPK identifier of the package
	 * @return all available task descriptors within the package
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public Map<String, TaskDescriptor> getTaskDescriptors(BpkIdentifier bpkIdentifier) throws BeenApiException;

	/**
	 * Retrieves a single task descriptor with the specified name (filename) from
	 * the specified BPK package. Throws a {@link BeenApiException} if the BPK
	 * identifier is invalid. Returns null if there is no such descriptor.
	 * 
	 * @param bpkIdentifier
	 *          the BPK identifier of the package
	 * @param descriptorName
	 *          the name of the descriptor to retrieve
	 * @return the task descriptor with the specified name or null if there is no
	 *         such descriptor
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public TaskDescriptor getTaskDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) throws BeenApiException;

	/**
	 * Retrieves task context descriptors contained withing the BPK package with
	 * the specified BPK identifier. The result is a map from the names of the
	 * task context descriptors (filenames) and the actual task context
	 * descriptors.
	 * 
	 * @param bpkIdentifier
	 *          the BPK identifier of the package
	 * @return all available task context descriptors within the package
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public
			Map<String, TaskContextDescriptor>
			getTaskContextDescriptors(BpkIdentifier bpkIdentifier) throws BeenApiException;

	/**
	 * Retrieves a single task context descriptor with the specified name
	 * (filename) from the specified BPK package. Throws a
	 * {@link BeenApiException} if the BPK identifier is invalid. Returns null if
	 * there is no such descriptor.
	 * 
	 * @param bpkIdentifier
	 *          the BPK identifier of the package
	 * @param descriptorName
	 *          the name of the descriptor to retrieve
	 * @return the task context descriptor with the specified name or null if
	 *         there is no such descriptor
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	public
			TaskContextDescriptor
			getTaskContextDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) throws BeenApiException;

	public Collection<DebugListItem> getDebugWaitingTasks() throws BeenApiException;

	public Collection<TaskEntry> listActiveTasks(String runtimeId) throws BeenApiException;

	public Collection<CommandEntry> listCommandEntries(String runtimeId) throws BeenApiException;

	public Collection<TaskEntry> listTasks(String runtimeId) throws BeenApiException;

	public boolean isConnected();

	boolean isSwRepositoryOnline() throws BeenApiException;

	public void disallowResubmitsForBenchmark(String benchmarkId) throws BeenApiException;

	void deleteNamedTaskDescriptor(BpkIdentifier bpkId, String name) throws BeenApiException;

	void deleteNamedTaskContextDescriptor(BpkIdentifier bpkId, String name) throws BeenApiException;

	interface LogListener {
		public void logAdded(String jsonLog);
	}
}
