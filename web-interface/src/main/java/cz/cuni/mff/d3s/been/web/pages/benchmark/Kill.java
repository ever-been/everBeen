package cz.cuni.mff.d3s.been.web.pages.benchmark;

import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.web.pages.Page;
import cz.cuni.mff.d3s.been.web.pages.task.Tree;

/**
 * @author Kuba Brecka
 */
public class Kill extends Page {

	Object onActivate(String benchmarkId) {
		this.api.getApi().killBenchmark(benchmarkId);

		return Tree.class;
	}

}
