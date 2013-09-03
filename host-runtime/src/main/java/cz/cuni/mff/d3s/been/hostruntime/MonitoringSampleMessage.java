package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.ri.MonitorSample;

/**
 * Message carrying monitoring sample.
 * 
 * @author Kuba Brecka
 */
public class MonitoringSampleMessage extends BaseMessage {

	private MonitorSample sample;

	/**
	 * Returns the sample
	 * 
	 * @return the sample
	 */
	public MonitorSample getSample() {
		return sample;
	}

	/**
	 * Creates new MonitoringSampleMessage
	 * 
	 * @param sample
	 *          the sample associated with the message
	 */
	public MonitoringSampleMessage(MonitorSample sample) {
		super(null);
		this.sample = sample;
	}
}
