package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * @author Kuba Brecka
 */
public class Kill extends Page {

	Object onActivate(String taskId) {
		this.api.getApi().killTask(taskId);

		return Tree.class;
	}

}
