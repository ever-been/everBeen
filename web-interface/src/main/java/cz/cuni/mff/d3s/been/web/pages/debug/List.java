package cz.cuni.mff.d3s.been.web.pages.debug;

import cz.cuni.mff.d3s.been.debugassistant.DebugListItem;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.DEBUG_LIST)
public class List extends Page {

	@Property
	private Collection<DebugListItem> tasks;

	@Property
	private DebugListItem task;

	void onActivate() {
		tasks = this.api.getApi().getDebugWaitingTasks();
	}

}
