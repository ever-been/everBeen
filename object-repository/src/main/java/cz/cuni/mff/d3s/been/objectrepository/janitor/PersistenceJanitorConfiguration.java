package cz.cuni.mff.d3s.been.objectrepository.janitor;

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
	public static final String FAILED_LONGEVITY = "been.objectrepository.janitor.failed-longevity";

	/**
	 * By default, failed objects stay persistent for {@value} hours
	 */
	public static final Long DEFAULT_FAILED_LONGEVITY = 96l;



	/**
	 * Number of hours that meta-info objects with a 'finished' status will stay
	 * persistent
	 */
	public static final String FINISHED_LONGEVITY = "been.objectrepository.janitor.finished-longevity";

	/**
	 * By default, meta-info of objects with a <code>finished</code> status will
	 * stay persistent for {@value} hours
	 */
	public static final Long DEFAULT_FINISHED_LONGEVITY = 168l;


	/**
	 * Number of hour service meta-info will stay persistent
	 */
	public static final String SERVICE_LOGS_LONGEVITY = "been.objectrepository.janitor.service-logs-longevity";

	/**
	 * By default, service meta-info will stay persistent for {@value} hours
	 */
	public static final Long DEFAULT_SERVICE_LOGS_LONGEVITY = 168l;


	/**
	 * Number of hours load monitor samples will stay persistent
	 */
	public static final String LOAD_SAMPLE_LONGEVITY = "been.objectrepository.janitor.load-sample-longevity";

	/**
	 * By default, load monitor samples will stay persistent for {@value}
	 */
	public static final Long DEFAULT_LOAD_SAMPLE_LONGEVITY = 168l;



	/** Number of minutes between janitor cleanup checks */
	public static final String WAKEUP_INTERVAL = "been.objectrepository.janitor.cleanup-interval";
	/**
	 * By default, the persistence janitor will wake up every {@value} minutes
	 */
	public static final Long DEFAULT_WAKEUP_INTERVAL = 10l;

}
