package cz.cuni.mff.d3s.been.objectrepository;

/**
 * Configuration for the generic persistence layer
 *
 * @author darklight
 */
public final class ObjectRepositoryConfiguration {
	private ObjectRepositoryConfiguration() {}

	/** Property that carries a rate of persistence layer failures that triggers this objectrepository's suspension */
	public static final String FAIL_RATE_BEFORE_SUSPEND = "been.objectrepository.fail-rate-before-suspend";
	/** By default, a {@value #DEFAULT_FAIL_RATE_BEFORE_SUSPEND} rate of failures will suspend the objectrepository temporarily */
	public static final Float DEFAULT_FAIL_RATE_BEFORE_SUSPEND = .1f;

	/** Property that determines how long does the objectrepository suspend when persistence fail rate is too high (in seconds). */
	public static final String SUSPENSION_TIME = "been.objectrepository.suspend-time";
	/** By default, BEEN objectrepository will suspend its activity for {@value #DEFAULT_SUSPENSION_TIME} seconds if the persistence layer fail rate is too high. */
	public static final Long DEFAULT_SUSPENSION_TIME = 60l;
}
