package cz.cuni.mff.d3s.been.manager;

import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.manager.msg.TaskMessage;
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

	protected final MessageQueues mqs = MessageQueues.getInstance();

	/**
	 * Returns a new sender to the Task Manager's queue.
	 * 
	 * @return Sender bind to the Task Manager's action queue
	 * @throws ServiceException
	 */
	final IMessageSender<TaskMessage> createSender() throws ServiceException {
		try {
			return mqs.createSender(TaskManagerNames.ACTION_QUEUE_NAME);
		} catch (MessagingException e) {
			String msg = String.format("Cannot create sender to %s", TaskManagerNames.ACTION_QUEUE_NAME);
			throw new ServiceException(msg, e);
		}
	}
}
