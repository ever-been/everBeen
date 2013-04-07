package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.TaskMessageType;

/**
 * 
 * Implementations of this listener are used in {@link TaskMessageDispatcher}
 * and are responsible for processing of received messages.
 * 
 * @author Tadeáš Palusga
 * 
 */
public interface MessageListener {
	/**
	 * This method should {@link TaskMessageType} for which the listener is
	 * designed for
	 * 
	 * @return message type of the listener
	 */
	TaskMessageType getMessageType();

	/**
	 * Implement process logic in body of this method.
	 * 
	 * @param message
	 */
	void processMessage(String message);
}