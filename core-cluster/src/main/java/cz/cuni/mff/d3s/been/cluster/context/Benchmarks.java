package cz.cuni.mff.d3s.been.cluster.context;

import com.hazelcast.core.IMap;

import com.hazelcast.query.SqlPredicate;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.benchmark.Storage;
import cz.cuni.mff.d3s.been.core.task.*;

import java.util.Collection;
import java.util.UUID;

import static cz.cuni.mff.d3s.been.cluster.Names.BENCHMARKS_CONTEXT_ID;

/**
 * @author Martin Sixta
 */
public class Benchmarks {
	private final ClusterContext clusterContext;

	public Benchmarks(ClusterContext clusterContext) {
		this.clusterContext = clusterContext;
	}

	public IMap<String, BenchmarkEntry> getBenchmarksMap() {
		return clusterContext.getMap(Names.BENCHMARKS_MAP_NAME);
	}

	public void put(BenchmarkEntry entry) {
		getBenchmarksMap().put(entry.getId(), entry);
	}

	public BenchmarkEntry get(String id) {
		return getBenchmarksMap().get(id);
	}

	public void addBenchmarkToBenchmarksContext(TaskEntry taskEntry) {
		IMap<String, TaskContextEntry> taskContextsMap = clusterContext.getTaskContexts().getTaskContextsMap();
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

	public void removeBenchmarkFromBenchmarksContext(TaskEntry taskEntry) {
		IMap<String, TaskContextEntry> taskContextsMap = clusterContext.getTaskContexts().getTaskContextsMap();
		try {
			taskContextsMap.lock(BENCHMARKS_CONTEXT_ID);

			TaskContextEntry taskContextEntry = taskContextsMap.get(BENCHMARKS_CONTEXT_ID);
			if (taskContextEntry == null) {
				throw new RuntimeException("Benchmarks context does not exist.");
			}

			taskContextEntry.getContainedTask().remove(taskEntry.getId());

			taskContextsMap.put(BENCHMARKS_CONTEXT_ID, taskContextEntry);
		} finally {
			taskContextsMap.unlock(BENCHMARKS_CONTEXT_ID);
		}
	}

	public String submit(TaskDescriptor benchmarkTaskDescriptor) {
		if (benchmarkTaskDescriptor.getType() != TaskType.BENCHMARK) {
			throw new IllegalArgumentException("TaskDescriptor's type is not benchmark.");
		}

		BenchmarkEntry benchmarkEntry = new BenchmarkEntry();
		benchmarkEntry.setStorage(new Storage());
		String benchmarkId = UUID.randomUUID().toString();
		benchmarkEntry.setId(benchmarkId);
		String taskId = clusterContext.getTaskContexts().submitBenchmarkTask(benchmarkTaskDescriptor, benchmarkId);
		benchmarkEntry.setGeneratorId(taskId);

		put(benchmarkEntry);

		return benchmarkEntry.getId();
	}

	public String resubmit(BenchmarkEntry benchmarkEntry) {
		String benchmarkId = benchmarkEntry.getId();
		String generatorId = benchmarkEntry.getGeneratorId();
		TaskEntry generatorEntry = clusterContext.getTasks().getTask(generatorId);
		TaskDescriptor benchmarkTaskDescriptor = generatorEntry.getTaskDescriptor();

		String taskId = clusterContext.getTaskContexts().submitBenchmarkTask(benchmarkTaskDescriptor, benchmarkId);
		benchmarkEntry.setGeneratorId(taskId);

		put(benchmarkEntry);

		return benchmarkEntry.getId();
	}

	public Collection<TaskContextEntry> getTaskContextsInBenchmark(String benchmarkId) {
		SqlPredicate predicate = new SqlPredicate(String.format("benchmarkId = '%s'", benchmarkId));
		return clusterContext.getTaskContexts().getTaskContextsMap().values(predicate);
	}
}
