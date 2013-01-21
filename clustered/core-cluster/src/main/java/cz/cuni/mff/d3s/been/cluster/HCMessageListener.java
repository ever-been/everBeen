package cz.cuni.mff.d3s.been.cluster;

import cz.cuni.mff.d3s.been.core.protocol.cluster.MessageListener;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;

final class HCMessageListener implements com.hazelcast.core.MessageListener<BaseMessage> {

	private MessageListener listener;

	public HCMessageListener(MessageListener listener) {
		this.listener = listener;
	}

	@Override
	public void onMessage(com.hazelcast.core.Message<BaseMessage> message) {
		listener.onMessage(message.getMessageObject());
	}

}
