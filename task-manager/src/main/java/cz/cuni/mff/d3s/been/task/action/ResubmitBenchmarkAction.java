package cz.cuni.mff.d3s.been.task.action;

import static cz.cuni.mff.d3s.been.task.action.ResubmitActionConfiguration.DEFAULT_MAXIMUM_ALLOWED_RESUBMITS;
import static cz.cuni.mff.d3s.been.task.action.ResubmitActionConfiguration.MAXIMUM_ALLOWED_RESUBMITS;

import java.util.List;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.PropertyReader;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistoryItem;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * @author Kuba Brecka
 */
final class ResubmitBenchmarkAction implements TaskAction {
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

			if (!benchmarkEntry.isAllowResubmit()) {
				return;
			}

			List<ResubmitHistoryItem> resubmits = benchmarkEntry.getResubmitHistory().getResubmitHistoryItem();

			PropertyReader propertyReader = PropertyReader.on(ctx.getProperties());
			int maximumResubmits = propertyReader.getInteger(MAXIMUM_ALLOWED_RESUBMITS, DEFAULT_MAXIMUM_ALLOWED_RESUBMITS);
			if (resubmits.size() >= maximumResubmits) {
				return;
			}

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
