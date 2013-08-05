package cz.cuni.mff.d3s.been.cluster;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Configuration of the BEEN cluster (mainly network config)
 *
 * @author darklight
 */
public class ClusterConfiguration extends BeenServiceConfiguration {
	private ClusterConfiguration() {}

	/**
	 * Property for the group BEEN's underlying Hazelcast cluster will use to join.
	 * WARNING: THIS PROPERTY NEEDS TO MATCH FOR ALL MEMBERS, OTHERWISE THE CLUSTER WILL SHATTER.
	 */
	public static final String GROUP = "been.cluster.group";
	/** By default, BEEN will join in group '{@value #DEFAULT_GROUP}' */
	public static final String DEFAULT_GROUP = "dev";

	/**
	 * Property for the password which enables access to the group where BEEN's underlying Hazelcast cluster members will converge.
	 * WARNING: THIS PROPERTY NEEDS TO MATCH FOR ALL MEMBERS, OTHERWISE THE CLUSTER WILL SHATTER.
	 */
	public static final String PASSWORD = "been.cluster.password";
	/** The default group access password for the BEEN cluster is '{@value #DEFAULT_PASSWORD}' */
	public static final String DEFAULT_PASSWORD = "dev-pass";

	/** Property denoting whether the Hazelcast cluster should bind to local interfaces */
	public static final String SOCKET_BIND_ANY = "been.cluster.socket.bind.any";
	/** By default, the BEEN Hazelcast cluster will try to bind to local interfaces as well */
	public static final Boolean DEFAULT_SOCKET_BIND_ANY = TRUE;

	/** Property denoting whether IP version 4 should be preferred over IP version 6 */
	public static final String PREFER_IPV4 = "been.cluster.preferIPv4Stack";
	/** By default, IPv4 stack is preferred */
	public static final Boolean DEFAULT_PREFER_IPV4 = TRUE;

	/** Property name for the default BEEN cluster listening port */
	public static final String PORT = "been.cluster.port";
	/** Default BEEN cluster listening port is '{@value #DEFAULT_PORT}' */
	public static final Integer DEFAULT_PORT = 5701;

	/** Property specifying the list of interfaces Hazelcast should bind to, note that Hazelcast will bind to local interfaces as well unless been.cluster.socket.bind.any is set to false (wildcards should work) */
	public static final String INTERFACES = "been.cluster.interfaces";
	/** By default, the cluster won't try to bind any interfaces explicitly */
	public static final String DEFAULT_INTERFACES = "";

	/**
	 * Property denoting the join policy of underlying Hazelcast cluster. Possible variants are:
	 * <dl>
	 *     <dt>multicast</dt>	<dd>Attempt to join by multicast on the local subnet. See {@link #MULTICAST_GROUP} and {@link #MULTICAST_PORT} for more configuration</dd>
	 *     <dt>tcp</dt>			<dd>Attempt to connect to a specific list of interfaces by TCP. These may be outside the local subnet. See {@link #TCP_MEMBERS} for more configuration</dd>
	 * </dl>
	 */
	public static final String JOIN = "been.cluster.join";
	/** By default, BEEN will try to join Hazelcast instances by {@value #DEFAULT_JOIN} */
	public static final String DEFAULT_JOIN = "multicast";

	/** Property for multicast-mode joining port configuration */
	public static final String MULTICAST_PORT = "been.cluster.multicast.port";
	/** By default, BEEN will try to join underlying Hazelcast cluster on port '{@value #DEFAULT_MULTICAST_PORT}' */
	public static final Integer DEFAULT_MULTICAST_PORT = 54327;

	/** Property for multciast-mode joining group configuration */
	public static final String MULTICAST_GROUP = "been.cluster.multicast.group";
	/** By default, BEEN will attempt to join underlying Hazelcast cluster in group '{@value #DEFAULT_MULTICAST_GROUP}' */
	public static final String DEFAULT_MULTICAST_GROUP = "224.2.2.3";

	/** This property should contain a ';' separated list of TCP connection strings, which determines the list of hosts that BEEN's underlying Hazelcast cluster will attempt to contact when joining the cluster */
	public static final String TCP_MEMBERS = "been.cluster.tcp.members";
	/** By default, these members will be contacted: {@value #DEFAULT_TCP_MEMBERS} */
	public static final String DEFAULT_TCP_MEMBERS = "localhost:5701";

	/** Property saying whether logging should be enabled for underlying Hazelcast cluster. WARNING: Generates a lot of noise. */
	public static final String LOGGING = "been.cluster.logging";
	/** By default, Hazelcast logging is <code>OFF</code> */
	public static final Boolean DEFAULT_LOGGING = FALSE;

	/** Type of the Hazelcast join method. */
	static enum JOIN_TYPE {
		MULTICAST, TCP
	}
}
