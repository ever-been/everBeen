package cz.cuni.mff.d3s.been.repository.janitor;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * Configuration for persistence layer janitor component
 * 
 * @author darklight
 */
public class PersistenceJanitorConfiguration implements BeenServiceConfiguration {

	/**
	 * Number of hours that objects with a <code>failed</code> status stay
	 * persistent
	 */
	public static final String FAILED_LONGEVITY = "been.repository.janitor.failed-longevity";
	/**
	 * By default, failed objects stay persistent for
	 * {@value #DEFAULT_FAILED_LONGEVITY} hours
	 */
	public static final Long DEFAULT_FAILED_LONGEVITY = 48l;

	/**
	 * Number of hours that meta-info objects with a 'finished' status will stay
	 * persistent
	 */
	public static final String FINISHED_LONGEVITY = "been.repository.janitor.finished-longevity";
	/**
	 * By default, meta-info of objects with a <code>finished</code> status will
	 * stay persistent for {@value #DEFAULT_FINISHED_LONGEVITY} hours
	 */
	public static final Long DEFAULT_FINISHED_LONGEVITY = 96l;

	/** Number of seconds between janitor cleanup checks */
	public static final String WAKEUP_INTERVAL = "been.repository.janitor.cleanup-interval";
	/**
	 * By default, the persistence janitor will wake up every
	 * {@value #DEFAULT_WAKEUP_INTERVAL} minutes
	 */
	public static final Long DEFAULT_WAKEUP_INTERVAL = 10l;

}
