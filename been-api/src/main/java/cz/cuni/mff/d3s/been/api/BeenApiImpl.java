package cz.cuni.mff.d3s.been.api;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.query.RuntimeInfoPredicate;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntry;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntryState;
import cz.cuni.mff.d3s.been.core.protocol.messages.DeleteTaskWrkDirMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.service.ServiceInfo;
import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.datastore.SoftwareStore;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.debugassistant.DebugListItem;
import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.logging.ServiceLogMessage;
import cz.cuni.mff.d3s.been.logging.TaskLogMessage;
import cz.cuni.mff.d3s.been.persistence.*;
import cz.cuni.mff.d3s.been.persistence.task.PersistentTaskState;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;
import cz.cuni.mff.d3s.been.swrepository.SWRepositoryServiceInfoConstants;
import cz.cuni.mff.d3s.been.swrepository.Versions;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * The implementation of the {@link BeenApi} interface. To instantiate this
 * class, use {@link BeenApiFactory}.
 * 
 * @author donarus
 */
final class BeenApiImpl implements BeenApi {

	/** class slf4j logger */
	private static final Logger log = LoggerFactory.getLogger(BeenApiImpl.class);

	/** BEEN cluster context instance */
	private final ClusterContext clusterContext;

	/** JSON utility instance for serialization/deserialization */
	private final JSONUtils jsonUtils = JSONUtils.newInstance();

	/**
	 * Default constructor, which connects to the BEEN cluster as a native
	 * Hazelcast client using the specified connection credentials. There must
	 * already be some running BEEN node in full mode.
	 * 
	 * @param host
	 *          IP address or hostname of a running node
	 * @param port
	 *          port of a running Hazelcast instance of the node
	 * @param groupName
	 *          Hazelcast group name
	 * @param groupPassword
	 *          Hazelcast group password
	 */
	public BeenApiImpl(final String host, final int port, final String groupName, final String groupPassword) {
		Instance.newNativeInstance(host, port, groupName, groupPassword);
		clusterContext = Instance.createContext();
	}

	/**
	 * {@link BeenApiImpl} constructor that reuses an already existing
	 * {@link ClusterContext} instance. The passed cluster context must be already
	 * connected to the BEEN cluster.
	 * 
	 * @param clusterContext
	 *          the cluster context to reuse
	 */
	public BeenApiImpl(final ClusterContext clusterContext) {
		this.clusterContext = clusterContext;
	}

	@Override
	public void shutdown() {
		clusterContext.stop();
		Instance.shutdown();
	}

	@Override
	public Collection<Member> getClusterMembers() throws BeenApiException {
		final String errorMsg = "Failed to list connected members";

		checkIsActive(errorMsg);

		try {
			return clusterContext.getMembers();
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public boolean isSwRepositoryOnline() throws BeenApiException {
		final String errorMsg = "Failed to check if software repository is running";

		checkIsActive(errorMsg);

		try {
			return clusterContext.getServices().getSWRepositoryInfo() != null;
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public List<ServiceInfo> getClusterServices() throws BeenApiException {
		final String errorMsg = "Failed to list available cluster services";

		checkIsActive(errorMsg);

		try {
			return new ArrayList<>(clusterContext.getServices().getServicesMap().values());
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<TaskEntry> getTasks() throws BeenApiException {
		final String errorMsg = "Failed to list task entries";

		checkIsActive(errorMsg);

		try {
			return clusterContext.getTasks().getTasks();
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<TaskEntry> getTasksOnRuntime(String runtimeId) throws BeenApiException {
		final String errorMsg = "Failed to list task entries on a Host Runtime " + runtimeId;

		checkIsActive(errorMsg);

		try {
			return clusterContext.getTasks().getTasksOnRuntime(runtimeId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}

	}

	@Override
	public TaskEntry getTask(final String taskId) throws BeenApiException {
		final String errorMsg = String.format("Failed to get task with id '%s'", taskId);

		checkIsActive(errorMsg);

		try {
			return clusterContext.getTasks().getTask(taskId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<TaskContextEntry> getTaskContexts() throws BeenApiException {
		final String errorMsg = "Failed to list task contexts";

		checkIsActive(errorMsg);

		try {
			return clusterContext.getTaskContexts().getTaskContexts();
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public TaskContextEntry getTaskContext(final String taskContextId) throws BeenApiException {
		final String errorMsg = String.format("Failed to get task context with id '%s'", taskContextId);

		checkIsActive(errorMsg);

		try {
			return clusterContext.getTaskContexts().getTaskContext(taskContextId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<BenchmarkEntry> getBenchmarks() throws BeenApiException {
		final String errorMsg = "Failed to list benchmarks";

		checkIsActive(errorMsg);

		try {
			return clusterContext.getBenchmarks().getBenchmarksMap().values();
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public BenchmarkEntry getBenchmark(final String benchmarkId) throws BeenApiException {
		final String errorMsg = String.format("Failed to get benchmark with id '%s'", benchmarkId);

		checkIsActive(errorMsg);

		try {
			return clusterContext.getBenchmarks().get(benchmarkId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<TaskContextEntry> getTaskContextsInBenchmark(final String benchmarkId) throws BeenApiException {
		final String errorMsg = String.format("Failed to list task contexts for benchmark with id '%s'", benchmarkId);

		checkIsActive(errorMsg);

		try {
			return clusterContext.getBenchmarks().getTaskContextsInBenchmark(benchmarkId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<TaskEntry> getTasksInTaskContext(final String taskContextId) throws BeenApiException {
		final String errorMsg = String.format("Failed to list tasks for task context with id '%s'", taskContextId);

		checkIsActive(errorMsg);

		try {
			return clusterContext.getTaskContexts().getTasksInTaskContext(taskContextId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<RuntimeInfo> getRuntimes() throws BeenApiException {
		final String errorMsg = "Failed to list host runtimes";

		checkIsActive(errorMsg);

		try {
			return clusterContext.getRuntimes().getRuntimes();
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<RuntimeInfo> getRuntimes(final String xpath) throws BeenApiException {
		final String errorMsg = String.format("Failed to get list of runtimes matching '%s'", xpath);
		checkIsActive(errorMsg);

		Predicate<?, ?> predicate = new RuntimeInfoPredicate(xpath);

		try {
			return clusterContext.getRuntimes().getRuntimeMap().values(predicate);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}

	}

	@Override
	public RuntimeInfo getRuntime(final String runtimeId) throws BeenApiException {
		final String errorMsg = String.format("Failed to get host runtime with id '%s'", runtimeId);
		checkIsActive(errorMsg);

		try {
			return clusterContext.getRuntimes().getRuntimeInfo(runtimeId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	// ---------------------------------------------------
	// TASK/TASK CONTEXT/BENCHMARK DESCRIPTORS PERSISTENCE
	// ---------------------------------------------------

	@Override
	public
			void
			saveNamedTaskDescriptor(final TaskDescriptor descriptor, final String name, final BpkIdentifier bpkId) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to save named task descriptors with name '%s' for bpk '%s:%s:%s'",
				name,
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());

		checkIsActive(errorMsg);

		final String key = createNamedDescriptorKey(bpkId, name);
		final NamedTaskDescriptor namedDescriptor = new NamedTaskDescriptor(name, bpkId.getGroupId(), bpkId.getBpkId(), bpkId.getVersion(), descriptor);

		try {
			clusterContext.getMap(Names.NAMED_TASK_DESCRIPTORS_MAP_NAME).put(key, namedDescriptor);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void deleteNamedTaskDescriptor(final BpkIdentifier bpkId, final String name) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to delete named task descriptors with name '%s' for bpk '%s:%s:%s'",
				name,
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());

		checkIsActive(errorMsg);

		final String key = createNamedDescriptorKey(bpkId, name);

		try {
			clusterContext.getMap(Names.NAMED_TASK_DESCRIPTORS_MAP_NAME).remove(key);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void saveNamedContextDescriptor(final TaskContextDescriptor descriptor, final String name,
			final BpkIdentifier bpkId) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to save named task context descriptors with name '%s' for bpk '%s:%s:%s'",
				name,
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());

		checkIsActive(errorMsg);

		final String key = createNamedDescriptorKey(bpkId, name);
		final NamedTaskContextDescriptor namedDescriptor = new NamedTaskContextDescriptor(name, bpkId.getGroupId(), bpkId.getBpkId(), bpkId.getVersion(), descriptor);

		try {
			clusterContext.getMap(Names.NAMED_TASK_CONTEXT_DESCRIPTORS_MAP_NAME).put(key, namedDescriptor);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void deleteNamedTaskContextDescriptor(final BpkIdentifier bpkId, final String name) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to delete named task context descriptors with name '%s' for bpk '%s:%s:%s'",
				name,
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());

		checkIsActive(errorMsg);

		final String key = createNamedDescriptorKey(bpkId, name);

		try {
			clusterContext.getMap(Names.NAMED_TASK_CONTEXT_DESCRIPTORS_MAP_NAME).remove(key);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public
			Map<String, TaskDescriptor>
			getNamedTaskDescriptorsForBpk(final BpkIdentifier bpkIdentifier) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to list named task descriptors for bpk '%s:%s:%s'",
				bpkIdentifier.getGroupId(),
				bpkIdentifier.getBpkId(),
				bpkIdentifier.getVersion());

		checkIsActive(errorMsg);

		final SqlPredicate predicate = new SqlPredicate(String.format(
				"groupId = '%s' AND bpkId = '%s' and bpkVersion = '%s'",
				bpkIdentifier.getGroupId(),
				bpkIdentifier.getBpkId(),
				bpkIdentifier.getVersion()));

		Collection<NamedTaskDescriptor> foundDescriptors;
		try {
			foundDescriptors = queryHazelcastMap(Names.NAMED_TASK_DESCRIPTORS_MAP_NAME, predicate);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}

		final Map<String, TaskDescriptor> namedDescriptors = new HashMap<>();
		for (NamedTaskDescriptor foundDescriptor : foundDescriptors) {
			namedDescriptors.put(foundDescriptor.getName(), foundDescriptor.getDescriptor());
		}

		return namedDescriptors;
	}

	@Override
	public
			Map<String, TaskContextDescriptor>
			getNamedContextDescriptorsForBpk(final BpkIdentifier bpkIdentifier) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to list named task context descriptors for bpk '%s:%s:%s'",
				bpkIdentifier.getGroupId(),
				bpkIdentifier.getBpkId(),
				bpkIdentifier.getVersion());

		checkIsActive(errorMsg);

		final SqlPredicate predicate = new SqlPredicate(String.format(
				"groupId = '%s' AND bpkId = '%s' and bpkVersion = '%s'",
				bpkIdentifier.getGroupId(),
				bpkIdentifier.getBpkId(),
				bpkIdentifier.getVersion()));

		Collection<NamedTaskContextDescriptor> foundDescriptors;
		try {
			foundDescriptors = queryHazelcastMap(Names.NAMED_TASK_CONTEXT_DESCRIPTORS_MAP_NAME, predicate);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}

		final Map<String, TaskContextDescriptor> namedDescriptors = new HashMap<>();
		for (NamedTaskContextDescriptor foundDescriptor : foundDescriptors) {
			namedDescriptors.put(foundDescriptor.getName(), foundDescriptor.getDescriptor());
		}

		return namedDescriptors;
	}

	// FIXME - EntryListener is haazelcast dependency !!!
	@Override
	public void addLogListener(final EntryListener<String, String> listener) throws BeenApiException {
		final String errorMsg = "Failed to add task log listener";
		checkIsActive(errorMsg);

		try {
			clusterContext.<String, String> getMap(Names.LOGS_TASK_MAP_NAME).addEntryListener(listener, true);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void removeLogListener(final EntryListener<String, String> listener) throws BeenApiException {
		final String errorMsg = "Failed to remove task log listener";
		checkIsActive(errorMsg);

		try {
			clusterContext.<String, String> getMap(Names.LOGS_TASK_MAP_NAME).removeEntryListener(listener);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<TaskLogMessage> getLogsForTask(final String taskId) throws BeenApiException {
		final String errorMsg = String.format("Failed to list logs for task with id '%s'", taskId);
		final Query query = new QueryBuilder().on(Entities.LOG_TASK.getId()).with("taskId", taskId).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			List<TaskLogMessage> logs = new ArrayList<>(unpackDataAnswer(query, answer, TaskLogMessage.class));
			Comparator<TaskLogMessage> comparator = new Comparator<TaskLogMessage>() {
				@Override
				public int compare(TaskLogMessage o1, TaskLogMessage o2) {
					return new Long(o1.getCreated()).compareTo(o2.getCreated());
				}
			};
			Collections.sort(logs, comparator);
			return logs;
		} catch (DAOException e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<EvaluatorResult> getEvaluatorResults() throws BeenApiException {
		final String errorMsg = "Failed to list logs for task";
		final Query query = new QueryBuilder().on(Entities.RESULT_EVALUATOR.getId()).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			return unpackDataAnswer(query, answer, EvaluatorResult.class);
		} catch (DAOException e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void deleteResult(final String resultId) throws BeenApiException {
		final String errorMsg = String.format("Failed to delete result with id '%s'", resultId);
		final Query query = new QueryBuilder().on(Entities.RESULT_EVALUATOR.getId()).with("id", resultId).delete();

		final QueryAnswer answer = performQuery(query, errorMsg);

		if (answer.getStatus() != QueryStatus.OK) {
			throw createBeenApiException(errorMsg, answer.getStatus().getDescription());
		}
	}

	@Override
	public EvaluatorResult getEvaluatorResult(final String resultId) throws BeenApiException {
		final String errorMsg = String.format("Failed to get evaluator results with id '%s'", resultId);
		final Query query = new QueryBuilder().on(Entities.RESULT_EVALUATOR.getId()).with("id", resultId).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			final Collection<EvaluatorResult> evaluatorResults = unpackDataAnswer(query, answer, EvaluatorResult.class);
			if (evaluatorResults.size() != 1) {
				throw createBeenApiException(
						errorMsg,
						String.format("Found '%d' results but expected exactly 1 result", evaluatorResults.size()));
			}
			return evaluatorResults.iterator().next();
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}
	}

	@Override
	public Collection<BpkIdentifier> getBpks() throws BeenApiException {
		final String errorMsg = "Failed to list bpks";
		checkIsActive(errorMsg);

		final SwRepoClient client = getSwRepoClient(errorMsg);

		try {
			return client.listBpks();
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void uploadBpk(final BpkHolder bpkFileHolder) throws BeenApiException {
		final BpkIdentifier bpkIdentifier;
		try {
			bpkIdentifier = bpkFileHolder.getBpkIdentifier();
		} catch (Exception e) {
			throw createBeenApiException("Failed to read bpk info from bpk stream", e);
		}

		final String groupId = bpkIdentifier.getGroupId();
		final String bpkId = bpkIdentifier.getBpkId();
		final String version = bpkIdentifier.getVersion();
		final String errorMsg = String.format("Failed to upload bpk '%s:%s:%s'", groupId, bpkId, version);

		checkIsActive(errorMsg);

		if (!version.endsWith(Versions.SNAPSHOT_SUFFIX)) {
			for (BpkIdentifier existingBpk : getBpks()) {
				if (existingBpk.getBpkId().equals(bpkId) && existingBpk.getGroupId().equals(groupId) && existingBpk.getVersion().equals(
						version)) {
					throw createBeenApiException(
							errorMsg,
							"Bpk with same identifiers already exists in repository and its version is not '*-SNAPSHOT'. Reupload is not supported.");
				}
			}
		}

		final SwRepoClient client = getSwRepoClient(errorMsg);
		try {
			client.putBpk(bpkIdentifier, bpkFileHolder.getInputStream());
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public InputStream downloadBpk(final BpkIdentifier bpkIdentifier) throws BeenApiException {
		final String groupId = bpkIdentifier.getGroupId();
		final String bpkId = bpkIdentifier.getBpkId();
		final String version = bpkIdentifier.getVersion();
		final String errorMsg = String.format("Failed to download bpk '%s:%s:%s'", groupId, bpkId, version);

		checkIsActive(errorMsg);

		final SwRepoClient client = getSwRepoClient(errorMsg);
		try {
			return client.getBpkNoCache(bpkIdentifier).getInputStream();
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public final String submitTask(final TaskDescriptor descriptor) throws BeenApiException {
		final String groupId = descriptor.getGroupId();
		final String bpkId = descriptor.getBpkId();
		final String version = descriptor.getVersion();
		final String errorMsg = String.format("Failed to submit task descriptor '%s:%s:%s/%s'", groupId, bpkId, version, descriptor.getName());

		checkIsActive(errorMsg);

		try {
			return clusterContext.getTaskContexts().submitTaskInNewContext(descriptor);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public final String submitTaskContext(final TaskContextDescriptor descriptor) throws BeenApiException {
		final String errorMsg = String.format("Failed to submit task context descriptor '%s'", descriptor.getName());

		checkIsActive(errorMsg);

		try {
			return clusterContext.getTaskContexts().submit(descriptor, null);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public final
			String
			submitTaskContext(final TaskContextDescriptor descriptor, final String benchmarkId) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to submit task context descriptor '%s' for benchmark '%s'",
				descriptor.getName(),
				benchmarkId);

		checkIsActive(errorMsg);

		try {
			return clusterContext.getTaskContexts().submit(descriptor, benchmarkId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public final String submitBenchmark(final TaskDescriptor benchmarkTaskDescriptor) throws BeenApiException {
		final String groupId = benchmarkTaskDescriptor.getGroupId();
		final String bpkId = benchmarkTaskDescriptor.getBpkId();
		final String version = benchmarkTaskDescriptor.getVersion();
		final String errorMsg = String.format("Failed to submit benchmark descriptor '%s:%s:%s'", groupId, bpkId, version);

		checkIsActive(errorMsg);

		try {
			return clusterContext.getBenchmarks().submit(benchmarkTaskDescriptor);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void killTask(final String taskId) throws BeenApiException {
		final String errorMsg = String.format("Failed to kill task '%s'", taskId);

		checkIsActive(errorMsg);

		try {
			clusterContext.getTasks().kill(taskId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void killTaskContext(final String taskContextId) throws BeenApiException {
		final String errorMsg = String.format("Failed to kill task context '%s'", taskContextId);

		checkIsActive(errorMsg);

		try {
			clusterContext.getTaskContexts().kill(taskContextId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void killBenchmark(final String benchmarkId) throws BeenApiException {
		final String errorMsg = String.format("Failed to kill benchmark '%s'", benchmarkId);

		checkIsActive(errorMsg);

		try {
			clusterContext.getBenchmarks().kill(benchmarkId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void disallowResubmitsForBenchmark(String benchmarkId) throws BeenApiException {
		final String errorMsg = String.format("Failed to disallow resubmits for benchmark '%s'", benchmarkId);

		checkIsActive(errorMsg);

		try {
			clusterContext.getBenchmarks().disallowResubmits(benchmarkId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void removeTaskEntry(final String taskId) throws BeenApiException {
		final String errorMsg = String.format("Failed to remove task entry '%s'", taskId);

		checkIsActive(errorMsg);

		TaskEntry task = getTask(taskId);

		try {
			clusterContext.getTasks().remove(taskId);
			clearPersistenceForTask(taskId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}

		// we don't want to remove benchmarks context id
		if (task.getTaskContextId().equals(Names.BENCHMARKS_CONTEXT_ID)) {
			return;
		}

		// check if task was last not null task of the context in the map
		try {
			TaskContextEntry taskContext = getTaskContext(task.getTaskContextId());
			List<String> containedTask = taskContext.getContainedTask();
			if (containedTask.size() == 1) { // just one task in task context
				removeTaskContextEntry(task.getTaskContextId());
				clusterContext.getTaskContexts().remove(task.getTaskContextId());
			} else {
				// more tasks in task context
				boolean removable = true;
				for (String containedTaskId : containedTask) {
					if (getTask(containedTaskId) != null) {
						removable = false;
					}
				}
				if (removable) {
					clusterContext.getTaskContexts().remove(task.getTaskContextId());
					clearPersistenceForContext(task.getTaskContextId());
				}
			}

		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void removeTaskContextEntry(final String taskContextId) throws BeenApiException {
		final String errorMsg = String.format("Failed to remove task context entry '%s'", taskContextId);

		checkIsActive(errorMsg);

		try {
			TaskContextEntry taskContextEntry = getTaskContext(taskContextId);
			clusterContext.getTaskContexts().remove(taskContextId);
			clearPersistenceForContext(taskContextId);
			for (String taskId : taskContextEntry.getContainedTask()) {
				clearPersistenceForTask(taskId);
			}
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public void removeBenchmarkEntry(final String benchmarkId) throws BeenApiException {
		final String errorMsg = String.format("Failed to remove benchmark entry '%s'", benchmarkId);

		checkIsActive(errorMsg);

		try {
			BenchmarkEntry benchmarkEntry = getBenchmark(benchmarkId);
			clusterContext.getBenchmarks().remove(benchmarkId);

			// cleanup persistence
			clearPersistenceForTask(benchmarkEntry.getGeneratorId());
			clearPersistenceForBenchmark(benchmarkId);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public CommandEntry deleteTaskWrkDirectory(final String runtimeId, final String taskWrkDir) throws BeenApiException {

		// FIXME needs refactoring
		// FIXME do not return CommandEntry, but return void and throw exception on error or timeout

		final String errorMsg = String.format(
				"Failed to delete task working directory '%s' on runtime '%s'",
				taskWrkDir,
				runtimeId);

		try {
			long operationId = clusterContext.generateId(DeleteTaskWrkDirMessage.OPERATION_ID_KEY);
			final DeleteTaskWrkDirMessage deleteMessage = new DeleteTaskWrkDirMessage(runtimeId, taskWrkDir, operationId);
			clusterContext.getTopics().publishInGlobalTopic(deleteMessage);

			final IMap<Long, CommandEntry> map = clusterContext.getMap(Names.BEEN_MAP_COMMAND_ENTRIES);

			final BlockingQueue<CommandEntry> queue = new LinkedBlockingQueue<>();
			final EntryListener<Long, CommandEntry> waiter = new CommandEntryMapWaiter(queue);

			map.addEntryListener(waiter, operationId, true);

			CommandEntry commandEntry = map.get(operationId);

			if (commandEntry == null || commandEntry.getState() == CommandEntryState.PENDING) {
				try {
					commandEntry = queue.poll(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					log.warn("CommandEntry poll interrupted", e);
				}
			}

			map.removeEntryListener(waiter);
			queue.clear();

			if (commandEntry == null) {
				throw new CommandTimeoutException(String.format(
						"delete task working directory command " + "with parameters [runtimeId : '%s', taskWrkDirName : '%s'] timeouted",
						runtimeId,
						taskWrkDir));
			}

			return commandEntry;
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}

	}

	@Override
	public Collection<DebugListItem> getDebugWaitingTasks() throws BeenApiException {
		final String errorMsg = "Failed to list tasks waiting for debug";
		checkIsActive(errorMsg);

		try {
			return new DebugAssistant(clusterContext).listWaitingProcesses();
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Map<String, TaskDescriptor> getTaskDescriptors(final BpkIdentifier bpkIdentifier) throws BeenApiException {
		final String groupId = bpkIdentifier.getGroupId();
		final String bpkId = bpkIdentifier.getBpkId();
		final String version = bpkIdentifier.getVersion();
		final String errorMsg = String.format("Failed to list task descriptors for bpk '%s:%s:%s'", groupId, bpkId, version);

		checkIsActive(errorMsg);

		final SwRepoClient client = getSwRepoClient(errorMsg);

		try {
			return client.listTaskDescriptors(bpkIdentifier);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public
			TaskDescriptor
			getTaskDescriptor(final BpkIdentifier bpkIdentifier, final String descriptorName) throws BeenApiException {
		final String groupId = bpkIdentifier.getGroupId();
		final String bpkId = bpkIdentifier.getBpkId();
		final String version = bpkIdentifier.getVersion();
		final String errorMsg = String.format(
				"Failed to get task descriptor for bpk '%s:%s:%s' with name '%s'",
				groupId,
				bpkId,
				version,
				descriptorName);

		// check if cluster is live is done in getTaskDescriptors method
		try {
			final TaskDescriptor td = getTaskDescriptors(bpkIdentifier).get(descriptorName);
			if (td == null) throw new BeenApiException(String.format("No task descriptor named '%s' found in BPK [%s]", descriptorName, bpkIdentifier));
			return td;
		} catch (BeenApiException e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public
			Map<String, TaskContextDescriptor>
			getTaskContextDescriptors(final BpkIdentifier bpkIdentifier) throws BeenApiException {
		final String groupId = bpkIdentifier.getGroupId();
		final String bpkId = bpkIdentifier.getBpkId();
		final String version = bpkIdentifier.getVersion();
		final String errorMsg = String.format(
				"Failed to list task context descriptors for bpk '%s:%s:%s'",
				groupId,
				bpkId,
				version);

		checkIsActive(errorMsg);

		final SwRepoClient client = getSwRepoClient(errorMsg);
		try {
			return client.listTaskContextDescriptors(bpkIdentifier);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public
			TaskContextDescriptor
			getTaskContextDescriptor(final BpkIdentifier bpkIdentifier, final String descriptorName) throws BeenApiException {
		final String groupId = bpkIdentifier.getGroupId();
		final String bpkId = bpkIdentifier.getBpkId();
		final String version = bpkIdentifier.getVersion();
		final String errorMsg = String.format(
				"Failed to get task context descriptor for bpk '%s:%s:%s' with name '%s'",
				groupId,
				bpkId,
				version,
				descriptorName);

		// check if cluster is live is done in getTaskContextDescriptors method
		try {
			return getTaskContextDescriptors(bpkIdentifier).get(descriptorName);
		} catch (BeenApiException e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	/**
	 * Performs a general query (e.g. fetch, delete, ...) in the persistence layer
	 * and returns a {@link QueryAnswer} object that represent the query result
	 * and the returned data (if there are any).
	 * 
	 * @param query
	 *          the query to execute
	 * @return the query answer
	 * @throws BeenApiException
	 *           in case of an internal exception, see {@link BeenApi} for
	 *           discussion
	 */
	private QueryAnswer queryPersistence(final Query query) throws BeenApiException {
		return performQuery(query, "Failed to perform persistence query");
	}

	@Override
	public Collection<CommandEntry> listCommandEntries(final String runtimeId) throws BeenApiException {
		final String errorMsg = String.format("Failed to list command entries for runtime '%s'", runtimeId);

		checkIsActive(errorMsg);

		final SqlPredicate predicate = new SqlPredicate(String.format("runtimeId = '%s'", runtimeId));

		try {
			return queryHazelcastMap(Names.BEEN_MAP_COMMAND_ENTRIES, predicate);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<TaskEntry> listActiveTasks(final String runtimeId) throws BeenApiException {
		final String errorMsg = String.format("Failed to list active tasks on runtime '%s'", runtimeId);

		checkIsActive(errorMsg);

		final SqlPredicate predicate = new SqlPredicate(String.format(
				"runtimeId = '%s' AND state != %s",
				runtimeId,
				TaskState.ABORTED));

		try {
			return queryHazelcastMap(Names.TASKS_MAP_NAME, predicate);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	@Override
	public Collection<TaskEntry> listTasks(final String runtimeId) throws BeenApiException {
		final String errorMsg = String.format("Failed to list tasks on runtime '%s'", runtimeId);

		checkIsActive(errorMsg);

		final SqlPredicate predicate = new SqlPredicate(String.format("runtimeId = '%s'", runtimeId));

		try {
			return queryHazelcastMap(Names.TASKS_MAP_NAME, predicate);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	/**
	 * Performs a general Hazelcast predicate query on the specified map. This
	 * evaluates the predicate in a distributed manner and returns the map entries
	 * that matched the query (for which the predicate returned true).
	 * 
	 * @param mapName
	 *          the name of the map to query
	 * @param queryPredicate
	 *          the query to execute
	 * @param <T>
	 *          type of map entries
	 * @return the collection of matching map entries
	 */
	private <T> Collection<T> queryHazelcastMap(final String mapName, final SqlPredicate queryPredicate) {
		final IMap<?, T> map = clusterContext.getMap(mapName);
		return map.values(queryPredicate);
	}

	@Override
	public Collection<ServiceLogMessage> getServiceLogsByBeenId(final String participantId) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to list service logs for service with participant id '%s'",
				participantId);
		final Query query = new QueryBuilder().on(Entities.LOG_SERVICE.getId()).with("beenId", participantId).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			return unpackDataAnswer(query, answer, ServiceLogMessage.class);
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}
	}

	@Override
	public
			Collection<ServiceLogMessage>
			getServiceLogsByHostRuntimeId(final String hostRuntimeId) throws BeenApiException {
		final String errorMsg = String.format("Failed to list host runtime logs for runtime with id '%s'", hostRuntimeId);
		final Query query = new QueryBuilder().on(Entities.LOG_SERVICE.getId()).with("hostRuntimeId", hostRuntimeId).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			return unpackDataAnswer(query, answer, ServiceLogMessage.class);
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}
	}

	@Override
	public Collection<ServiceLogMessage> getServiceLogsByServiceName(final String serviceName) throws BeenApiException {
		final String errorMsg = String.format("Failed to list logs for service '%s'", serviceName);
		final Query query = new QueryBuilder().on(Entities.LOG_SERVICE.getId()).with("serviceName", serviceName).fetch();
		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			return unpackDataAnswer(query, answer, ServiceLogMessage.class);
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}
	}

	@Override
	public Collection<ServiceLogMessage> getServiceLogsByDate(final Date date) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to list service log messages for date '%s'",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		final Long timeToday = c.getTimeInMillis();
		c.add(Calendar.DATE, 1);
		final Long timeTomorrow = c.getTimeInMillis();

		final Query query = new QueryBuilder().on(Entities.LOG_SERVICE.getId()).with("created").between(
				timeToday,
				timeTomorrow).fetch();
		final QueryAnswer answer = queryPersistence(query);

		try {
			return unpackDataAnswer(query, answer, ServiceLogMessage.class);
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}
	}

	/**
	 * Checks whether the current cluster context is still active and connected.
	 * If yes, this method simply returns, if no, it throws a
	 * {@link ClusterConnectionUnavailableException} exception with the specified
	 * exception message.
	 * 
	 * @param format
	 *          the format of the exception message
	 * @param args
	 *          arguments of the exception message
	 * @throws ClusterConnectionUnavailableException
	 *           when the connection is unavailable
	 */
	private void checkIsActive(final String format, final String... args) throws ClusterConnectionUnavailableException {
		final String message = String.format(format, args);

		if (!isConnected()) {
			throw new ClusterConnectionUnavailableException(message);
		}
	}

	@Override
	public boolean isConnected() {
		return clusterContext.isActive();
	}

	// TASK STATE RETRIEVAL

	@Override
	public Collection<String> getTasksWithState(final TaskState state) throws BeenApiException {
		final String errorMsg = String.format("Failed to list tasks with state '%s'", state.name());
		final Query query = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("taskState", state.name()).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			final Collection<PersistentTaskState> persistentTaskStates = unpackDataAnswer(
					query,
					answer,
					PersistentTaskState.class);
			final Collection<String> taskIds = new ArrayList<>(persistentTaskStates.size());
			for (PersistentTaskState persistentTaskState : persistentTaskStates) {
				taskIds.add(persistentTaskState.getTaskId());
			}
			return taskIds;
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}
	}

	@Override
	public
			Collection<String>
			getTasksWithStateFromContext(final TaskState state, final String contextId) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to list tasks with state '%s' from task context '%s'",
				state.name(),
				contextId);
		final Query query = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("taskState", state.name()).with(
				"contextId",
				contextId).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		final Collection<PersistentTaskState> persistentTaskStates;
		try {
			persistentTaskStates = unpackDataAnswer(query, answer, PersistentTaskState.class);
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}

		final Collection<String> taskIds = new ArrayList<>(persistentTaskStates.size());
		for (PersistentTaskState persistentTaskState : persistentTaskStates) {
			taskIds.add(persistentTaskState.getTaskId());
		}
		return taskIds;
	}

	@Override
	public
			Collection<String>
			getTasksWithStateFromBenchmark(TaskState state, final String benchmarkId) throws BeenApiException {
		final String errorMsg = String.format(
				"Failed to list tasks with state '%s' from benchmark '%s'",
				state.name(),
				benchmarkId);
		final Query query = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("taskState", state.name()).with(
				"benchmarkId",
				benchmarkId).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			final Collection<PersistentTaskState> persistentTaskStates = unpackDataAnswer(
					query,
					answer,
					PersistentTaskState.class);
			final Collection<String> taskIds = new ArrayList<>(persistentTaskStates.size());
			for (PersistentTaskState persistentTaskState : persistentTaskStates) {
				taskIds.add(persistentTaskState.getTaskId());
			}
			return taskIds;
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}
	}

	@Override
	public TaskState getTaskState(final String taskId) throws BeenApiException {
		final String errorMsg = String.format("Failed to get state of task '%s'", taskId);
		final Query query = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("taskId", taskId).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			final Collection<PersistentTaskState> states = unpackDataAnswer(query, answer, PersistentTaskState.class);
			if (states.size() != 1) {
				throw createBeenApiException(
						errorMsg,
						String.format("Found '%d' results but expected exactly 1 result", states.size()));
			}
			return states.iterator().next().getTaskState();
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}
	}

	@Override
	public Map<String, TaskState> getTaskStatesForContext(final String contextId) throws BeenApiException {
		final String errorMsg = String.format("Failed to list states of tasks running in task context '%s'", contextId);
		final Query query = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("contextId", contextId).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			final Collection<PersistentTaskState> states = unpackDataAnswer(query, answer, PersistentTaskState.class);
			final Map<String, TaskState> statesMap = new HashMap<>(states.size());
			for (PersistentTaskState pState : states) {
				statesMap.put(pState.getTaskId(), pState.getTaskState());
			}
			return statesMap;
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}
	}

	@Override
	public Map<String, TaskState> getTaskStatesForBenchmark(final String benchmarkId) throws BeenApiException {
		final String errorMsg = String.format("Failed to list states of tasks running in benchmark '%s'", benchmarkId);
		final Query query = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("benchmarkId", benchmarkId).fetch();

		final QueryAnswer answer = performQuery(query, errorMsg);

		try {
			final Collection<PersistentTaskState> states = unpackDataAnswer(query, answer, PersistentTaskState.class);
			final Map<String, TaskState> statesMap = new HashMap<>(states.size());
			for (PersistentTaskState pState : states) {
				statesMap.put(pState.getTaskId(), pState.getTaskState());
			}
			return statesMap;
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		}
	}

	// --------------------
	// PERSISTENCE CLEARING
	// --------------------

	@Override
	public void clearPersistenceForTask(final String taskId) throws BeenApiException {
		final Query query = new QueryBuilder().with("taskId", taskId).delete();
		final String errorMsg = String.format("Failed to delete leftover entities for task with id '%s'", taskId);
		performQuery(query, errorMsg);
	}

	@Override
	public void clearPersistenceForContext(final String contextId) throws BeenApiException {
		final Query query = new QueryBuilder().with("contextId", contextId).delete();
		final String errorMsg = String.format("Failed to delete leftover entities for task context with id '%s'", contextId);
		performQuery(query, errorMsg);
	}

	@Override
	public void clearPersistenceForBenchmark(final String benchmarkId) throws BeenApiException {
		final Query query = new QueryBuilder().with("benchmarkId", benchmarkId).delete();
		final String errorMsg = String.format("Failed to delete leftover entities for benchmark with id '%s'", benchmarkId);
		performQuery(query, errorMsg);
	}

	// ---------------
	// NON-API METHODS
	// ---------------

	/**
	 * Unmarshall a collection of accordingly typed entities from a
	 * {@link QueryAnswer}, or throw a {@link DAOException} if something goes
	 * wrong Call this on a {@link QueryAnswer} that should be bringing you data.
	 * Provide the {@link Query} this answer originated from. If the answer is not
	 * carrying the data it should, an informative exception is thrown.
	 * 
	 * @param query
	 *          {@link Query} you used to retrieve data. This method should only
	 *          be using for fetch-type queries
	 * @param answer
	 *          The {@link QueryAnswer} you got as a response to this query
	 * @param entityClass
	 *          Class of the entity to unmarshall
	 * @param <T>
	 *          the type of the entity
	 * @return unpacked answer
	 * @throws DAOException
	 *           When there is no data in the answer
	 */
	private <T extends Entity> Collection<T> unpackDataAnswer(final Query query, final QueryAnswer answer,
			final Class<T> entityClass) throws DAOException {
		if (!answer.isCarryingData()) {
			throw new DAOException(String.format(
					"Answer for query '%s' returned with no data: %s",
					query.toString(),
					answer.getStatus().getDescription()));
		}
		log.debug("Deserializing returned data: {}", answer.getData());
		try {
			return jsonUtils.deserialize(answer.getData(), entityClass);
		} catch (JsonException e) {
			throw new DAOException(String.format("Failed to unmarshall data responding to query '%s'", query.toString()), e);
		}
	}

	/**
	 * Perform given query operation
	 * 
	 * @param query
	 *          query to be performed
	 * @param errorMsg
	 *          if the operation fails, message will be added to produced
	 *          exception
	 * @throws ClusterConnectionUnavailableException
	 *           when been api is not connected to cluster
	 * @throws PersistenceException
	 *           when retrieved query answer is invalid or with status other than
	 *           OK
	 * @return query answer
	 * @throws BeenApiException
	 *           when something other goes wrong while retrieving query answer
	 */
	private QueryAnswer performQuery(final Query query, final String errorMsg) throws BeenApiException {
		checkIsActive(errorMsg);

		final QueryAnswer answer;
		try {
			answer = clusterContext.getPersistence().query(query);
		} catch (DAOException e) {
			throw createPersistenceException(errorMsg, e);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}

		if (!answer.getStatus().isOk()) {
			throw createPersistenceException(errorMsg, answer.getStatus().getDescription());
		}

		return answer;
	}

	/**
	 * Creates and returns an instance of {@link SwRepoClient} that serves as a
	 * client for the software repository. The repository must be running, or else
	 * a {@link BeenApiException} is thrown with the specified message.
	 * 
	 * @param errorMsg
	 *          the message of the exception
	 * @return a client for the software repository
	 * @throws BeenApiException
	 *           when the software repository is unavailable or another internal
	 *           exception occurs
	 */
	private SwRepoClient getSwRepoClient(final String errorMsg) throws BeenApiException {
		final ServiceInfo swInfo;
		try {
			swInfo = clusterContext.getServices().getSWRepositoryInfo();
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}

		if (swInfo == null) {
			throw createSoftwareRepositoryUnavailableException(errorMsg);
		}

		try {
			final SoftwareStore softwareCache = SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache();
			final SwRepoClientFactory swRepoClientFactory = new SwRepoClientFactory(softwareCache);
			final String addresses = (String) swInfo.getParam(SWRepositoryServiceInfoConstants.ADDRESSES);
			return swRepoClientFactory.getClient(addresses);
		} catch (Exception e) {
			throw createBeenApiException(errorMsg, e);
		}
	}

	/**
	 * Creates a persistence exception with the specified message and cause.
	 * 
	 * @param errorMsg
	 *          message of the exception
	 * @param cause
	 *          cause of the exception
	 * @return a new exception object
	 */
	private PersistenceException createPersistenceException(final String errorMsg, Exception cause) {
		final String msg = String.format("%s. Reason: %s", errorMsg, cause.getMessage());
		return new PersistenceException(msg, cause);
	}

	/**
	 * Creates a persistence exception with the specified message and reason.
	 * 
	 * @param errorMsg
	 *          message of the exception
	 * @param reason
	 *          reason of the exception
	 * @return a new exception object
	 */
	private PersistenceException createPersistenceException(final String errorMsg, final String reason) {
		final String msg = String.format("%s. Reason: %s", errorMsg, reason);
		return new PersistenceException(msg);
	}

	/**
	 * Creates an exception which signals that the software repository is
	 * unavailable.
	 * 
	 * @param errorMsg
	 *          message of the exception
	 * @return a new exception object
	 */
	private SoftwareRepositoryUnavailableException createSoftwareRepositoryUnavailableException(final String errorMsg) {
		final String msg = String.format("%s. Reason: Software repository is not available", errorMsg);
		return new SoftwareRepositoryUnavailableException(msg);
	}

	/**
	 * Creates a general BEEN exception with the specified message and cause.
	 * 
	 * @param errorMsg
	 *          message of the exception
	 * @param e
	 *          cause of the exception
	 * @return a new exception object
	 */
	private BeenApiException createBeenApiException(final String errorMsg, final Exception e) {
		return new BeenApiException(String.format("%s. Reason: %s", errorMsg, e.getMessage()), e);
	}

	/**
	 * Creates a persistence exception with the specified message and reason.
	 * 
	 * @param errorMsg
	 *          message of the exception
	 * @param reason
	 *          reason of the exception
	 * @return a new exception object
	 */
	private BeenApiException createBeenApiException(final String errorMsg, final String reason) {
		return new BeenApiException(String.format("%s. Reason: %s", errorMsg, reason));
	}

	/**
	 * Creates a string representation of the BPK identifier and name of a
	 * descriptor.
	 * 
	 * @param bpkId
	 *          BPK identifier
	 * @param name
	 *          name of the descriptor
	 * @return string representation of the parameters
	 */
	private String createNamedDescriptorKey(BpkIdentifier bpkId, String name) {
		return String.format("%s_%s_%s__%s", bpkId.getGroupId(), bpkId.getBpkId(), bpkId.getVersion(), name);
	}

}
