package cz.cuni.mff.d3s.been.web.pages.task;

import java.util.Collection;

import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import org.apache.tapestry5.annotations.Property;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * User: donarus Date: 4/22/13 Time: 1:38 PM
 */
@Page.Navigation(section = Layout.Section.TASK_LIST)
public class List extends Page {

	public Collection<TaskEntry> getTasks() {
		return this.api.getApi().getTasks();
	}

	public Collection<TaskContextEntry> getContexts() {
		return this.api.getApi().getTaskContexts();
	}

	@Property
	private TaskEntry task;

	@Property
	private TaskContextEntry context;


}
