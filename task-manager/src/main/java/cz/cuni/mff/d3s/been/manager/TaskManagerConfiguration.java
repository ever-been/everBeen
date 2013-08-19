package cz.cuni.mff.d3s.been.manager;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * Configuration options affection the Task Manager.
 * 
 * Override with care!
 * 
 * @author Martin Sixta
 */
final class TaskManagerConfiguration extends BeenServiceConfiguration {
	/**
	 * The period in seconds with which the {@link LocalKeyScanner} is scheduled.
	 * 
	 * Too short periods can affect performance.
	 */
	public static String SCANNER_PERIOD = "been.tm.scanner.period";

	/**
	 * The default value for the {@link TaskManagerConfiguration#SCANNER_PERIOD}
	 * in seconds
	 */
	public static int DEFAULT_SCANNER_PERIOD = 30;

	/**
	 * The initial delay in seconds before running the {@link LocalKeyScanner}
	 */
	public static String SCANNER_INITIAL_DELAY = "been.tm.scanner.delay";

	/**
	 * The default value for the
	 * {@link TaskManagerConfiguration#SCANNER_INITIAL_DELAY} in seconds.
	 */
	public static int DEFAULT_SCANNER_INITIAL_DELAY = DEFAULT_SCANNER_PERIOD / 2;

}
