package cz.cuni.mff.d3s.been.web.pages.benchmark;

import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.web.pages.Page;
import cz.cuni.mff.d3s.been.web.pages.task.Tree;

/**
 * @author Kuba Brecka
 */
public class Remove extends Page {

	Object onActivate(String benchmarkId) {

		if (benchmarkId.equals("all-finished")) {
			for (BenchmarkEntry benchmarkEntry : this.api.getApi().getBenchmarks()) {
				TaskEntry generatorTask = this.api.getApi().getTask(benchmarkEntry.getGeneratorId());
				if (generatorTask.getState() == TaskState.FINISHED) {
					this.api.getApi().removeBenchmarkEntry(benchmarkEntry.getId());
				}
			}

		} else {
			this.api.getApi().removeBenchmarkEntry(benchmarkId);
		}

		return Tree.class;
	}

}
