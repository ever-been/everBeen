package cz.cuni.mff.d3s.been.manager.action;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * @author Kuba Brecka
 */
public class ResubmitActionConfiguration extends BeenServiceConfiguration {
	private ResubmitActionConfiguration() {}

	/**
	 * The maximum number of benchmark generator resubmits before aborting the
	 * whole benchmark.
	 */
	public static final String MAXIMUM_ALLOWED_RESUBMITS = "been.cluster.resubmit.maximum-allowed";
	/**
	 * The default maximum number of resubmits is '
	 * {@value #DEFAULT_MAXIMUM_ALLOWED_RESUBMITS}'.
	 */
	public static final Integer DEFAULT_MAXIMUM_ALLOWED_RESUBMITS = 10;
}
