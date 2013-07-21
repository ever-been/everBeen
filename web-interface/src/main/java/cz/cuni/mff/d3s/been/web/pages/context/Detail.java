package cz.cuni.mff.d3s.been.web.pages.context;

import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskProperty;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.*;
import java.util.List;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
public class Detail extends Page {

	@Property
	private TaskContextEntry context;

	@Property
	private List<String> taskIds;

	@Property
	private String taskId;

	@Property
	private Map<String, TaskEntry> tasksMap;

	@Property
	private TaskEntry task;

	@Property
	private cz.cuni.mff.d3s.been.core.task.Property property;

	void onActivate(String taskContextId) {
		context = api.getApi().getTaskContext(taskContextId);

		taskIds = context.getContainedTask();
		tasksMap = new HashMap<>();
		for (TaskEntry taskEntry : this.api.getApi().getTasksInTaskContext(taskContextId)) {
			if (taskEntry == null) continue;
			tasksMap.put(taskEntry.getId(), taskEntry);
		}
	}

	public TaskEntry taskEntryWithId(String taskId) {
		return tasksMap.get(taskId);
	}
}
