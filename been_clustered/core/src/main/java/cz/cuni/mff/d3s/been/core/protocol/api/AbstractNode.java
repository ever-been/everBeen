package cz.cuni.mff.d3s.been.core.protocol.api;

import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.JSONSerializer.JSONSerializerException;
import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.NodeRegisteredMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.NodeTerminatedMessage;

public abstract class AbstractNode {

	private final Messaging messaging;

	private final DataPersistence dataPersistence;

	private final String nodeId;

	public AbstractNode(final Messaging messaging, final DataPersistence dataPersistence, final String nodeId) {
		this.nodeId = nodeId;
		this.messaging = messaging;
		this.dataPersistence = dataPersistence;
	}

	public void sendMessage(final BaseMessage message) throws JSONSerializerException {
		messaging.send(Context.GLOBAL_TOPIC, message);
	}

	public void sendNodeRegisteredMessage(final NodeRegisteredMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	public void sendNodeTerminatedMessage(final NodeTerminatedMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	protected final DataPersistence getDataPersistence() {
		return dataPersistence;
	}

	protected final Messaging getMessaging() {
		return messaging;
	}

	public String getNodeId() {
		return nodeId;
	}

	public abstract void start();

}
