package cz.cuni.mff.d3s.been.task.action;

import com.hazelcast.core.IMap;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * @author Kuba Brecka
 */
public class ResubmitBenchmarkAction implements TaskAction {
	private final ClusterContext ctx;
	private final TaskEntry entry;

	public ResubmitBenchmarkAction(ClusterContext ctx, TaskEntry entry) {
		this.ctx = ctx;
		this.entry = entry;
	}

	@Override
	public void execute() throws TaskActionException {
		String benchmarkId = entry.getBenchmarkId();
		IMap<String, BenchmarkEntry> benchmarksMap = ctx.getBenchmarks().getBenchmarksMap();

		benchmarksMap.lock(benchmarkId);
		try {
			BenchmarkEntry benchmarkEntry = ctx.getBenchmarks().get(benchmarkId);
			String generatorId = benchmarkEntry.getGeneratorId();
			TaskEntry generatorTask = ctx.getTasks().getTask(generatorId);

			// fail-safe check for race conditions
			if (generatorTask.getState() != TaskState.ABORTED) {
				return;
			}

			ctx.getBenchmarks().resubmit(benchmarkEntry);
		} finally {
			benchmarksMap.unlock(benchmarkId);
		}

	}
}
