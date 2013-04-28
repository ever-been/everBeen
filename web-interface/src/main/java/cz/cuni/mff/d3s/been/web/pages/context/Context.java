package cz.cuni.mff.d3s.been.web.pages.context;

import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.CONTEXT_DETAIL)
public class Context extends Page {

	@Property
	private TaskContextEntry context;

	void onActivate(String taskContextId) {
		context = api.getApi().getTaskContext(taskContextId);
	}

}
