package cz.cuni.mff.d3s.been.web.pages.context;

import cz.cuni.mff.d3s.been.core.task.StateChangeEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.*;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_TASKS)
public class List extends Page {

	@Property
	private TaskContextEntry context;

	public ArrayList<TaskContextEntry> getContexts() {
		Collection<TaskContextEntry> allContexts = this.api.getApi().getTaskContexts();

		ArrayList<TaskContextEntry> entries = new ArrayList<>(allContexts);
		Collections.sort(entries, new Comparator<TaskContextEntry>() {
			@Override
			public int compare(TaskContextEntry o1, TaskContextEntry o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});

		return entries;
	}

}
