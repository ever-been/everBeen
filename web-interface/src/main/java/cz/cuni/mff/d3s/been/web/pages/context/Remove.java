package cz.cuni.mff.d3s.been.web.pages.context;

import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.web.pages.Page;
import cz.cuni.mff.d3s.been.web.pages.task.Tree;

/**
 * @author Kuba Brecka
 */
public class Remove extends Page {

	Object onActivate(String contextId) {
		this.api.getApi().removeTaskContextEntry(contextId);
		return Tree.class;
	}

}
