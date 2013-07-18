package cz.cuni.mff.d3s.been.web.pages.task;

import java.util.*;

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
@Page.Navigation(section = Layout.Section.TASK_TREE)
@ImportJQueryUI
@Import(library={"context:js/task-list.js"})
public class Tree extends Page {

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

	@Property
	private ArrayList<TaskEntry> orphanedContext;

	@Property
	private int taskIndex;

	public ArrayList<ArrayList<TaskEntry>> getOrphanedContexts() {
		Collection<TaskEntry> allTasks = this.api.getApi().getTasks();
		Collection<String> tasksInTree = getTaskIdsFromTree();
		Collection<String> generatorTasks = getGeneratorTaskIds();

		ArrayList<TaskEntry> taskEntries = new ArrayList<>(allTasks);
		Collections.sort(taskEntries, new Comparator<TaskEntry>() {
			@Override
			public int compare(TaskEntry o1, TaskEntry o2) {
				int order = o1.getTaskContextId().compareTo(o2.getTaskContextId());
				if (order == 0)
					order = o1.getId().compareTo(o2.getId());
				return order;
			}
		});

		Map<String, ArrayList<TaskEntry>> tasksByContexts = new LinkedHashMap<>();

		for (TaskEntry taskEntry : taskEntries) {
			if (tasksInTree.contains(taskEntry.getId())) {
				continue;
			}

			if (generatorTasks.contains(taskEntry.getId())) {
				continue;
			}

			String contextId = taskEntry.getTaskContextId();
			if (!tasksByContexts.containsKey(contextId))
				tasksByContexts.put(contextId, new ArrayList<TaskEntry>());
			tasksByContexts.get(contextId).add(taskEntry);
		}

		return new ArrayList<>(tasksByContexts.values());
	}

	private Collection<String> getGeneratorTaskIds() {
		ArrayList<String> result = new ArrayList<>();
		for (BenchmarkEntry benchmarkEntry : this.getBenchmarks()) {
			result.add(benchmarkEntry.getGeneratorId());
		}
		return result;
	}

	private Collection<String> getTaskIdsFromTree() {
		ArrayList<String> result = new ArrayList<>();
		for (BenchmarkEntry benchmarkEntry : getBenchmarks()) {
			for (TaskContextEntry taskContextEntry : contextsForBenchmark(benchmarkEntry.getId())) {
				for (TaskEntry taskEntry : tasksForContext(taskContextEntry.getId())) {
					result.add(taskEntry.getId());
				}
			}
		}
		return result;
	}

	public boolean isFirstInContext() {
		return taskIndex == 0;
	}
}
