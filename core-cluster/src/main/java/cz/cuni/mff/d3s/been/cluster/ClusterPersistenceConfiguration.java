package cz.cuni.mff.d3s.been.cluster;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * Configuration for persistence transport layer
 * 
 * @author darklight
 */
public class ClusterPersistenceConfiguration implements BeenServiceConfiguration {

	/** Property that contains the timeout for queries into persistence layer. */
	public static final String QUERY_TIMEOUT = "been.cluster.persistence.query-timeout";
	/**
	 * By default, the timeout before a query is evicted is
	 * {@code DEFAULT_QUERY_TIMEOUT}seconds
	 */
	public static final Long DEFAULT_QUERY_TIMEOUT = 10l;

	/**
	 * Property that contains the timeout for a query's processing time in the
	 * persistence layer. Processing time contains the trip the data has to make
	 * back to the requesting host.
	 */
	public static final String QUERY_PROCESSING_TIMEOUT = "been.cluster.persistence.query-processing-timeout";
	/**
	 * By default, the timeout before a query is deemed failed is
	 * {@code DEFAULT_QUERY_PROCESSING_TIMEOUT}seconds
	 */
	public static final Long DEFAULT_QUERY_PROCESSING_TIMEOUT = 5l;
}
