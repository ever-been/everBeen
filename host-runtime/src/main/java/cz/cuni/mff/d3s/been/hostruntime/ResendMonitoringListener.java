package cz.cuni.mff.d3s.been.hostruntime;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.core.ri.MonitorSample;
import cz.cuni.mff.d3s.been.detectors.MonitoringListener;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * A {@link cz.cuni.mff.d3s.been.detectors.MonitoringListener} that resends
 * received data using an {@link cz.cuni.mff.d3s.been.mq.IMessageSender}
 * 
 * @author Radek Macha
 */
public class ResendMonitoringListener implements MonitoringListener {
	private final IMessageSender<Serializable> sender;

	private ResendMonitoringListener(IMessageSender<Serializable> sender) {
		this.sender = sender;
	}

	/**
	 * Crates new MonitoringListener.
	 * 
	 * @param sender
	 *          the sender used to resend monitoring events.
	 * @return new MonitoringListener
	 */
	public static MonitoringListener create(IMessageSender<Serializable> sender) {
		return new ResendMonitoringListener(sender);
	}

	@Override
	public void sampleGenerated(MonitorSample sample) {
		try {
			sender.send(new MonitoringSampleMessage(sample));
		} catch (MessagingException e) {
			throw new RuntimeException("Cannot request message.", e);
		}
	}

	@Override
	public void close() {
		sender.close();
	}
}
