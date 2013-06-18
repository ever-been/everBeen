package cz.cuni.mff.d3s.been.socketworks;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.oneway.ReadOnlyGuard;
import cz.cuni.mff.d3s.been.socketworks.oneway.ReadOnlyHandler;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyGuard;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandlerFactory;

/**
 * A message dispatcher intended as high-level abstraction over inter-process
 * socket works.
 * 
 * Created with IntelliJ IDEA. User: darklight Date: 5/6/13 Time: 12:26 PM
 */
public class MessageDispatcher implements Service {
	private static final Logger log = LoggerFactory.getLogger(MessageDispatcher.class);

	private final String hostname;
	private final Map<String, QueueGuard> guards;

	private MessageDispatcher(String hostname) {
		this.hostname = hostname;
		this.guards = new TreeMap<String, QueueGuard>();
	}

	public static MessageDispatcher create(String hostname) {
		return new MessageDispatcher(hostname);
	}

	public Integer getPortForQueue(String queueName) {
		return guards.get(queueName).getPort();
	}

	/**
	 * Create a new one-way named queue and associate it with a provided handler.
	 * 
	 * Use this method <b>BEFORE</b> {@link #start()}. If you reverse the order,
	 * the queue will be correctly created (and disposed of upon termination), but
	 * will remain inactive.
	 * 
	 * @param queueName
	 *          Name of the queue to create. Must be unique
	 * @param handler
	 *          Handler that specifies what should be done with incoming messages
	 * 
	 * @throws ServiceException
	 *           If sockets associated with any of the requested message queues
	 *           cannot be bound
	 */
	public void addRecieveHandler(String queueName, ReadOnlyHandler handler) throws ServiceException {
		try {
			guards.put(queueName, ReadOnlyGuard.create(hostname, queueName, handler));
		} catch (MessagingException e) {
			throw new ServiceException(String.format("Unable to initialize receiver for queue \"%s\".", queueName), e);
		}
	}

	/**
	 * Create a new two-way named queue and associated it with a provided handler
	 * factory. This means that messages arriving to this queue will be handled by
	 * handlers supplied by that factory.
	 * 
	 * Use this method <b>BEFORE</b> {@link #start()}. If you reverse the order,
	 * the queue will be correctly created (and disposed of upon termination), but
	 * will remain inactive.
	 * 
	 * @param queueName
	 *          Name of the created queue. Must be unique
	 * 
	 * @param handlerFactory
	 *          Factory that provides handlers servicing the requests (messages)
	 * 
	 * @throws ServiceException
	 *           If sockets associated with any of the requested message queues
	 *           cannot be found
	 */
	public void addRespondingHandler(String queueName, ReadReplyHandlerFactory handlerFactory) throws ServiceException {
		try {
			guards.put(queueName, ReadReplyGuard.create(hostname, queueName, handlerFactory));
		} catch (MessagingException e) {
			throw new ServiceException(String.format("Unable to initialize receiver for queue \"%s\"", queueName), e);
		}
	}

	@Override
	public void start() throws ServiceException {
		for (QueueGuard guard : guards.values()) {
			guard.listen();
		}
	}

	@Override
	public final void stop() {
		for (QueueGuard guard : guards.values()) {
			try {
				log.debug("Terminating {}", guard);
				guard.terminate();
				log.debug("{} terminated", guard);
			} catch (MessagingException e) {
				log.error("Failed to purge queue {}");
			}
		}
	}
}
