package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.protocol.cluster.MessageListener;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;

public class HostRuntimeMessageListener implements MessageListener {

	private HostRuntime hostRuntime;

	public HostRuntimeMessageListener(final HostRuntime hostRuntime) {
		this.hostRuntime = hostRuntime;
	}

	@Override
	public void onMessage(final BaseMessage message) {
		String recieverId = message.recieverId;
		if (recieverId == null || hostRuntime.getNodeId().equals(recieverId)) {
			if (message instanceof RunTaskMessage) {
				hostRuntime.onRunTask((RunTaskMessage) message);
			} else if (message instanceof KillTaskMessage) {
				hostRuntime.onKillTask((KillTaskMessage) message);
			}
		}
	}
}