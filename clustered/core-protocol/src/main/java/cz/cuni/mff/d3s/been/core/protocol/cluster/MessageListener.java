package cz.cuni.mff.d3s.been.core.protocol.cluster;

import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;


public interface MessageListener {
	public void onMessage(BaseMessage message);
}
