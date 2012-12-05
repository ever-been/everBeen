package cz.cuni.mff.d3s.been.core.protocol.api;

import cz.cuni.mff.d3s.been.core.protocol.Contexts;
import cz.cuni.mff.d3s.been.core.protocol.JSONSerializer.JSONSerializerException;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.NodeRegisteredMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.NodeTerminatedMessage;

public abstract class BaseApi {
	
	protected Messaging messaging;

	public BaseApi(Messaging messaging) {
		this.messaging = messaging;
	}

	protected final void sendMessage(BaseMessage message) throws JSONSerializerException {
		messaging.send(Contexts.GLOBAL_CONTEXT, message);
	} 
	
	protected final void sendNodeRegisteredMessage(NodeRegisteredMessage message) throws JSONSerializerException {
		sendMessage(message);
	}
	
	protected final void sendNodeTerminatedMessage(NodeTerminatedMessage message) throws JSONSerializerException {
		sendMessage(message);
	}
	
}
