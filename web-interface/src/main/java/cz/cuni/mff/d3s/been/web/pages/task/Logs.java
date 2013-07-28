package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.logging.LogMessage;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.logging.ServiceLogMessage;
import cz.cuni.mff.d3s.been.logging.TaskLogMessage;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.got5.tapestry5.jquery.ImportJQueryUI;

import java.util.Collection;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
@ImportJQueryUI
@Import(library = { "context:js/logs.js" })
public class Logs extends Page {

	@Property
	private TaskEntry task;

	@Property
	private Collection<TaskLogMessage> logs;

	@Property
	private TaskLogMessage log;

	private JSONUtils jsonUtils = JSONUtils.newInstance();

	void onActivate(String taskId) throws DAOException, BeenApiException {
		task = api.getApi().getTask(taskId);
		logs = api.getApi().getLogsForTask(taskId);
	}

	public String shortLogMessage(LogMessage log) {
		if (log.getMessage().length() < 80) {
			return log.getMessage();
		}

		return log.getMessage().substring(0, 75) + " ...";
	}

	public String shortClass(LogMessage log) {
		int idx = log.getName().lastIndexOf('.');
		if (idx == -1) return log.getName();
		return "..." + log.getName().substring(idx + 1);
	}

    public String jsonTaskLog(TaskLogMessage taskLog) {
        try {
            return jsonUtils.serialize(taskLog);
        } catch (JsonException e) {
            return "";
        }
    }

}
