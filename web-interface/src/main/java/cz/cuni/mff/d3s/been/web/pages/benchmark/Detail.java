package cz.cuni.mff.d3s.been.web.pages.benchmark;

import java.util.Collection;

import org.apache.tapestry5.annotations.Property;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistoryItem;
import cz.cuni.mff.d3s.been.core.benchmark.StorageItem;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.model.BenchmarkSupport;
import cz.cuni.mff.d3s.been.web.pages.Page;
import cz.cuni.mff.d3s.been.web.pages.task.Tree;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
public class Detail extends Page {

	private static final int ACTION_WAIT_TIMEOUT = 10;

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

	@Property
	private String benchmarkId;

	@Property
	private boolean removable;

	void onActivate(String benchmarkId) throws BeenApiException {
		this.benchmarkId = benchmarkId;
		this.benchmark = api.getApi().getBenchmark(benchmarkId);

		if (benchmark != null) {
			this.generator = api.getApi().getTask(benchmark.getGeneratorId());
			this.contexts = api.getApi().getTaskContextsInBenchmark(benchmarkId);
			this.removable = new BenchmarkSupport(getApi()).isBenchmarkRemovable(benchmark.getId());
		}
	}

	Object onPasivate() {
		return benchmarkId;
	}

	public Object onRemoveBenchmark(String benchmarkId) throws BeenApiException {
		new BenchmarkSupport(api.getApi()).removeKilledBenchmark(benchmarkId);
		return Tree.class;
	}

	public Object onDisallowResubmit(String benchmarkId) throws BeenApiException {
		new BenchmarkSupport(api.getApi()).disallowResubmits(benchmarkId);
		return this;
	}

	public Object onKillBenchmark(String benchmarkId) throws BeenApiException, InterruptedException {
		new BenchmarkSupport(api.getApi()).killBenchmark(benchmarkId);
		return this;
	}

	// reloads fresh instance from hazelacast cluster.
	private TaskEntry getGenerator(String benchmarkId) throws BeenApiException {
		return new BenchmarkSupport(getApi()).getGenerator(benchmarkId);
	}

	public boolean isBenchmarkInFinalState(String benchmarkId) throws BeenApiException {
		return new BenchmarkSupport(getApi()).isBenchmarkInFinalState(benchmarkId);
	}

	public boolean isBenchmarkRemovable(String benchmarkId) throws BeenApiException {
		return new BenchmarkSupport(getApi()).isBenchmarkRemovable(benchmarkId);
	}

}
