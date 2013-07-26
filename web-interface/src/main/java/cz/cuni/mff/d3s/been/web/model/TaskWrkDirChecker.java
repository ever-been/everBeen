package cz.cuni.mff.d3s.been.web.model;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: donarus
 */
public final class TaskWrkDirChecker {

    private final BeenApi api;

    // cached (for this request only) list of all tasks in cluster
    private java.util.List<TaskEntry> allTasks;

    public TaskWrkDirChecker(BeenApi api) {
        this.api = api;

    }

    public java.util.List<String> getOldTaskDirsOnRuntime(RuntimeInfo runtime) {
        java.util.List<String> taskDirsOnRuntime = getTaskDirsOnHost(runtime);

        for (TaskEntry entry : getLiveTasksOnHost(runtime)) {
            taskDirsOnRuntime.remove(entry.getWorkingDirectory());
        }

        return taskDirsOnRuntime;
    }

    private java.util.List<String> getTaskDirsOnHost(RuntimeInfo runtime) {
        return new ArrayList<>(runtime.getTaskDirs());
    }

    private Collection<TaskEntry> liveTasksOnHosts;

    private Collection<TaskEntry> getLiveTasksOnHost(RuntimeInfo runtime) {
        if (liveTasksOnHosts == null) {
            liveTasksOnHosts = api.listActiveTasks(runtime.getId());
        }
        return liveTasksOnHosts;
    }
}
