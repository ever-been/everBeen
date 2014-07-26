package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * Configuration options of the Software Repository component
 *
 * @author darklight
 */
public final class SoftwareRepositoryConfiguration implements BeenServiceConfiguration {

	/**
	 * Property name for Software Repository listening interface.
	 * Property should contain comma-separated values in the form of <code>[HOST]:PORT</code>.
	 */
	public static final String INTERFACE = "swrepository.host";
	/** The literal denoting that all interfaces should be bound */
	public static final String VALUE_INTERFACE_ALL = "*";
	/** Default Software Repository listening is {@link #VALUE_INTERFACE_ALL} */
	public static final String DEFAULT_INTERFACE = VALUE_INTERFACE_ALL;

	/** Property name for Software Repository listening port */
	public static final String PORT = "swrepository.port";
	/** Default Software Repository listening port is 8000 */
	public static final int DEFAULT_PORT = 8000;

}
