package cz.cuni.mff.d3s.been.cluster.context;

import static cz.cuni.mff.d3s.been.cluster.Names.BENCHMARKS_CONTEXT_ID;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistory;
import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistoryItem;
import cz.cuni.mff.d3s.been.core.benchmark.Storage;
import cz.cuni.mff.d3s.been.core.task.*;

/**
 * 
 * Utility class for manipulating {@link BenchmarkEntry}.
 * 
 * WARNING: Part of BEEN private API. The interface/semantics can change, you
 * better know what you are doing!
 * 
 * 
 * @author Martin Sixta
 */
public class Benchmarks {

	/** slf4j logger */
	private static final Logger log = LoggerFactory.getLogger(Benchmarks.class);

	/** Connection to the cluster */
	private final ClusterContext clusterContext;

	/** Tasks context */
	private final TaskContexts taskContexts;

	/**
	 * Package private constructor.
	 * 
	 * @param clusterContext
	 *          the BEEN cluster context
	 */
	Benchmarks(ClusterContext clusterContext) {
		this.clusterContext = clusterContext;
		this.taskContexts = clusterContext.getTaskContexts();
	}

	/**
	 * Returns the map which holds all benchmark entries.
	 * 
	 * @return {@link IMap} with benchmark entries.
	 */
	public IMap<String, BenchmarkEntry> getBenchmarksMap() {
		return clusterContext.getMap(Names.BENCHMARKS_MAP_NAME);
	}

	/**
	 * Puts a benchmark entry to the benchmark map.
	 * 
	 * The key to the map is
	 * {@link cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry#getId()} of the
	 * entry.
	 * 
	 * The call has semantics of (distributed)
	 * {@link java.util.concurrent.ConcurrentMap#put(Object, Object)}.
	 * 
	 * 
	 * WARNING: The benchmark entry is a shared resource and can be potentially
	 * modified concurrently, use with care.
	 * 
	 * @see IMap#put(Object, Object)
	 * @see IMap#lock(Object)
	 * 
	 * @param entry
	 *          the entry to put/update
	 * @return old value of the entry
	 */
	public BenchmarkEntry put(BenchmarkEntry entry) {
		return getBenchmarksMap().put(entry.getId(), entry);
	}

	/**
	 * Returns a benchmark entry with the id.
	 * 
	 * The call has semantics of (distributed)
	 * {@link java.util.concurrent.ConcurrentMap#get(Object)}.
	 * 
	 * @param id
	 *          ID of the benchmark entry to get
	 * 
	 * @return the entry with the id or null
	 * 
	 * @see IMap#get(Object)
	 */
	public BenchmarkEntry get(String id) {
		return getBenchmarksMap().get(id);
	}

	/**
	 * Returns all currently known task contexts of a given benchmark.
	 * 
	 * WARNING: does not return all contexts ever generated, but only the current
	 * ones
	 * 
	 * @param benchmarkId
	 *          ID of a benchmark to get contexts for
	 * @return all currently known task contexts of a given benchmark
	 */
	public Collection<TaskContextEntry> getTaskContextsInBenchmark(String benchmarkId) {
		String query = String.format("benchmarkId = '%s'", benchmarkId);
		return taskContexts.getTaskContextsMap().values(new SqlPredicate(query));
	}

	/**
	 * Atomically adds a (generator) task to the special benchmark task context.
	 * 
	 * The benchmark task context keeps track of all generator tasks.
	 * 
	 * 
	 * @param taskEntry
	 *          (generator) task to add
	 */
	public void addBenchmarkToBenchmarksContext(TaskEntry taskEntry) {
		IMap<String, TaskContextEntry> taskContextsMap = taskContexts.getTaskContextsMap();
		try {
			taskContextsMap.lock(BENCHMARKS_CONTEXT_ID);

			TaskContextEntry taskContextEntry = taskContextsMap.get(BENCHMARKS_CONTEXT_ID);
			if (taskContextEntry == null) {
				taskContextEntry = new TaskContextEntry();
				taskContextEntry.setId(BENCHMARKS_CONTEXT_ID);
				taskContextEntry.setContextState(TaskContextState.RUNNING);
				taskContextEntry.setLingering(true); // do not destroy this context
				taskContextEntry.setCreated(new Date().getTime());
			}

			taskContextEntry.getContainedTask().add(taskEntry.getId());

			taskContextsMap.put(BENCHMARKS_CONTEXT_ID, taskContextEntry);
		} finally {
			taskContextsMap.unlock(BENCHMARKS_CONTEXT_ID);
		}
	}

	/**
	 * Atomically removes a (generator) task from the special benchmark task
	 * context.
	 * 
	 * The benchmark task context keeps track of all generator tasks.
	 * 
	 * 
	 * Currently there is no automatic clean-up (on purpose) of generator tasks,
	 * we want to have a user well informed.
	 * 
	 * @param taskEntry
	 *          (generator) task to remove
	 */
	public void removeBenchmarkFromBenchmarksContext(TaskEntry taskEntry) {
		IMap<String, TaskContextEntry> taskContextsMap = taskContexts.getTaskContextsMap();
		taskContextsMap.lock(BENCHMARKS_CONTEXT_ID);
		try {
			TaskContextEntry taskContextEntry = taskContextsMap.get(BENCHMARKS_CONTEXT_ID);
			if (taskContextEntry == null) {
				// TODO 
				throw new RuntimeException("Benchmarks context does not exist.");
			}

			taskContextEntry.getContainedTask().remove(taskEntry.getId());

			taskContextsMap.put(BENCHMARKS_CONTEXT_ID, taskContextEntry);
		} finally {
			taskContextsMap.unlock(BENCHMARKS_CONTEXT_ID);
		}
	}

	/**
	 * Submits a generator task.
	 * 
	 * Task schedulability rules apply.
	 * 
	 * Does not lock (no need since the task does not exist yet)
	 * 
	 * @param benchmarkTaskDescriptor
	 *          description of the generator task
	 * @return ID of the submitted task
	 * @throws IllegalArgumentException
	 *           when trying to submit task which is not of type
	 *           {@link TaskType#BENCHMARK`}
	 */

	public String submit(TaskDescriptor benchmarkTaskDescriptor) throws IllegalArgumentException {
		if (benchmarkTaskDescriptor.getType() != TaskType.BENCHMARK) {
			throw new IllegalArgumentException("The task is not a benchmark generator!");
		}

		BenchmarkEntry benchmarkEntry = new BenchmarkEntry();
		benchmarkEntry.setStorage(new Storage());
		benchmarkEntry.setAllowResubmit(true);
		benchmarkEntry.setResubmitHistory(new ResubmitHistory());
		String benchmarkId = UUID.randomUUID().toString();
		benchmarkEntry.setId(benchmarkId);
		String taskId = taskContexts.submitBenchmarkTask(benchmarkTaskDescriptor, benchmarkId);
		benchmarkEntry.setGeneratorId(taskId);

		put(benchmarkEntry);

		return benchmarkEntry.getId();
	}

	/**
	 * 
	 * Resubmits a benchmark's generator task.
	 * 
	 * Task schedulability rules apply.
	 * 
	 * WARNING: does not lock the entry
	 * 
	 * WARNING: the function is meant to be called after a generator failure is
	 * detected. The detection logic is implemented in TM and must lock the entry
	 * first
	 * 
	 * TODO: merge with the detection logic to TM? if there are more users (user
	 * request?) it should lock.
	 * 
	 * 
	 * @param benchmarkEntry
	 *          entry to resubmit
	 * @return ID of the new generator task
	 */
	public String resubmit(BenchmarkEntry benchmarkEntry) {
		String benchmarkId = benchmarkEntry.getId();
		String oldGeneratorId = benchmarkEntry.getGeneratorId();
		TaskEntry generatorEntry = clusterContext.getTasks().getTask(oldGeneratorId);
		TaskDescriptor benchmarkTaskDescriptor = generatorEntry.getTaskDescriptor();
		String oldRuntimeId = generatorEntry.getRuntimeId();

		String newGeneratorId = taskContexts.submitBenchmarkTask(benchmarkTaskDescriptor, benchmarkId);
		benchmarkEntry.setGeneratorId(newGeneratorId);

		ResubmitHistoryItem i = new ResubmitHistoryItem();
		i.setTimestamp(new Date().getTime());
		i.setOldRuntimeId(oldRuntimeId);
		i.setOldGeneratorId(oldGeneratorId);
		benchmarkEntry.getResubmitHistory().getResubmitHistoryItem().add(i);

		put(benchmarkEntry);

		return benchmarkEntry.getId();
	}

	/**
	 * Removes the benchmark entry with the specified ID from Hazelcast map of
	 * benchmarks. The benchmark's generator must be in a final state (finished,
	 * aborted) or already removed. Also removes all existing task contexts that
	 * belong to this benchmark. Also removes all "old generators", which have
	 * failed and were resubmitted.
	 * 
	 * @param benchmarkId
	 *          ID of the benchmark to remove
	 */
	public void remove(String benchmarkId) {
		BenchmarkEntry benchmarkEntry = get(benchmarkId);
		String generatorId = benchmarkEntry.getGeneratorId();
		TaskEntry generatorEntry = clusterContext.getTasks().getTask(generatorId);

		if (generatorEntry == null || generatorEntry.getState() == TaskState.FINISHED || generatorEntry.getState() == TaskState.ABORTED) {
			log.info("Removing benchmark entry {} from map.", benchmarkId);

			// remove all existing contexts from the benchmark
			for (TaskContextEntry taskContextEntry : getTaskContextsInBenchmark(benchmarkId)) {
				clusterContext.getTaskContexts().remove(taskContextEntry.getId());
			}

			// remove failed generators
			for (ResubmitHistoryItem resubmitHistoryItem : benchmarkEntry.getResubmitHistory().getResubmitHistoryItem()) {
				String oldGeneratorId = resubmitHistoryItem.getOldGeneratorId();
				TaskEntry oldGeneratorEntry = clusterContext.getTasks().getTask(oldGeneratorId);
				if (oldGeneratorEntry != null) {
					clusterContext.getTasks().remove(oldGeneratorId);
					removeBenchmarkFromBenchmarksContext(oldGeneratorEntry);
				}
			}

			// remove current generator
			clusterContext.getTasks().remove(generatorId);

			// remove generator from the Been special context for generators
			if (generatorEntry != null) {
				removeBenchmarkFromBenchmarksContext(generatorEntry);
			}

			// remove the entry
			getBenchmarksMap().remove(benchmarkId);
		} else {
			throw new IllegalStateException(String.format(
					"Trying to remove benchmark entry %s, but it's generator is in state %s.",
					benchmarkEntry,
					generatorEntry.getState()));
		}
	}

	/**
	 * Issues a "kill task" message for the benchmark generator. This message will
	 * be delivered to the appropriate host runtime, which will try to kill the
	 * generator of the benchmark. Running contexts and tasks of the benchmark are
	 * *not* affected.
	 * 
	 * @param benchmarkId
	 *          ID of the benchmark to kill
	 */
	public void kill(String benchmarkId) {
		IMap<String, BenchmarkEntry> benchmarksMap = getBenchmarksMap();
		try {
			benchmarksMap.lock(benchmarkId);

			BenchmarkEntry benchmarkEntry = get(benchmarkId);
			String generatorId = benchmarkEntry.getGeneratorId();
			TaskEntry generatorEntry = clusterContext.getTasks().getTask(generatorId);

			if (generatorEntry == null) {
				throw new IllegalStateException(String.format("The generator of benchmark %s does not exist.", benchmarkId));
			}

			if (generatorEntry.getState() == TaskState.FINISHED || generatorEntry.getState() == TaskState.ABORTED) {
				throw new IllegalStateException(String.format(
						"Trying to kill benchmark %s, but it's generator is in state %s.",
						benchmarkId,
						generatorEntry.getState()));
			}

			benchmarkEntry.setAllowResubmit(false);
			put(benchmarkEntry);

			clusterContext.getTasks().kill(generatorId);
		} finally {
			benchmarksMap.unlock(benchmarkId);
		}

	}

	/**
	 * Disallow any further resubmits for the specified benchmark. This means that
	 * when the generator task fails, the whole benchmark will be terminated.
	 * Currently running generator is not affected.
	 * 
	 * This method locks the benchmark entry before applying the setting.
	 * 
	 * @param benchmarkId
	 *          ID of the benchmark for which resubmits should be disallowed
	 */
	public void disallowResubmits(String benchmarkId) {
		IMap<String, BenchmarkEntry> benchmarksMap = getBenchmarksMap();
		benchmarksMap.lock(benchmarkId);
		try {
			BenchmarkEntry benchmarkEntry = get(benchmarkId);
			benchmarkEntry.setAllowResubmit(false);
			put(benchmarkEntry);
		} finally {
			benchmarksMap.unlock(benchmarkId);
		}
	}

}
