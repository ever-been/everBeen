package cz.cuni.mff.d3s.been.core;

/**
 *
 * Defines property names for running task. Host Runtime must define these
 * properties in order to be used in tasks.
 *
 * @author Martin Sixta
 */
public class TaskPropertyNames {
	/**
	 * System property with Task ID
	 */
	public static String TASK_ID = "been.task.id";

	/**
	 *
	 * System property with Host Runtime Communication Port (aka sink)
	 *
	 */
	public static String HR_COMM_PORT = "been.task.mq.sink.port";

}
