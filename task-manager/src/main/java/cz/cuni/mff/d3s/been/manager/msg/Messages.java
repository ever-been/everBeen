package cz.cuni.mff.d3s.been.manager.msg;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * Factory for {@link TaskMessage}s.
 * 
 * 
 * @author Martin Sixta
 */
public class Messages {
	/**
	 * Creates RunContextMessage implementation
	 * 
	 * @param contextId
	 *          targeted context id
	 * @return RunContextMessage implementation
	 */
	public static TaskMessage createRunContextMessage(String contextId) {
		return new RunContextMessage(contextId);
	}

	/**
	 * Creates TaskChangedMessage implementation
	 * 
	 * @param entry
	 *          targeted task entry
	 * @return TaskChangedMessage implementation
	 */
	public static TaskMessage createTaskChangedMessage(TaskEntry entry) {
		return new TaskChangedMessage(entry);
	}

	/**
	 * Creates NewTaskMessage implementation.
	 * 
	 * @param entry
	 *          targeted task entry
	 * @return NewTaskMessage implementation
	 */
	public static TaskMessage createNewTaskMessage(TaskEntry entry) {
		return new NewTaskMessage(entry);
	}

	/**
	 * Creates ScheduleTaskMessage implementation.
	 * 
	 * @param entry
	 *          targeted task entry
	 * @return ScheduleTaskMessage implementation
	 */
	public static TaskMessage createScheduleTaskMessage(TaskEntry entry) {
		return new ScheduleTaskMessage(entry);
	}

	/**
	 * Creates RescheduleTaskMessage implementation.
	 * 
	 * @param entry
	 *          targeted task entry
	 * @return RescheduleTaskMessage implementation
	 */
	public static TaskMessage createRescheduleTaskMessage(TaskEntry entry) {
		return new RescheduleTaskMessage(entry);
	}

	/**
	 * Creates CheckSchedulabilityMessage implementation.
	 * 
	 * @param entry
	 *          targeted task entry
	 * @return CheckSchedulabilityMessage implementation
	 */
	public static TaskMessage createCheckSchedulabilityMessage(TaskEntry entry) {
		return new CheckSchedulabilityMessage(entry);
	}

	/**
	 * Creates AbortTaskMessage implementation.
	 * 
	 * @param entry
	 *          targeted task entry
	 * @param reasonFormat
	 *          reason message format
	 * @param args
	 *          arguments for the format
	 * @return AbortTaskMessage implementation
	 */
	public static TaskMessage createAbortTaskMessage(TaskEntry entry, String reasonFormat, Object... args) {
		return new AbortTaskMessage(entry, reasonFormat, args);
	}
}
