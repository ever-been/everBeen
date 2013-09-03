package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.ri.MonitorSample;

/**
 * 
 * Interface for listening on Monitoring events
 * 
 * @author Kuba Brecka
 */
public interface MonitoringListener {

	/**
	 * Called on every monitoring sample event.
	 * 
	 * @param sample
	 *          the generated sample
	 */
	public void sampleGenerated(MonitorSample sample);

	/**
	 * Closes the listener when monitoring shuts down
	 */
	public void close();
}
