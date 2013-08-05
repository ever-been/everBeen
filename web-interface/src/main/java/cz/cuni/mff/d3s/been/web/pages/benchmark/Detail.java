package cz.cuni.mff.d3s.been.web.pages.benchmark;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistoryItem;
import cz.cuni.mff.d3s.been.core.benchmark.StorageItem;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;

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
	        this.removable = this.isBenchmarkRemovable(benchmark);
        }
    }

    Object onPasivate() {
        return benchmarkId;
    }

}
