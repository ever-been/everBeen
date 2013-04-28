package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.ri.MonitorSample;

/**
 * @author Kuba Brecka
 */
public interface MonitoringListener {
	public void sampleGenerated(MonitorSample sample);
}
