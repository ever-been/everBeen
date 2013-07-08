package cz.cuni.mff.d3s.been.mq;

import org.jeromq.ZMQ.Context;
import org.jeromq.ZMQ.Poller;
import org.jeromq.ZMQ.Socket;

/**
 * This is a duplication of the {@link Context} class API. The duplication was
 * necessary for the creation of a non-static wrapper because the ZMQ API
 * doesn't provide any interface, just a static class.
 * 
 * All of these methods can additionally throw {@link MessagingException} in
 * case the instance of the non-static wrapper has been released previously
 * 
 * @author darklight
 * 
 */
public interface ZMQContext {
	/**
	 * @see Context#socket(int)
	 */
	Socket socket(int type) throws MessagingException;

	/**
	 * @see Context#poller()
	 */
	Poller poller() throws MessagingException;

	/**
	 * @see Context#poller(int)
	 */
	Poller poller(int size) throws MessagingException;

	/**
	 * @see Context#term()
	 */
	void term() throws MessagingException;
}
