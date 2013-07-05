package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.ri.MonitorSample;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * @author Kuba Brecka
 */
public interface MonitoringListener {
	public void sampleGenerated(MonitorSample sample);
    public void close();
}
