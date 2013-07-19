package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.StateChangeEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.*;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_TASKS)
public class List extends Page {

	@Property
	private TaskEntry task;

	@Property
	private ArrayList<TaskEntry> context;

	@Property
	private int taskIndex;

	public ArrayList<ArrayList<TaskEntry>> getContexts() {
		Collection<TaskEntry> allTasks = this.api.getApi().getTasks();

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
			String contextId = taskEntry.getTaskContextId();
			if (!tasksByContexts.containsKey(contextId))
				tasksByContexts.put(contextId, new ArrayList<TaskEntry>());
			tasksByContexts.get(contextId).add(taskEntry);
		}

		return new ArrayList<>(tasksByContexts.values());
	}

	public boolean isFirstInContext() {
		return taskIndex == 0;
	}

	public Date taskLastChanged(TaskEntry taskEntry) {
		java.util.List<StateChangeEntry> logEntries = taskEntry.getStateChangeLog().getLogEntries();
		if (logEntries.size() == 0) return null;
		StateChangeEntry entry = logEntries.get(0);
		if (entry.getTimestamp() == 0) return null;
		return new Date(entry.getTimestamp());
	}

}
