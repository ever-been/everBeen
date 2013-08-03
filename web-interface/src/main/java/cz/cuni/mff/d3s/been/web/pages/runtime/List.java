package cz.cuni.mff.d3s.been.web.pages.runtime;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.model.TaskWrkDirChecker;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static cz.cuni.mff.d3s.been.core.task.TaskState.ABORTED;
import static cz.cuni.mff.d3s.been.core.task.TaskState.FINISHED;

/**
 * @author Kuba Brecka, Tadeas Palusga
 */
@Page.Navigation(section = Layout.Section.RUNTIME_LIST)
public class List extends Page {

    @Property
    private RuntimeInfo runtime;

	@Inject
	private Request request;

	// cached (for this request only) list of all tasks in cluster
    private java.util.List<TaskEntry> allTasks;

    private Map<RuntimeInfo, java.util.List<String>> oldWrkDirs = new HashMap<>();

	public String getCurrentFilter() {
		String filter = request.getParameter("filter");
		if (filter == null) return "";
		return filter;
	}

    public Collection<RuntimeInfo> getRuntimes() throws BeenApiException {
	    String filter = request.getParameter("filter");
	    if (filter == null) {
            return this.api.getApi().getRuntimes();
	    } else {
		    return this.api.getApi().getRuntimes(filter);
	    }
    }

    public String getRowClass(RuntimeInfo runtime) throws BeenApiException {
        if (getOldTaskDirsOnRuntime
                (runtime).isEmpty()) {
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
        java.util.List<TaskEntry> allTasks = getAllTasks();
        java.util.List<TaskEntry> tasksOnHost = new ArrayList<>();
        for (TaskEntry entry : allTasks) {
            if (entry.getOwnerId().equals(runtime.getId()) && entry.getState() != ABORTED && entry.getState() != FINISHED) {
                tasksOnHost.add(entry);
            }
        }
        return tasksOnHost;
    }


    private java.util.List<TaskEntry> getAllTasks() throws BeenApiException {
        if (allTasks == null) {
            allTasks = new ArrayList<>(this.api.getApi().getTasks());
        }
        return allTasks;
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
