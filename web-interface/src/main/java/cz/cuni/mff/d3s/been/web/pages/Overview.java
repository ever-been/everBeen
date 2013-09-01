package cz.cuni.mff.d3s.been.web.pages;

import java.util.*;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.got5.tapestry5.jquery.ImportJQueryUI;

import cz.cuni.mff.d3s.been.core.ri.FilesystemSample;
import cz.cuni.mff.d3s.been.core.ri.NetworkSample;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.services.LiveFeedService;

/**
 * User: donarus Date: 4/22/13 Time: 12:25 PM
 */
@Page.Navigation(section = Layout.Section.OVERVIEW)
@ImportJQueryUI
@Import(library = { "context:js/jquery.flot.min.js", "context:js/overview.js" })
public class Overview extends Page {

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

	private static String contextPath;

	@Inject
	private Request request;

	public Object onActivate() {
		if (contextPath == null) {
			contextPath = request.getContextPath();
			if (contextPath.startsWith("/") || contextPath.startsWith("\\")) {
				contextPath = contextPath.substring(1);
			}
		}
		return super.onActivate();
	}

	public Block onRuntimesUpdated(final Collection<RuntimeInfo> message) {
		runtimes = message;

		ajaxResponseRenderer.addCallback(new JavaScriptCallback() {
			public void run(JavaScriptSupport jss) {
				for (RuntimeInfo ri : runtimes) {
					long netBytes = 0;
					long fsBytes = 0;
					for (NetworkSample iface : ri.getMonitorSample().getInterfaces()) {
						netBytes += iface.getBytesOut() + iface.getBytesIn();
					}
					for (FilesystemSample fs : ri.getMonitorSample().getFilesystems()) {
						fsBytes += fs.getWriteBytes() + fs.getReadBytes();
					}
					jss.addScript(
							"addPlotPoint('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
							ri.getId(),
							new Date().getTime(),
							ri.getMonitorSample().getCpuUsage() * 100,
							ri.getMonitorSample().getFreeMemory(),
							ri.getMonitorSample().getLoadAverage().getLoad1(),
							netBytes,
							fsBytes);
				}
			}
		});

		return runtimesBlock;
	}

	public Block onTasksUpdated(final Collection<TaskEntry> message) {
		ArrayList<TaskEntry> taskEntries = new ArrayList<>(message);
		Collections.sort(taskEntries, new Comparator<TaskEntry>() {
			@Override
			public int compare(TaskEntry o1, TaskEntry o2) {
				int order = o1.getTaskContextId().compareTo(o2.getTaskContextId());
				if (order == 0)
					order = o1.getId().compareTo(o2.getId());
				return order;
			}
		});

		Map<String, ArrayList<TaskEntry>> tasksByContexts = new LinkedHashMap<>();

		for (TaskEntry taskEntry : taskEntries) {
			if (taskEntry.getState() == TaskState.FINISHED) {
				continue;
			}

			String contextId = taskEntry.getTaskContextId();
			if (!tasksByContexts.containsKey(contextId))
				tasksByContexts.put(contextId, new ArrayList<TaskEntry>());
			tasksByContexts.get(contextId).add(taskEntry);
		}

		this.contexts = new ArrayList<>(tasksByContexts.values());

		return tasksBlock;
	}

	public void onLogsUpdated(final String log) {
		ajaxResponseRenderer.addCallback(new JavaScriptCallback() {
			public void run(JavaScriptSupport jss) {
				jss.addScript("addLog(%s)", log);
			}
		});
	}

	public boolean getAreThereAnyTasks() {
		return contexts.size() > 0;
	}

	public boolean isFirstInContext() {
		return taskIndex == 0;
	}

	public boolean isRuntimeFullExclusive() {
		return TaskExclusivity.valueOf(runtime.getExclusivity()) == TaskExclusivity.EXCLUSIVE;
	}

	public boolean isRuntimeContextExclusive() {
		return TaskExclusivity.valueOf(runtime.getExclusivity()) == TaskExclusivity.CONTEXT_EXCLUSIVE;
	}

	public String getContextDetailLink(String contextId) {
		return contextPath + "context/detail/" + contextId;
	}

	public String getTaskDetailLink(String taskId) {
		return contextPath + "/task/detail/" + taskId;
	}

	public String getBenchmarkDetailLink(String benchmarkId) {
		return contextPath + "/benchmark/detail/" + benchmarkId;
	}

	public String getRuntimeDetailLink(String runtimeId) {
		return contextPath + "/runtime/detail/" + runtimeId;
	}
}
