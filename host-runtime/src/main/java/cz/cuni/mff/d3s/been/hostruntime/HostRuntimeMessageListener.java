package cz.cuni.mff.d3s.been.hostruntime;

import static cz.cuni.mff.d3s.been.hostruntime.HostRuntime.ACTION_QUEUE_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
import cz.cuni.mff.d3s.been.mq.MessagingException;

final class HostRuntimeMessageListener implements MessageListener<BaseMessage>, Service {

	/** Logging */
	private static final Logger log = LoggerFactory.getLogger(HostRuntimeMessageListener.class);

	/** Cluster Context */
	private final ClusterContext ctx;

	/** Sender for task action messages */
	private IMessageSender<BaseMessage> sender;

	/** ID of this Host Runtime */
	private final String nodeId;

	/** The Hazelcast topic to listen for messages on */
	final ITopic<BaseMessage> globalTopic;

	public HostRuntimeMessageListener(final ClusterContext ctx, final String nodeId) {
		this.ctx = ctx;
		this.nodeId = nodeId;

		globalTopic = ctx.getTopic(Context.GLOBAL_TOPIC.getName());
	}

	@Override
	public void start() throws ServiceException {

		try {
			this.sender = MessageQueues.getInstance().createSender(ACTION_QUEUE_NAME);
		} catch (MessagingException e) {
			String msg = String.format("Cannot connet to %s", ACTION_QUEUE_NAME);
			throw new ServiceException(msg, e);
		}
		globalTopic.addMessageListener(this);
	}

	@Override
	public synchronized void stop() {
		globalTopic.removeMessageListener(this);
		sender.close();
	}

	/**
	 * Picks a message from Hazelcast and sends it to its Host Runtime for
	 * processing.
	 * 
	 * @param message
	 */
	@Override
	public synchronized void onMessage(Message<BaseMessage> message) {
		final BaseMessage messageObject = message.getMessageObject();

		String receiverId = messageObject.recieverId;

		boolean isForThisHostRuntime = (receiverId == null || nodeId.equals(receiverId));

		if (isForThisHostRuntime) {
			try {
				sender.send(messageObject);
			} catch (MessagingException e) {
				log.error("Cannot request message to task action queue", e);
			}
		}
	}
}
