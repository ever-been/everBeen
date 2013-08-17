package cz.cuni.mff.d3s.been.web.pages.task;

import java.util.*;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.got5.tapestry5.jquery.ImportJQueryUI;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * User: donarus Date: 4/22/13 Time: 1:38 PM
 */
@Page.Navigation(section = Layout.Section.TASK_TREE)
@ImportJQueryUI
@Import(library = { "context:js/task-list.js" })
public class Tree extends Page {

	private static final int ACTION_WAIT_TIMEOUT = 10;
	@Property
	private TaskEntry task;

	@Property
	private TaskContextEntry context;

	@Property
	private BenchmarkEntry benchmark;

	public boolean isSwRepositoryOnline() throws BeenApiException {
		return this.api.getApi().isSwRepositoryOnline();
	}

	public Collection<BenchmarkEntry> getBenchmarks() throws BeenApiException {
		return this.api.getApi().getBenchmarks();

	}

	public Collection<TaskContextEntry> contextsForBenchmark(String benchmarkId) throws BeenApiException {
		Collection<TaskContextEntry> contexts = this.api.getApi().getTaskContextsInBenchmark(benchmarkId);
		ArrayList<TaskContextEntry> arrayList = new ArrayList<>(contexts);
		Collections.sort(arrayList, new Comparator<TaskContextEntry>() {
			@Override
			public int compare(TaskContextEntry o1, TaskContextEntry o2) {
				return Long.compare(o2.getCreated(), o1.getCreated());
			}
		});
		return arrayList;
	}

	public Collection<TaskEntry> tasksForContext(String contextId) throws BeenApiException {
		return this.api.getApi().getTasksInTaskContext(contextId);
	}

	public String benchmarkName(String benchmarkId) throws BeenApiException {
		String generatorId = this.api.getApi().getBenchmark(benchmarkId).getGeneratorId();
		TaskEntry taskEntry = this.api.getApi().getTask(generatorId);
		if (taskEntry == null)
			return "";
		return taskEntry.getTaskDescriptor().getName();
	}

	public TaskState benchmarkState(String benchmarkId) throws BeenApiException {
		String generatorId = this.api.getApi().getBenchmark(benchmarkId).getGeneratorId();
		TaskEntry taskEntry = this.api.getApi().getTask(generatorId);
		if (taskEntry == null)
			return null;
		return taskEntry.getState();
	}

	@Property
	private ArrayList<TaskEntry> orphanedContext;

	@Property
	private int taskIndex;

	public ArrayList<ArrayList<TaskEntry>> getOrphanedContexts() throws BeenApiException {
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

	private Collection<String> getGeneratorTaskIds() throws BeenApiException {
		ArrayList<String> result = new ArrayList<>();
		for (BenchmarkEntry benchmarkEntry : this.getBenchmarks()) {
			result.add(benchmarkEntry.getGeneratorId());
		}
		return result;
	}

	private Collection<String> getTaskIdsFromTree() throws BeenApiException {
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

	public Object onActionFromKillBenchmark(String benchmarkId) throws BeenApiException, InterruptedException {
		api.getApi().killBenchmark(benchmarkId);
		int time = 0;
		TaskEntry generator = getGenerator(benchmarkId);
		while (time < ACTION_WAIT_TIMEOUT && generator != null && generator.getState() != TaskState.ABORTED && generator.getState() != TaskState.FINISHED) {
			Thread.sleep(1000);
			time++;
			generator = getGenerator(benchmarkId);
		}
		return this;
	}

	public Object onActionFromRemoveBenchmark(String benchmarkId) throws BeenApiException {
		this.api.getApi().removeBenchmarkEntry(benchmarkId);
		return this;
	}

	public Object onActionFromRemoveAllFinishedBenchmarks() throws BeenApiException {
		for (BenchmarkEntry benchmarkEntry : this.api.getApi().getBenchmarks()) {
			TaskEntry generatorTask = this.api.getApi().getTask(benchmarkEntry.getGeneratorId());
			if (generatorTask.getState() == TaskState.FINISHED) {
				this.api.getApi().removeBenchmarkEntry(benchmarkEntry.getId());
			}
		}
		return this;
	}

	// reloads fresh instance from hazelacast cluster.
	private TaskEntry getGenerator(String benchmarkId) throws BeenApiException {
		BenchmarkEntry benchmark = api.getApi().getBenchmark(benchmarkId);
		if (benchmark != null) {
			return api.getApi().getTask(benchmark.getGeneratorId());
		}
		return null;
	}
}
