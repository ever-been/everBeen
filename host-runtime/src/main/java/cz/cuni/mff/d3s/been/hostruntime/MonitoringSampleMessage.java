package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.ri.MonitorSample;

/**
 * @author Kuba Brecka
 */
public class MonitoringSampleMessage extends BaseMessage {

	MonitorSample sample;

	public MonitorSample getSample() {
		return sample;
	}

	public MonitoringSampleMessage(MonitorSample sample) {
		super(null, null);
		this.sample = sample;
	}
}
