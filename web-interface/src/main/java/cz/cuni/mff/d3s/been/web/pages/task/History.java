package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.logging.LogMessage;
import cz.cuni.mff.d3s.been.core.task.StateChangeEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.*;
import java.util.List;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
public class History extends Page {

	@Property
	private TaskEntry task;

	@Property
	private List<StateChangeEntry> history;

	@Property
	private StateChangeEntry entry;

	void onActivate(String taskId) {
		task = api.getApi().getTask(taskId);
		history = task.getStateChangeLog().getLogEntries();
	}

}
