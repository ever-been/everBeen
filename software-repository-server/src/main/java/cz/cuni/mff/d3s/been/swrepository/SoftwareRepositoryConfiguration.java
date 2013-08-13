package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * @author darklight
 */
public class SoftwareRepositoryConfiguration extends BeenServiceConfiguration {
	private SoftwareRepositoryConfiguration(){}

	/** Property name for Software Repository listening port */
	public static final String PORT = "swrepository.port";
	/** Default Software Repository listening port is 8000 */
	public static final int DEFAULT_PORT = 8000;


    public static final String SERVICE_DETECTION_PERIOD = "swrepository.serviceInfoDetectionPeriod";
    /** in seconds */
    public static final int DEFAULT_SERVICE_DETECTION_PERIOD = 30;


    public static final String SERVICE_TIMEOUT = "swrepository.serviceInfoTimeout";
    /** in seconds */
    public static final int DEFAULT_SERVICE_TIMEOUT = 45;

}
