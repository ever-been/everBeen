package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * 
 * Base class for Task Manager's service.
 * 
 * With easy access to all those things Task Manager's services need.
 * 
 * @author Martin Sixta
 */
abstract class TaskManagerService implements Service {

	private String MQ_NAME = TaskManagerNames.ACTION_QUEUE_NAME;

	protected final MessageQueues mqs = MessageQueues.getInstance();

	/**
	 * Returns a new sender to the Task Manager's queue.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	final IMessageSender<TaskMessage> createSender() throws ServiceException {
		try {
			return mqs.createSender(MQ_NAME);
		} catch (MessagingException e) {
			String msg = String.format("Cannot create sender to %s", MQ_NAME);
			throw new ServiceException(msg, e);
		}
	}
}
