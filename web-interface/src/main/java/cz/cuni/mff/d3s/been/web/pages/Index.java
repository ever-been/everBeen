package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.services.LiveFeedService;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.got5.tapestry5.jquery.ImportJQueryUI;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: donarus Date: 4/22/13 Time: 12:25 PM
 */
@Page.Navigation(section = Layout.Section.OVERVIEW)
@ImportJQueryUI
public class Index extends Page {

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	@Inject
	private LiveFeedService logFeed;

	@Inject
	private Block runtimesBlock;
	@Property
	private Collection<RuntimeInfo> runtimes;
	@Property
	private RuntimeInfo runtime;

	@Inject
	private Block tasksBlock;
	@Property
	private ArrayList<ArrayList<TaskEntry>> contexts;
	@Property
	private ArrayList<TaskEntry> context;
	@Property
	private TaskEntry task;

	@Property
	private int taskIndex;

	public Block onRuntimesUpdated(final Collection<RuntimeInfo> message) {
		runtimes = message;
		return runtimesBlock;
	}

	public Block onTasksUpdated(final Collection<TaskEntry> message) {
		Map<String, ArrayList<TaskEntry>> tasksByContexts = new HashMap<>();
		for (TaskEntry taskEntry : message) {
			String contextId = taskEntry.getTaskContextId();
			if (! tasksByContexts.containsKey(contextId))
				tasksByContexts.put(contextId, new ArrayList<TaskEntry>());
			tasksByContexts.get(contextId).add(taskEntry);
		}
		this.contexts = new ArrayList<>(tasksByContexts.values());

		return tasksBlock;
	}

	public boolean isFirstInContext() {
		return taskIndex == 0;
	}
}
