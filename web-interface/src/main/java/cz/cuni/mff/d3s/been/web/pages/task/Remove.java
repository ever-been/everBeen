package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * @author Kuba Brecka
 */
public class Remove extends Page {

	Object onActivate(String taskId) {
		this.api.getApi().removeTaskEntry(taskId);

		return Tree.class;
	}

}
