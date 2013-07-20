package cz.cuni.mff.d3s.been.web.pages.benchmark;

import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistoryItem;
import cz.cuni.mff.d3s.been.core.benchmark.StorageItem;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
public class Detail extends Page {

	@Property
	BenchmarkEntry benchmark;

	@Property
	TaskEntry generator;

	@Property
	StorageItem storageItem;

	@Property
	ResubmitHistoryItem resubmit;

	@Property
	Collection<TaskContextEntry> contexts;

	@Property
	TaskContextEntry context;

	void onActivate(String benchmarkId) {
		benchmark = api.getApi().getBenchmark(benchmarkId);
		generator = api.getApi().getTask(benchmark.getGeneratorId());
		contexts = api.getApi().getTaskContextsInBenchmark(benchmarkId);
	}

}
