package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
public class Logs extends Page {

	@Property
	private TaskEntry task;

	@Property
	private Collection<LogMessage> logs;

	@Property
	private LogMessage log;

	void onActivate(String taskId) {
		task = api.getApi().getTask(taskId);
		logs = api.getApi().getLogsForTask(taskId);
	}

}
