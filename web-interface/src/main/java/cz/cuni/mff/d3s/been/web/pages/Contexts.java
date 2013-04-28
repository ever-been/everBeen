package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.CONTEXT_LIST)
public class Contexts extends Page {

	public Collection<TaskContextEntry> getContexts() {
		return this.api.getApi().getTaskContexts();
	}

	@Property
	private TaskContextEntry context;

}
