package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * @author darklight
 */
public class SotwareRepositoryConfiguration extends BeenServiceConfiguration {
	private SotwareRepositoryConfiguration(){}

	/** Property name for Software Repository listening port */
	public static final String PORT = "swrepository.port";
	/** Default Software Repository listening port is 8000 */
	public static final int DEFAULT_PORT = 8000;
}
