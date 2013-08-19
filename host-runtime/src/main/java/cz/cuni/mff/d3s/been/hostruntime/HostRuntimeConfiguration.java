package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * Configuration parameters for the Host Runtime
 * 
 * @author darklight
 */
public final class HostRuntimeConfiguration implements BeenServiceConfiguration {

	/** Name of the Host Runtime working directory property */
	public static final String WRKDIR_NAME = "hostruntime.wrkdir.name";
	/** Default name of the Host Runtime working directory */
	public static final String DEFAULT_WRKDIR_NAME = ".HostRuntime";

	/**
	 * Name of the property which determines the maximum number of working
	 * directories a Host Runtime will keep in history. When this number is
	 * exceeded at the boot of a Host Runtime service, the oldest existing
	 * directory is deleted.
	 */
	public static final String TASKS_WRKDIR_MAX_HISTORY = "hostruntime.tasks.wrkdir.maxHistory";
	/**
	 * Default maximum number of working directories a Host Runtime can keep in
	 * history
	 */
	public static final Integer DEFAULT_TASKS_WRKDIR_MAX_HISTORY = 4;

	/**
	 * Name of the property which controls maximum number of tasks per Host
	 * Runtime
	 */
	public static final String MAX_TASKS = "hostruntime.tasks.max";

	/** Default value of {@link HostRuntimeConfiguration#MAX_TASKS} */
	public static final int DEFAULT_MAX_TASKS = 15;

	/**
	 * Name of the property which controls Host Runtime memory threshold in
	 * percents. If the threshold is reached no other task will be run on the Host
	 * Runtime.
	 * 
	 * The value must be between 20 - 100.
	 * 
	 * The threshold is compared to the value of '(free memory/available
	 * memory)*100'.
	 */
	public static final String MEMORY_THRESHOLD = "hostruntime.tasks.memory.threshold";

	/** Default value of {@link HostRuntimeConfiguration#MEMORY_THRESHOLD} */
	public static final int DEFAULT_MEMORY_THRESHOLD = 90;

}
