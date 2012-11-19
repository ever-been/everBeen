package cz.cuni.mff.d3s.been.cluster.hazelcast;


import cz.cuni.mff.d3s.been.cluster.Message;
import cz.cuni.mff.d3s.been.cluster.MessageListener;


final class HCMessageListener implements com.hazelcast.core.MessageListener {

	private MessageListener listener;

	public HCMessageListener(MessageListener listener) {
		this.listener = listener;
	}


	@Override
	public void onMessage(com.hazelcast.core.Message message) {
		listener.onMessage((Message)message.getMessageObject());
	}
}
