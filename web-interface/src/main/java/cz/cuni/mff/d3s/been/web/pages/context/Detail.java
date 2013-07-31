package cz.cuni.mff.d3s.been.web.pages.context;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
public class Detail extends Page {

    @Property
    private TaskContextEntry context;

    @Property
    private List<String> taskIds;

    @Property
    private String taskId;

    @Property
    private Map<String, TaskEntry> tasksMap;

    @Property
    private TaskEntry task;

    @Property
    private cz.cuni.mff.d3s.been.core.task.Property property;

    @Property
    private String taskContextId;

    void onActivate(String taskContextId) throws BeenApiException {
        this.taskContextId = taskContextId;

        context = api.getApi().getTaskContext(taskContextId);

        if (context != null) {
            taskIds = context.getContainedTask();
            tasksMap = new HashMap<>();
            for (TaskEntry taskEntry : this.api.getApi().getTasksInTaskContext(taskContextId)) {
                if (taskEntry == null) continue;
                tasksMap.put(taskEntry.getId(), taskEntry);
            }
        }
    }

    Object onPassivate() {
        return taskContextId;
    }

    public TaskEntry taskEntryWithId(String taskId) {
        return tasksMap.get(taskId);
    }


}
