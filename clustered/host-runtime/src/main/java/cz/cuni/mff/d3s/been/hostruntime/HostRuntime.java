package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.RuntimeInfoUtils;
import cz.cuni.mff.d3s.been.core.RuntimesUtils;
import cz.cuni.mff.d3s.been.core.TopicUtils;
import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.JSONSerializer.JSONSerializerException;

import cz.cuni.mff.d3s.been.core.protocol.messages.*;
import cz.cuni.mff.d3s.been.task.TaskRunner;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;


final class HostRuntime implements IClusterService {

	private final TaskRunner taskRunner;
	private final RuntimeInfo hostRuntimeInfo;
	private HostRuntimeMessageListener messageListener;
	private RestBridgeListener restBridgeListener;


	public HostRuntime(TaskRunner taskRunner, String nodeId) {
		this.taskRunner = taskRunner;
		this.hostRuntimeInfo = RuntimeInfoUtils.newInfo(nodeId);
	}

	@Override
	public void start() {
		// We must register listeners first
		registerListeners();

		// Now, we can register the runtime without missing any messages
		registerHostRuntime(hostRuntimeInfo);
	}

	@Override
	public void stop() {
		unregisterListeners();

		//TODO: delete node info, when we figure out what the info should be
	}

	private void unregisterListeners() {
		restBridgeListener.stop();
		messageListener.stop();
	}


	private void registerListeners() {
		messageListener =  new HostRuntimeMessageListener(this);
		restBridgeListener = new RestBridgeListener();

		messageListener.start();

		// TODO: unlike messageListener, here is race condition. But do we care?
		restBridgeListener.start();
	}


	private void registerHostRuntime(RuntimeInfo runtimeInfo) {
		RuntimesUtils.setRuntimeInfo(runtimeInfo);
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

	public String getNodeId() {
		return hostRuntimeInfo.getId();
	}

	private void sendMessage(final BaseMessage message) {
		TopicUtils.publish(Context.GLOBAL_TOPIC.getName(), message);
	}

}
