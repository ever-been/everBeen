package cz.cuni.mff.d3s.been.web.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextState;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * @author donarus
 */
public final class BenchmarkSupport {

	private final BeenApi api;

	public BenchmarkSupport(BeenApi api) {
		this.api = api;
	}

	// ****************
	// CLEANING METHODS
	// ****************

	public void killBenchmark(String benchmarkId) throws BeenApiException, InterruptedException {
		api.killBenchmark(benchmarkId);
		int time = 0;
		TaskEntry generator = getGenerator(benchmarkId);
		while (time < Timeouts.KILL_BENCHMARK_TIMEOUT && generator != null && generator.getState() != TaskState.ABORTED && generator.getState() != TaskState.FINISHED) {
			Thread.sleep(1000);
			time++;
			generator = getGenerator(benchmarkId);
		}
	}

	public void removeKilledBenchmark(String benchmarkId) throws BeenApiException {
		this.api.removeBenchmarkEntry(benchmarkId);
	}

	public void removedFinishedBenchmarks() throws BeenApiException {
		for (BenchmarkEntry benchmarkEntry : this.api.getBenchmarks()) {
			TaskEntry generatorTask = this.api.getTask(benchmarkEntry.getGeneratorId());
			if (generatorTask.getState() == TaskState.FINISHED) {
				this.api.removeBenchmarkEntry(benchmarkEntry.getId());
			}
		}
	}

	// **********************
	// BENCHMARK INFO METHODS
	// **********************

	public TaskState getBenchmarkState(String benchmarkId) throws BeenApiException {
		String generatorId = this.api.getBenchmark(benchmarkId).getGeneratorId();
		TaskEntry taskEntry = this.api.getTask(generatorId);
		if (taskEntry == null) {
			return null;
		}
		return taskEntry.getState();
	}

	public String getBenchmarkName(String benchmarkId) throws BeenApiException {
		String generatorId = this.api.getBenchmark(benchmarkId).getGeneratorId();
		TaskEntry taskEntry = this.api.getTask(generatorId);
		if (taskEntry == null) {
			return "";
		}
		return taskEntry.getTaskDescriptor().getName();
	}

	/**
	 * This method collects task contexts contained in benchmark
	 * 
	 * @param benchmarkId
	 * @return collected contexts
	 * 
	 * @throws BeenApiException
	 */
	public Collection<TaskContextEntry> getContextsForBenchmark(String benchmarkId) throws BeenApiException {
		Collection<TaskContextEntry> contexts = this.api.getTaskContextsInBenchmark(benchmarkId);
		ArrayList<TaskContextEntry> arrayList = new ArrayList<>(contexts);
		Collections.sort(arrayList, new Comparator<TaskContextEntry>() {
			@Override
			public int compare(TaskContextEntry o1, TaskContextEntry o2) {
				return Long.compare(o2.getCreated(), o1.getCreated());
			}
		});
		return arrayList;
	}

	// ***************
	// PRIVATE METHODS
	// ***************

	// reloads fresh instance from hazelcast cluster.
	public TaskEntry getGenerator(String benchmarkId) throws BeenApiException {
		BenchmarkEntry benchmark = api.getBenchmark(benchmarkId);
		if (benchmark != null) {
			return api.getTask(benchmark.getGeneratorId());
		}
		return null;
	}

	public boolean isBenchmarkRemovable(String benchmarkId) throws BeenApiException {

		Collection<TaskContextEntry> contexts = api.getTaskContextsInBenchmark(benchmarkId);
		for (TaskContextEntry context : contexts) {
			if (context.getContextState() != TaskContextState.FINISHED && context.getContextState() != TaskContextState.FAILED) {
				return false;
			}
		}

		return true;
	}

	public boolean isBenchmarkInFinalState(String benchmarkId) throws BeenApiException {
		String generatorId = this.api.getBenchmark(benchmarkId).getGeneratorId();
		TaskEntry taskEntry = this.api.getTask(generatorId);
		if (taskEntry == null)
			return true;
		TaskState state = taskEntry.getState();

		return state == TaskState.ABORTED || state == TaskState.FINISHED;
	}

	public boolean hasBenchmarkHaveFailedContexts(String benchmarkId) throws BeenApiException {
		Collection<TaskContextEntry> contexts = getContextsForBenchmark(benchmarkId);

		for (TaskContextEntry taskContextEntry : contexts) {
			if (taskContextEntry.getContextState() != TaskContextState.FINISHED) {
				return true;
			}
		}

		return false;
	}

}
