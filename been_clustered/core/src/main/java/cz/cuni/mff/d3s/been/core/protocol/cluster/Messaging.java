package cz.cuni.mff.d3s.been.core.protocol.cluster;

import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;


public interface Messaging {

	public void addMessageListener(String source, MessageListener listener);

	public void removeMessageListener(String source, MessageListener listener);

	public void send(String context, BaseMessage message);

}
