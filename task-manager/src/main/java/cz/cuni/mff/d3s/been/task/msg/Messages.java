package cz.cuni.mff.d3s.been.task.msg;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * @author Martin Sixta
 */
public class Messages {
	public static TaskMessage createRunContextMessage(String contextId) {
		return new RunContextMessage(contextId);
	}

	public static TaskMessage createNewTaskOwnerMessage(TaskEntry entry) {
		return new NewOwnerTaskMessage(entry);
	}

	public static TaskMessage createTaskChangedMessage(TaskEntry entry) {
		return new TaskChangedMessage(entry);
	}

	public static TaskMessage createNewTaskMessage(TaskEntry entry) {
		return new NewTaskMessage(entry);
	}

	public static TaskMessage createScheduleTaskMessage(TaskEntry entry) {
		return new ScheduleTaskMessage(entry);
	}

	public static TaskMessage createCheckSchedulabilityMessage(TaskEntry entry) {
		return new CheckSchedulabilityMessage(entry);
	}
}
