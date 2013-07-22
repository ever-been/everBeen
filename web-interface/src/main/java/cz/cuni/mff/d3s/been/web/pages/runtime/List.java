package cz.cuni.mff.d3s.been.web.pages.runtime;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

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

    // cached (for this request only) list of all tasks in cluster
    private java.util.List<TaskEntry> allTasks;

    private Map<RuntimeInfo, java.util.List<String>> oldWrkDirs = new HashMap<>();

    public Collection<RuntimeInfo> getRuntimes() {
        return this.api.getApi().getRuntimes();
    }

    public String getRowClass(RuntimeInfo runtime) {
        if (getOldTaskDirsOnRuntime(runtime).isEmpty()) {
            return "";
        } else {
            return "error";
        }
    }

    private java.util.List<String> getOldTaskDirsOnRuntime(RuntimeInfo runtime) {
        java.util.List<String> taskDirsOnRuntime = oldWrkDirs.get(runtime);

        if (taskDirsOnRuntime == null) {
            java.util.List<TaskEntry> tasksOnRuntime = getUnfinishedTasksOnHost(runtime);
            taskDirsOnRuntime = getTaskDirsOnHost(runtime);

            for (TaskEntry entry : tasksOnRuntime) {
                taskDirsOnRuntime.remove(entry.getWorkingDirectory());
            }
        }
        return taskDirsOnRuntime;
    }

    private java.util.List<String> getTaskDirsOnHost(RuntimeInfo runtime) {
        return new ArrayList<>(runtime.getTaskDirs());
    }

    private java.util.List<TaskEntry> getUnfinishedTasksOnHost(RuntimeInfo runtime) {
        java.util.List<TaskEntry> allTasks = getAllTasks();
        java.util.List<TaskEntry> tasksOnHost = new ArrayList<>();
        for (TaskEntry entry : allTasks) {
            if (entry.getOwnerId().equals(runtime.getId()) && entry.getState() != ABORTED && entry.getState() != FINISHED) {
                tasksOnHost.add(entry);
            }
        }
        return tasksOnHost;
    }


    private java.util.List<TaskEntry> getAllTasks() {
        if (allTasks == null) {
            allTasks = new ArrayList<>(this.api.getApi().getTasks());
        }
        return allTasks;
    }

    public String shorten(String what, int beginChars, int endChars) {
        if (beginChars < 0 || endChars < 0) {
            throw new IllegalArgumentException("Negative offset for shortening string.");
        }

        if (what == null) {
            return null;
        }

        if (what.length() <= beginChars + endChars + 6) {
            // why number 6? Because four dots + two spaces = 6 characters
            return what;
        }

        return what.substring(0, beginChars) + " .... " + what.substring(what.length() - endChars, what.length());
    }

    public String getInfo(RuntimeInfo runtime) {
        if (getOldTaskDirsOnRuntime(runtime).isEmpty()) {
            return "";
        }
        return "Undeleted working directories of unfinished (failed/killed) tasks still exists in host runtime working directory.";
    }

    public String getMemoryInMB(RuntimeInfo runtime) {
        NumberFormat formatter = NumberFormat.getIntegerInstance();
        return formatter.format(runtime.getMonitorSample().getFreeMemory() / 1024 / 1024);
    }

    public String getStartTime(RuntimeInfo runtime) {
        GregorianCalendar c = runtime.getStartTime().toGregorianCalendar();
        Date startTime = c.getTime();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(startTime);
    }

}
