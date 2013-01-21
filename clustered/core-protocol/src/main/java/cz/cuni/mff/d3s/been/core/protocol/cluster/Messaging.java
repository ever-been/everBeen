package cz.cuni.mff.d3s.been.core.protocol.cluster;

import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;


public interface Messaging {

	public void addMessageListener(Context source, MessageListener listener);

	public void removeMessageListener(Context source, MessageListener listener);

	public void send(Context ctx, BaseMessage message);

}
