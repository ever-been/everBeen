package cz.cuni.mff.d3s.been.web.pages.task;

import java.util.Collection;

import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.got5.tapestry5.jquery.ImportJQueryUI;

/**
 * User: donarus Date: 4/22/13 Time: 1:38 PM
 */
@Page.Navigation(section = Layout.Section.TASK_LIST)
@ImportJQueryUI
@Import(library={"context:js/task-list.js"})
public class List extends Page {

	@Property
	private TaskEntry task;

	@Property
	private TaskContextEntry context;

	@Property
	private BenchmarkEntry benchmark;

	public Collection<BenchmarkEntry> getBenchmarks() {
		return this.api.getApi().getBenchmarks();
	}

	public Collection<TaskContextEntry> contextsForBenchmark(String benchmarkId) {
		return this.api.getApi().getTaskContextsInBenchmark(benchmarkId);
	}

	public Collection<TaskEntry> tasksForContext(String contextId) {
		return this.api.getApi().getTasksInTaskContext(contextId);
	}

	public String benchmarkName(String benchmarkId) {
		String generatorId = this.api.getApi().getBenchmark(benchmarkId).getGeneratorId();
		TaskEntry taskEntry = this.api.getApi().getTask(generatorId);
		return taskEntry.getTaskDescriptor().getName();
	}

	public TaskState benchmarkState(String benchmarkId) {
		String generatorId = this.api.getApi().getBenchmark(benchmarkId).getGeneratorId();
		TaskEntry taskEntry = this.api.getApi().getTask(generatorId);
		return taskEntry.getState();
	}

	public boolean benchmarkInFinalState(String benchmarkId) {
		String generatorId = this.api.getApi().getBenchmark(benchmarkId).getGeneratorId();
		TaskEntry taskEntry = this.api.getApi().getTask(generatorId);
		TaskState state = taskEntry.getState();

		return state == TaskState.ABORTED || state == TaskState.FINISHED;
	}

}
