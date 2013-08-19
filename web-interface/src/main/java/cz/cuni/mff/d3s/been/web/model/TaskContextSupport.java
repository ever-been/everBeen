package cz.cuni.mff.d3s.been.web.model;

import java.util.*;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextState;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * @author donarus
 */
public final class TaskContextSupport {

	private final BeenApi api;
	private ArrayList<ArrayList<TaskEntry>> orphanedContexts;

	public TaskContextSupport(BeenApi api) {
		this.api = api;
	}

	public void killTaskContext(String contextId) throws BeenApiException, InterruptedException {
		api.killTaskContext(contextId);
		int time = 0;
		TaskContextEntry entry = api.getTaskContext(contextId);
		while (time < Timeouts.KILL_TASK_CONTEXT_TIMEOUT && entry != null && entry.getContextState() != TaskContextState.FAILED && entry.getContextState() != TaskContextState.FINISHED) {
			Thread.sleep(1000);
			time++;
			entry = api.getTaskContext(contextId);
		}
	}

	public void removeKilledTaskContext(String contextId) throws BeenApiException {
		this.api.removeTaskContextEntry(contextId);
	}

	public boolean isContextRemovable(String contextId) throws BeenApiException {
		TaskContextEntry context = api.getTaskContext(contextId);
		if (context.getContextState() != TaskContextState.FINISHED && context.getContextState() != TaskContextState.FAILED) {
			return false;
		}
		return true;
	}

	public boolean isTaskContextInFinalState(String taskContextId) throws BeenApiException {
		TaskContextEntry taskContextEntry = this.api.getTaskContext(taskContextId);
		TaskContextState state = taskContextEntry.getContextState();
		return state == TaskContextState.FAILED || state == TaskContextState.FINISHED;
	}

	public ArrayList<ArrayList<TaskEntry>> getOrphanedContexts() throws BeenApiException {
		Collection<TaskEntry> allTasks = api.getTasks();
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
		for (BenchmarkEntry benchmarkEntry : api.getBenchmarks()) {
			result.add(benchmarkEntry.getGeneratorId());
		}
		return result;
	}

	private Collection<String> getTaskIdsFromTree() throws BeenApiException {
		ArrayList<String> result = new ArrayList<>();
		for (BenchmarkEntry benchmarkEntry : api.getBenchmarks()) {
			for (TaskContextEntry taskContextEntry : api.getTaskContextsInBenchmark(benchmarkEntry.getId())) {
				for (TaskEntry taskEntry : api.getTasksInTaskContext(taskContextEntry.getId())) {
					result.add(taskEntry.getId());
				}
			}
		}
		return result;
	}
}
