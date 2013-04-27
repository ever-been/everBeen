package cz.cuni.mff.d3s.been.web.pages;

import java.util.Collection;
import java.util.List;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import org.apache.tapestry5.annotations.Property;

/**
 * User: donarus Date: 4/22/13 Time: 1:38 PM
 */
public class Tasks extends Page {

	public Collection<TaskEntry> getTasks() {
		return this.api.getApi().getTasks();
	}

	@Property
	private TaskEntry task;


}
