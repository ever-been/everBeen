package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import org.apache.tapestry5.annotations.Property;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.RUNTIME_DETAIL)
public class Runtime extends Page {

	@Property
	private RuntimeInfo runtime;

	void onActivate(String runtimeId) {
		runtime = api.getApi().getRuntime(runtimeId);
	}

}
