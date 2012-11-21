package cz.cuni.mff.been.hostruntime;


import cz.cuni.mff.d3s.been.cluster.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Martin Sixta
 */
class HostRuntimeMember implements MessageListener {

	private static final Logger logger = LoggerFactory.getLogger(HostRuntimeMember.class);

	private Member member;

	HostRuntimeImplementation hostRuntimeImplementation;

	public HostRuntimeMember(HostRuntimeImplementation hostRuntimeImplementation) {
		this.hostRuntimeImplementation = hostRuntimeImplementation;
		member = Factory.createMember("hazelcast");
		member.connect();

		member.getMessaging().addMessageListener("BEEN_HOSTRUNTIME_TOPIC", this);
	}


	@Override
	public void onMessage(Message message) {
		logger.info("Message received: type=" + message.getType() + " text=" + message.getText());
	}
}
