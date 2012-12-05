package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.protocol.Contexts;
import cz.cuni.mff.d3s.been.core.protocol.JSONSerializer.JSONSerializerException;
import cz.cuni.mff.d3s.been.core.protocol.api.BaseApi;
import cz.cuni.mff.d3s.been.core.protocol.cluster.MessageListener;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillAllTasksMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskFinishedMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskKilledMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskStartedMessage;

public class HostRuntime extends BaseApi {

	private TaskRunner taskRunner;

	public HostRuntime(Messaging messaging, TaskRunner taskRunner, String nodeId) {
		super(messaging);
		this.taskRunner = taskRunner;
		registerListeners(nodeId);
	}

	private void registerListeners(String nodeId) {
		String context = Contexts.nodeContext(nodeId);
		messaging.addMessageListener(context, new MessageListener() {
			@Override
			public void onMessage(BaseMessage message) {
				if (message instanceof RunTaskMessage) {
					onRunTask((RunTaskMessage) message);
				} else if (message instanceof KillTaskMessage) {
					onKillTask((KillTaskMessage) message);
				} else if (message instanceof KillAllTasksMessage) {
					onKillAllTasks((KillAllTasksMessage) message);
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

	protected void onKillAllTasks(KillAllTasksMessage message) {

	}
}
