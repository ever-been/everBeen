package cz.cuni.mff.d3s.been.hostruntime;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.ClusterUtils;
import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;

final class HostRuntimeMessageListener implements MessageListener<BaseMessage>, IClusterService {

	private static final Logger log = LoggerFactory.getLogger(HostRuntimeMessageListener.class);


	private HostRuntime hostRuntime;
	final ITopic<BaseMessage> globalTopic;


	public HostRuntimeMessageListener(final HostRuntime hostRuntime) {
		this.hostRuntime = hostRuntime;
		globalTopic = ClusterUtils.getTopic(Context.GLOBAL_TOPIC.getName());
	}

	public void start() {
		globalTopic.addMessageListener(this);
	}

	public void stop() {
		globalTopic.removeMessageListener(this);
	}


	@Override
	public void onMessage(Message<BaseMessage> message) {
		final BaseMessage messageObject = message.getMessageObject();

		String recieverId = messageObject.recieverId;
		if (recieverId == null || hostRuntime.getNodeId().equals(recieverId)) {
			if (messageObject instanceof RunTaskMessage) {
				RunTaskMessage runTaskMessage = (RunTaskMessage) messageObject;

				log.info("Runtime: Received task to run " + runTaskMessage.taskId);
				hostRuntime.onRunTask((RunTaskMessage) messageObject);
			} else if (messageObject instanceof KillTaskMessage) {
				hostRuntime.onKillTask((KillTaskMessage) messageObject);
			}
		}
	}
}