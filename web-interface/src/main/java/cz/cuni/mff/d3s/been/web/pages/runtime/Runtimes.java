package cz.cuni.mff.d3s.been.web.pages.runtime;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.RUNTIME_LIST)
public class Runtimes extends Page {

	public Collection<RuntimeInfo> getRuntimes() {
		return this.api.getApi().getRuntimes();
	}

	@Property
	private RuntimeInfo runtime;

}
