package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import org.apache.tapestry5.annotations.Property;

/**
 * @author Kuba Brecka
 */
public class Context extends Page {

	@Property
	private TaskContextEntry context;

	void onActivate(String taskContextId) {
		context = api.getApi().getTaskContext(taskContextId);
	}

}
