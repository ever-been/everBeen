package cz.cuni.mff.d3s.been.logging;

import cz.cuni.mff.d3s.been.core.persistence.TaskEntity;

/**
 * Log message logged by a task. Wraps {@link LogMessage} with additional info
 *
 * @author darklight
 */
public class TaskLogMessage extends TaskEntity {

    private LogMessage message;

    public TaskLogMessage() {
        created = System.currentTimeMillis();
    }

    /**
     * Get the actual message
     *
     * @return The actual message
     */
    public LogMessage getMessage() {
        return message;
    }

    /**
     * Set the acutal message
     *
     * @param message Actual message to set
     */
    public void setMessage(LogMessage message) {
        this.message = message;
    }

    /**
     * Fluently set the actual message
     *
     * @param message Actual message to set
     *
     * @return This {@link TaskLogMessage}, with changed message
     */
    public TaskLogMessage withMessage(LogMessage message) {
        setMessage(message);
        return this;
    }
}
