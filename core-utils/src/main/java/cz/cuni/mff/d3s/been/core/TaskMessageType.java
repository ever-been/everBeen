package cz.cuni.mff.d3s.been.core;

/**
 * 
 * Types of messages a task can send to Host Runtimes
 * 
 * @author Martin Sixta
 */
public enum TaskMessageType {
	/**
	 * Log message. Tasks wants to log a message.
	 */
	LOG_MESSAGE,

	/**
	 * Unknown message type.
	 */

	UNKNOWN;

}
