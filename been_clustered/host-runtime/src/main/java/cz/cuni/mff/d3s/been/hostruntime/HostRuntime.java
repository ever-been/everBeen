package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.JSONSerializer.JSONSerializerException;
import cz.cuni.mff.d3s.been.core.protocol.api.AbstractNode;
import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskFinishedMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskKilledMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskStartedMessage;
import cz.cuni.mff.d3s.been.core.protocol.pojo.BaseNodeInfo;
import cz.cuni.mff.d3s.been.core.protocol.pojo.BaseNodeInfo.HostRuntimeNodeInfo;

public class HostRuntime extends AbstractNode {

	private final TaskRunner taskRunner;

	private final BaseNodeInfo nodeInfo;

	public HostRuntime(Messaging messaging, DataPersistence dataPersistence, TaskRunner taskRunner, String nodeId) {
		super(messaging, dataPersistence, nodeId);
		this.taskRunner = taskRunner;
		this.nodeInfo = new HostRuntimeNodeInfo(nodeId);
	}

	@Override
	public void start() {
		registerListeners();
		storeNodeInfo();
	}

	private void storeNodeInfo() {
		getDataPersistence().<BaseNodeInfo> getList(Context.NODE_INFO_LIST).add(nodeInfo);
	}

	private void registerListeners() {
		getMessaging().addMessageListener(Context.GLOBAL_TOPIC, new HostRuntimeMessageListener(this));
	}

	void sendTaskStartedMessage(TaskStartedMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	void sendTaskFinishedMessage(TaskFinishedMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	void sendTaskKilledMessage(TaskKilledMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	void onRunTask(RunTaskMessage message) {
		synchronized (taskRunner) {
			taskRunner.tryRunTask(message);
		}
	}

	void onKillTask(KillTaskMessage message) {
		synchronized (taskRunner) {
			taskRunner.killTask(message);
		}
	}

}
