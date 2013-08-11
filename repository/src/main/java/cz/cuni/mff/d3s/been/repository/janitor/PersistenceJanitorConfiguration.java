package cz.cuni.mff.d3s.been.repository.janitor;

/**
 * @author darklight
 */
public class PersistenceJanitorConfiguration {

	/** Number of hours that objects with a 'failed' status stay persistent */
	public static final String FAILED_LONGEVITY = "been.repository.janitor.failed-longevity";
	/** By default, failed objects stay persistent for {@value #DEFAULT_FAILED_LONGEVITY} hours */
	public static final Long DEFAULT_FAILED_LONGEVITY = 48l;

}
