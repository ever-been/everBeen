package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.DetailPage;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * User: donarus Date: 4/22/13 Time: 1:38 PM
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
public class Detail extends DetailPage {

	public TaskEntry getTask() {
		return api.getApi().getTask(itemId);
	}

}
