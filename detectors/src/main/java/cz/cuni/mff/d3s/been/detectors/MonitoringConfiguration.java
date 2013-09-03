package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * Monitoring Configuration
 * 
 * @author Kuba Brecka
 */
public class MonitoringConfiguration implements BeenServiceConfiguration {

	/**
	 * Property for the interval of automatic system monitoring samples, in
	 * milliseconds.
	 */
	public static final String INTERVAL = "been.monitoring.interval";
	/**
	 * By default, the monitoring will performed every
	 * <code>DEFAULT_INTERVAL</code>milliseconds.
	 */
	public static final Integer DEFAULT_INTERVAL = 5000;

}
