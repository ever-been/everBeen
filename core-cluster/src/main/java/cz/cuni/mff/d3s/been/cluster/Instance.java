package cz.cuni.mff.d3s.been.cluster;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * Singleton for the HazelcastInstance object.
 * <p/>
 * The class is responsible for joining the cluster with appropriate
 * {@link NodeType} and user-defined properties.
 * <p/>
 * 
 * 
 * 
 * @author Martin Sixta
 */

public final class Instance {

	/** Logging */
	private static final Logger log = LoggerFactory.getLogger(Instance.class);

	/** The singleton Hazelcast instance */
	private static HazelcastInstance hazelcastInstance = null;

	/** Type of the instance */
	private static NodeType nodeType = null;

	/**
	 * Timeout of the native client connection.
	 * 
	 * Hazelcast tends to disconnect/reconnect clients too often with default
	 * settings.
	 */
	public static final String PROPERTY_CLIENT_TIMEOUT = "been.cluster.client.timeout";
	public static final String PROPERTY_CLIENT_MEMBERS = "been.cluster.client.members";

	public static final String PROPERTY_CLUSTER_GROUP = "been.cluster.group";
	public static final String PROPERTY_CLUSTER_PASSWORD = "been.cluster.password";

	public static final String PROPERTY_CLUSTER_SOCKET_BIND_ANY = "been.cluster.socket.bind.any";

	public static final String PROPERTY_CLUSTER_PREFER_IPV4 = "been.cluster.preferIPv4Stack";
	public static final String PROPERTY_CLUSTER_PORT = "been.cluster.port";
	public static final String PROPERTY_CLUSTER_INTERFACES = "been.cluster.interfaces";
	public static final String PROPERTY_CLUSTER_MULTICAST_PORT = "been.cluster.multicast.port";
	public static final String PROPERTY_CLUSTER_MULTICAST_GROUP = "been.cluster.multicast.group";
	public static final String PROPERTY_CLUSTER_TCP_MEMBERS = "been.cluster.tcp.members";

	//public static final String PROPERTY_CLUSTER_TCP_INTERFACES = "been.cluster.tcp.interfaces";

	public static final String PROPERTY_CLUSTER_JOIN = "been.cluster.join";

	/**
	 * Path to the default Hazelcast configuration resource.
	 */

	/**
	 * Returns type of the node.
	 * 
	 * @return type of the node
	 * 
	 * @throws IllegalStateException
	 *           when node is not connected
	 */
	public static NodeType getNodeType() throws IllegalStateException {
		if (!isConnected()) {
			throw new IllegalStateException("The node is not connected!");
		}

		return nodeType;
	}

	/**
	 * Returns BEEN's Hazelcast instance
	 * 
	 * @return BEEN's Hazelcast instance
	 * @throws IllegalStateException
	 *           when node is not connected
	 */
	public static HazelcastInstance getInstance() throws IllegalStateException {
		if (!isConnected()) {
			throw new IllegalStateException("The node is not connected!");
		}

		return hazelcastInstance;
	}

	/**
	 * 
	 * Creates new HazelcastInstance according to supplied type.
	 * 
	 * The function can be called only once! If you need the hazelcastInstance
	 * call getInstance().
	 * 
	 * @param type
	 *          type of the node to be initialized
	 * @param userProperties
	 *          configuration, can be null
	 */
	public static synchronized
			void
			init(NodeType type, Properties userProperties) throws IllegalStateException, ServiceException {
		if (isConnected()) {
			throw new IllegalStateException("The node is already connected!");
		}

		hazelcastInstance = join(type, userProperties);
		nodeType = type;

	}

	public static synchronized HazelcastInstance newNativeInstance(String host,
			int port, String groupName, String groupPassword) {
		if (isConnected()) {
			throw new RuntimeException("Already connected");
		}

		Properties userProperties = new Properties();

		userProperties.setProperty(
				PROPERTY_CLIENT_MEMBERS,
				String.format("%s:%d", host, port));
		userProperties.setProperty(PROPERTY_CLUSTER_GROUP, groupName);
		userProperties.setProperty(PROPERTY_CLUSTER_PASSWORD, groupPassword);

		try {
			Instance.init(NodeType.NATIVE, userProperties);

			return getInstance();
		} catch (ServiceException e) {
			throw new RuntimeException("Cannot initialize client connection");
		}
	}

	public synchronized ClusterContext createContext() throws ServiceException {
		if (!isConnected()) {
			throw new ServiceException("Not connected to the cluster!");
		}

		return new ClusterContext(getInstance());
	}

	/**
	 * Shut downs connection to the Hazelcast cluster.
	 * 
	 */
	public static synchronized void shutdown() {
		if (!isConnected()) {
			log.warn("Connection to the cluster already closed");
			return;
		}

		getInstance().getLifecycleService().shutdown();

		hazelcastInstance = null;
		nodeType = null;
	}

	private static HazelcastInstance createNativeInstance(
			Properties userProperties) throws ServiceException {

		ClientConfig clientConfig = InstanceConfigHelper.createClientConfig(userProperties);

		return HazelcastClient.newHazelcastClient(clientConfig);

	}

	private static
			HazelcastInstance
			newDataInstance(Properties userProperties) throws ServiceException {

		Config config = InstanceConfigHelper.createMemberConfig(userProperties);

		return Hazelcast.newHazelcastInstance(config);
	}

	private static
			HazelcastInstance
			join(NodeType type, Properties userProperties) throws ServiceException {
		switch (type) {
			case DATA:
				return newDataInstance(userProperties);
			case LITE:
				throw new UnsupportedOperationException("LITE node not implemented!");
			case NATIVE:
				return createNativeInstance(userProperties);
			default:
				throw new UnsupportedOperationException(nodeType.toString() + " unknown mode!");
		}
	}

	private static boolean isConnected() {
		return (nodeType != null && hazelcastInstance != null);
	}

}
