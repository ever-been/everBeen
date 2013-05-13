package cz.cuni.mff.d3s.been.cluster.context;

import static cz.cuni.mff.d3s.been.cluster.Names.BENCHMARKS_CONTEXT_ID;

import java.util.Collection;
import java.util.UUID;

import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
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
	/** Connection to the cluster */
	private final ClusterContext clusterContext;

	/** Tasks context */
	private final TaskContexts taskContexts;

	/** Package private constructor */
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
		try {
			taskContextsMap.lock(BENCHMARKS_CONTEXT_ID);

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
		String generatorId = benchmarkEntry.getGeneratorId();
		TaskEntry generatorEntry = clusterContext.getTasks().getTask(generatorId);
		TaskDescriptor benchmarkTaskDescriptor = generatorEntry.getTaskDescriptor();

		String taskId = taskContexts.submitBenchmarkTask(benchmarkTaskDescriptor, benchmarkId);
		benchmarkEntry.setGeneratorId(taskId);

		put(benchmarkEntry);

		return benchmarkEntry.getId();
	}

}
