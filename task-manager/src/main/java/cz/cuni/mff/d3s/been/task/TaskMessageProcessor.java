package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.task.action.TaskAction;
import cz.cuni.mff.d3s.been.task.action.TaskActions;
import cz.cuni.mff.d3s.been.task.message.TaskMessage;

/**
 * @author Martin Sixta
 */
final class TaskMessageProcessor extends Thread {

	private static Logger log = LoggerFactory.getLogger(TaskMessageProcessor.class);

	private final ClusterContext clusterCtx;
	private final IMessageReceiver<TaskMessage> receiver;

	public TaskMessageProcessor(ClusterContext clusterCtx, IMessageReceiver<TaskMessage> receiver) {
		this.clusterCtx = clusterCtx;
		this.receiver = receiver;

	}

	@Override
	public void run() {
		while (true) {
			try {
				TaskMessage message = receiver.receive();

				log.debug("Task Action of type '{}' received", message.getClass());

				TaskAction action = TaskActions.createAction(clusterCtx, message);

				action.execute();

			} catch (MessagingException e) {
				log.error("Cannot receive a message", e);
			} catch (TaskManagerException e) {
				log.error("Cannot execute action for received message", e);
			}

		}

	}
}
