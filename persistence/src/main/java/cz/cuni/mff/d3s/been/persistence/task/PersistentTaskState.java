package cz.cuni.mff.d3s.been.persistence.task;

import cz.cuni.mff.d3s.been.core.persistence.TaskEntity;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * A persistent carrie object for {@link TaskState}
 *
 * @author darklight
 */
public class PersistentTaskState extends TaskEntity {

    private TaskState taskState;

    /**
     * Get the state with which this task has finished
     *
     * @return The task state
     */
    public TaskState getTaskState() {
        return taskState;
    }

    /**
     * Set the state with which given task has finished
     *
     * @param taskState State to set
     */
    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    /**
     * Fluently set the state with which given task has finished
     *
     * @param taskState State to set
     *
     * @return This {@link PersistentTaskState}, with changed task state
     */
    public PersistentTaskState withTaskState(TaskState taskState) {
        setTaskState(taskState);
        return this;
    }
}
