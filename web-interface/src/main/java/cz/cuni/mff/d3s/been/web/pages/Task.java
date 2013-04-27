package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import org.apache.tapestry5.annotations.Property;

import java.util.List;

/**
 * User: donarus Date: 4/22/13 Time: 1:38 PM
 */
public class Task extends Page {

	@Property
	private TaskEntry task;

	void onActivate(String taskId) {
		task = api.getApi().getTask(taskId);
	}
}
