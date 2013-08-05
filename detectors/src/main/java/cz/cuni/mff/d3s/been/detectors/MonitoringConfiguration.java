package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * @author Kuba Brecka
 */
public class MonitoringConfiguration extends BeenServiceConfiguration {
	private MonitoringConfiguration() {}

	/** Property for the interval of automatic system monitoring samples, in milliseconds. */
	public static final String INTERVAL = "been.monitoring.interval";
	/** By default, the monitoring will performed every {@value #DEFAULT_INTERVAL} milliseconds. */
	public static final Integer DEFAULT_INTERVAL = 5000;

}
