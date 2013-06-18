package cz.cuni.mff.d3s.been.socketworks;

import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * @author darklight
 */
public interface QueueGuard {
	void listen();
	void terminate() throws MessagingException;
	Integer getPort();
}
