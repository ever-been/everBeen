package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;

/**
 * @author Kuba Brecka
 */
public class Runtimes extends Page {

	public Collection<RuntimeInfo> getRuntimes() {
		return this.api.getApi().getRuntimes();
	}

	@Property
	private RuntimeInfo runtime;

}
