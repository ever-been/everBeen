package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * Configuration parameters for the Host Runtime
 *
 * @author darklight
 */
public final class HostRuntimeConfiguration extends BeenServiceConfiguration {
	private HostRuntimeConfiguration() {}

	/** Name of the Host Runtime working directory property */
	public static final String WRKDIR_NAME = "hostruntime.wrkdir.name";
	/** Default name of the Host Runtime working directory */
	public static final String DEFAULT_WRKDIR_NAME = ".HostRuntime";

	/** Name of the property which determines the maximum number of working directories a Host Runtime will keep in history. When this number is exceeded at the boot of a Host Runtime service, the oldest existing directory is deleted. */
	public static final String TASKS_WRKDIR_MAX_HISTORY = "hostruntime.tasks.wrkdir.maxHistory";
	/** Default maximum number of working directories a Host Runtime can keep in history */
	public static final Integer DEFAULT_TASKS_WRKDIR_MAX_HISTORY = 4;

}
