package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import org.apache.tapestry5.annotations.Property;

/**
 * User: donarus Date: 4/22/13 Time: 1:38 PM
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
public class Task extends Page {

	@Property
	private TaskEntry task;

	void onActivate(String taskId) {
		task = api.getApi().getTask(taskId);
	}
}
