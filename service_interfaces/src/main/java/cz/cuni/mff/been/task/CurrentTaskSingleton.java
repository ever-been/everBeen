package cz.cuni.mff.been.task;

/**
 * Singleton accessor for
 * 
 * @author darklight
 * 
 */
public abstract class CurrentTaskSingleton {
	static Task taskHandle;

	/**
	 * Get the task currently running in this runtime.
	 * 
	 * @return The current running task
	 */
	public static Task getTaskHandle() {
		return taskHandle;
	}

	/**
	 * Set the task currently running in this runtime.
	 * 
	 * @param taskHandle
	 *            The new running task
	 */
	static void setTaskHandle(Task taskHandle) {
		CurrentTaskSingleton.taskHandle = taskHandle;
	}
}
