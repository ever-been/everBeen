package cz.cuni.mff.d3s.been.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.manager.action.TaskAction;
import cz.cuni.mff.d3s.been.manager.action.TaskActionException;
import cz.cuni.mff.d3s.been.manager.msg.TaskMessage;
import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * @author Martin Sixta
 */
final class TaskMessageProcessor extends Thread {

	private static Logger log = LoggerFactory.getLogger(TaskMessageProcessor.class);

	private final ClusterContext clusterCtx;

	private final MessageQueues messageQueues = MessageQueues.getInstance();

	/**
	 * Creates TaskMessageProcessor.
	 * 
	 * @param clusterCtx
	 *          connection to the cluster
	 */
	public TaskMessageProcessor(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
	}

	@Override
	public void run() {
		final IMessageReceiver<TaskMessage> receiver;

		try {
			receiver = messageQueues.getReceiver(TaskManagerNames.ACTION_QUEUE_NAME);
		} catch (MessagingException e) {
			log.error("Cannot obtain reference to {} queue", TaskManagerNames.ACTION_QUEUE_NAME);
			return;
		}

		while (!Thread.interrupted()) {
			try {
				TaskMessage message = receiver.receive();

				if (message instanceof PoisonMessage) {
					log.info("Poison received, exiting");
					break;
				}

				log.debug("Task message of type '{}' received", message.getClass());

				TaskAction action = message.createAction(clusterCtx);

				if (action != null) {
					action.execute();
				}

			} catch (MessagingException e) {
				log.error("Cannot receive a message", e);
			} catch (TaskActionException e) {
				log.error("Cannot execute action for received message", e);
			} catch (Exception e) {
				log.error("Unknown error", e);
			}
		}

		log.info("Exiting thread of {}", TaskMessageProcessor.class.getSimpleName());
	}

	/**
	 *
	 *
	 */
	public void poison() {
		try {
			IMessageSender<TaskMessage> sender = messageQueues.createSender(TaskManagerNames.ACTION_QUEUE_NAME);
			sender.send(new PoisonMessage());
			sender.close();
			this.join();
		} catch (MessagingException | InterruptedException e) {
			log.error("Cannot stop Task Message Processor.", e);
		} finally {
			try {
				messageQueues.terminate(TaskManagerNames.ACTION_QUEUE_NAME);
			} catch (MessagingException e) {
				log.error("Error terminating message queue", e);
			}
		}
	}

	/**
	 * Message used to terminate task message processing.
	 */
	private static final class PoisonMessage implements TaskMessage {
		@Override
		public TaskAction createAction(ClusterContext ctx) {
			throw new UnsupportedOperationException("Poison message does not execute actions!");
		}
	}

}
