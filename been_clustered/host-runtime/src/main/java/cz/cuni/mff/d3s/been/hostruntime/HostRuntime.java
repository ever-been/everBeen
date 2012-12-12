package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.JSONSerializer.JSONSerializerException;
import cz.cuni.mff.d3s.been.core.protocol.api.AbstractNode;
import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;
import cz.cuni.mff.d3s.been.core.protocol.cluster.MessageListener;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskFinishedMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskKilledMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskStartedMessage;
import cz.cuni.mff.d3s.been.core.protocol.pojo.BaseNodeInfo;
import cz.cuni.mff.d3s.been.core.protocol.pojo.BaseNodeInfo.HostRuntimeNodeInfo;

public class HostRuntime extends AbstractNode {

	private TaskRunner taskRunner;

	private BaseNodeInfo nodeInfo;

	private String nodeId;

	public HostRuntime(Messaging messaging, DataPersistence dataPersistence, TaskRunner taskRunner, String nodeId) {
		super(messaging, dataPersistence);
		this.taskRunner = taskRunner;
		this.nodeId = nodeId;
	}

	@Override
	public void start() {
		registerListeners(nodeId);
		nodeInfo = new HostRuntimeNodeInfo(nodeId);
		storeNodeInfo();
	}

	private void storeNodeInfo() {
		getDataPersistence().<BaseNodeInfo> getList(Context.NODE_INFO_LIST).add(nodeInfo);
	}

	private void registerListeners(final String nodeId) {
		getMessaging().addMessageListener(Context.GLOBAL_TOPIC, new MessageListener() {
			@Override
			public void onMessage(BaseMessage message) {
				String recieverId = message.recieverId;
				if (recieverId == null || nodeId.equals(recieverId)) {
					if (message instanceof RunTaskMessage) {
						onRunTask((RunTaskMessage) message);
					} else if (message instanceof KillTaskMessage) {
						onKillTask((KillTaskMessage) message);
					}
				}
			}
		});
	}

	protected void sendTaskStartedMessage(TaskStartedMessage message)
			throws JSONSerializerException {
		sendMessage(message);
	}

	protected void sendTaskFinishedMessage(TaskFinishedMessage message)
			throws JSONSerializerException {
		sendMessage(message);
	}

	protected void sendTaskKilledMessage(TaskKilledMessage message)
			throws JSONSerializerException {
		sendMessage(message);
	}

	protected void onRunTask(RunTaskMessage message) {
		synchronized (taskRunner) {
			taskRunner.tryRunTask(message);
		}
	}

	protected void onKillTask(KillTaskMessage message) {
		synchronized (taskRunner) {
			taskRunner.killTask(message);
		}
	}

}
