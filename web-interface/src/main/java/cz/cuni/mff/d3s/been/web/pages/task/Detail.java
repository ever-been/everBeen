package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.core.task.Debug;
import cz.cuni.mff.d3s.been.core.task.ModeEnum;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskProperty;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.DetailPage;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

/**
 * User: donarus Date: 4/22/13 Time: 1:38 PM
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
public class Detail extends DetailPage {

	@Property
	private String arg;

	@Property
	private TaskProperty property;

	public TaskEntry getTask() {
		return api.getApi().getTask(itemId);
	}

	public String taskDebugToString(Debug debug) {
		if (debug.getMode() == ModeEnum.NONE) return "NONE";
		else if (debug.getMode() == ModeEnum.LISTEN) return "LISTEN, port: " + debug.getPort();
		else if (debug.getMode() == ModeEnum.CONNECT) return "CONNECT, host: " + debug.getHost() + ", port: " + debug.getPort();

		throw new IllegalStateException("Invalid enum value.");
	}
}
