package cz.cuni.mff.d3s.been.web.pages.runtime;

import static cz.cuni.mff.d3s.been.core.task.TaskState.ABORTED;
import static cz.cuni.mff.d3s.been.core.task.TaskState.FINISHED;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.model.TaskWrkDirChecker;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * @author Kuba Brecka, Tadeas Palusga
 */
@Page.Navigation(section = Layout.Section.RUNTIME_LIST)
public class List extends Page {

	@Property
	private RuntimeInfo runtime;

	@Property
	private boolean filterSyntaxError;

	@Inject
	private Request request;

	// cached (for this request only) list of all tasks in cluster
	private java.util.List<TaskEntry> allTasks;

	private Map<RuntimeInfo, java.util.List<String>> oldWrkDirs = new HashMap<>();

	public String getCurrentFilter() {
		String filter = request.getParameter("filter");
		if (filter == null)
			return "";
		return filter;
	}

	@Property
	Collection<RuntimeInfo> runtimes;

	public void onActivate() throws BeenApiException {
		String filter = request.getParameter("filter");
		if (filter == null || filter.isEmpty()) {
			runtimes = this.api.getApi().getRuntimes();
		} else {
			try {
				runtimes = this.api.getApi().getRuntimes(filter);
			} catch (BeenApiException e) {
				if (e.getCause().getMessage().startsWith("Invalid XPath")) {
					filterSyntaxError = true;
					runtimes = new ArrayList<>();
				} else {
					throw e;
				}
			}
		}
	}

	public String getRowClass(RuntimeInfo runtime) throws BeenApiException {
		if (getOldTaskDirsOnRuntime(runtime).isEmpty()) {
			return "";
		} else {
			return "error";
		}
	}

	private TaskWrkDirChecker taskWrkDirChecker = null;
	private java.util.List<String> getOldTaskDirsOnRuntime(RuntimeInfo runtime) throws BeenApiException {
		if (taskWrkDirChecker == null) {
			taskWrkDirChecker = new TaskWrkDirChecker(api.getApi());
		}
		return taskWrkDirChecker.getOldTaskDirsOnRuntime(runtime);
	}

	private java.util.List<String> getTaskDirsOnHost(RuntimeInfo runtime) {
		return new ArrayList<>(runtime.getTaskDirs());
	}

	private java.util.List<TaskEntry> getUnfinishedTasksOnHost(RuntimeInfo runtime) throws BeenApiException {
		final String runtimeId = runtime.getId();
		java.util.List<TaskEntry> tasks = new ArrayList<>(this.api.getApi().getTasksOnRuntime(runtimeId));

		java.util.List<TaskEntry> unfinishedTasks = new ArrayList<>();
		for (TaskEntry entry : tasks) {
			if (!entry.isSetRuntimeId() || !entry.getRuntimeId().equals(runtimeId)) {
				continue;
			}

			if (entry.getState() != ABORTED && entry.getState() != FINISHED) {
				unfinishedTasks.add(entry);
			}
		}
		return unfinishedTasks;
	}

	public String getErrors(RuntimeInfo runtime) throws BeenApiException {
		if (getOldTaskDirsOnRuntime(runtime).isEmpty()) {
			return "";
		}
		return "Undeleted working directories of unfinished (failed/killed) tasks still exists in host runtime working directory.";
	}

	public String getStartUpTime(RuntimeInfo runtime) {
		GregorianCalendar c = runtime.getStartUpTime().toGregorianCalendar();
		Date startTime = c.getTime();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(startTime);
	}

}
