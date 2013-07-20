package cz.cuni.mff.d3s.been.web.pages.context;

import cz.cuni.mff.d3s.been.web.pages.Page;
import cz.cuni.mff.d3s.been.web.pages.task.Tree;

/**
 * @author Kuba Brecka
 */
public class Kill extends Page {
	Object onActivate(String contextId) {
		this.api.getApi().killTaskContext(contextId);
		return Tree.class;
	}
}
