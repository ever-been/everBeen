package cz.cuni.mff.d3s.been.web.pages.task;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.got5.tapestry5.jquery.ImportJQueryUI;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.model.BenchmarkSupport;
import cz.cuni.mff.d3s.been.web.model.TaskContextSupport;
import cz.cuni.mff.d3s.been.web.model.TaskSupport;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * User: donarus Date: 4/22/13 Time: 1:38 PM
 */
@Page.Navigation(section = Layout.Section.TASK_TREE)
@ImportJQueryUI
@Import(library = { "context:js/task-list.js" })
public class Tree extends Page {

	@Property
	private TaskEntry task;

	@Property
	private TaskContextEntry context;

	@Property
	private BenchmarkEntry benchmark;

	@Property
	private ArrayList<TaskEntry> orphanedContext;

	@Property
	private int taskIndex;

	public Collection<BenchmarkEntry> getBenchmarks() throws BeenApiException {
		return getApi().getBenchmarks();
	}

	public Collection<TaskContextEntry> contextsForBenchmark(String benchmarkId) throws BeenApiException {
		return new BenchmarkSupport(getApi()).getContextsForBenchmark(benchmarkId);
	}

	public Collection<TaskEntry> tasksForContext(String contextId) throws BeenApiException {
		return getApi().getTasksInTaskContext(contextId);
	}

	public String benchmarkName(String benchmarkId) throws BeenApiException {
		return new BenchmarkSupport(getApi()).getBenchmarkName(benchmarkId);
	}

	public TaskState benchmarkState(String benchmarkId) throws BeenApiException {
		return new BenchmarkSupport(getApi()).getBenchmarkState(benchmarkId);
	}

	public ArrayList<ArrayList<TaskEntry>> getOrphanedContexts() throws BeenApiException {
		return new TaskContextSupport(getApi()).getOrphanedContexts();
	}

	public Object onKillBenchmark(String benchmarkId) throws BeenApiException, InterruptedException {
		new BenchmarkSupport(getApi()).killBenchmark(benchmarkId);
		return this;
	}

	public Object onKillContext(String contextId) throws BeenApiException, InterruptedException {
		new TaskContextSupport(getApi()).killTaskContext(contextId);
		return this;
	}

	public Object onRemoveBenchmark(String benchmarkId) throws BeenApiException {
		new BenchmarkSupport(getApi()).removeKilledBenchmark(benchmarkId);
		return this;
	}

	public Object onRemoveContext(String contextId) throws BeenApiException {
		new TaskContextSupport(getApi()).removeKilledTaskContext(contextId);
		return this;
	}

	public Object onRemoveFinishedBenchmarks() throws BeenApiException {
		new BenchmarkSupport(getApi()).removedFinishedBenchmarks();
		return this;
	}

	public TaskContextEntry getTaskContextWithId(String contextId) throws BeenApiException {
		return api.getApi().getTaskContext(contextId);
	}

	public boolean isContextRemovable(String contextId) throws BeenApiException {
		return new TaskContextSupport(getApi()).isContextRemovable(contextId);
	}

	public boolean isBenchmarkRemovable(String benchmarkId) throws BeenApiException {
		return new BenchmarkSupport(getApi()).isBenchmarkRemovable(benchmarkId);
	}

	public boolean isBenchmarkInFinalState(String benchmarkId) throws BeenApiException {
		return new BenchmarkSupport(getApi()).isBenchmarkInFinalState(benchmarkId);
	}

	public boolean isTaskContextInFinalState(String taskContextId) throws BeenApiException {
		return new TaskContextSupport(getApi()).isTaskContextInFinalState(taskContextId);
	}

	public boolean isTaskInFinalState(String taskId) throws BeenApiException {
		return new TaskSupport(getApi()).isTaskInFinalState(taskId);
	}

}
