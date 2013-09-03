package cz.cuni.mff.d3s.been.cluster;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * BEEN cluster client configuration
 * 
 * @author darklight
 */
public class ClusterClientConfiguration implements BeenServiceConfiguration {

	/**
	 * Property for the timeout of the native client connection (in seconds).
	 * Hazelcast tends to disconnect/reconnect clients too often with default
	 * settings.
	 */
	public static final String TIMEOUT = "been.cluster.client.timeout";
	/**
	 * By default, a native Hazelcast client's session will remain active for
	 * {@code DEFAULT_TIMEOUT} seconds
	 */
	public static final Integer DEFAULT_TIMEOUT = 120;

	/**
	 * Property with a ';' separated list of Hazelcast members which will be
	 * contacted in attempt to join the BEEN Hazelcast cluster.
	 */
	public static final String MEMBERS = "been.cluster.client.members";
	/**
	 * By default, BEEN will attempt to join these members:
	 * {@code DEFAULT_MEMBERS}
	 */
	public static final String DEFAULT_MEMBERS = "localhost:5701";
}
