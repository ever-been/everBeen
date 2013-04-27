package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;

/**
 * @author Kuba Brecka
 */
public class TaskLogs extends Page {

	@Property
	private TaskEntry task;

	@Property
	private Collection<LogMessage> logs;

	@Property
	private LogMessage log;

	void onActivate(String taskId) {
		task = api.getApi().getTask(taskId);
		logs = api.getApi().getLogs(taskId);
	}

}
