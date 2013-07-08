package cz.cuni.mff.d3s.been.core;

/**
 * 
 * Types of messages a task can send to Host Runtimes
 * 
 * @author Martin Sixta
 *
 * @deprecated The PREFIX#message notation should be abolished altogether, since different sockets are used for different message types. The only current use of this enum is {@link #TASK_RUNNING}, which is just a special checkpoint.
 */
@Deprecated
public enum TaskMessageType {
	/**
	 * Log message. Tasks wants to log a message.
	 */
	LOG_MESSAGE,

	/**
	 * Signal that the task is running (used in case of initially suspended tasks)
	 */
	TASK_RUNNING,

	/**
	 * Unknown message type.
	 */

	UNKNOWN;
}
