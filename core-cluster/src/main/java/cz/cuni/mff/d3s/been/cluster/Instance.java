package cz.cuni.mff.d3s.been.cluster;

import static cz.cuni.mff.d3s.been.cluster.ClusterClientConfiguration.MEMBERS;
import static cz.cuni.mff.d3s.been.cluster.ClusterConfiguration.GROUP;
import static cz.cuni.mff.d3s.been.cluster.ClusterConfiguration.PASSWORD;

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

	/** Instance properties */
	private static Properties properties = null;

	/** Type of the instance */
	private static NodeType nodeType = null;

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
	 * 
	 * @throws IllegalStateException
	 *           when the node is already connected
	 * @throws ServiceException
	 *           when the node cannot connect
	 */
	public static synchronized
			void
			init(NodeType type, Properties userProperties) throws IllegalStateException, ServiceException {
		if (isConnected()) {
			throw new IllegalStateException("The node is already connected!");
		}
		properties = userProperties;
		hazelcastInstance = join(type, userProperties);
		nodeType = type;
	}

	/**
	 * Creates {@link ClusterContext} for this node.
	 * 
	 * @return Cluster context for this node
	 * 
	 * @throws IllegalStateException
	 *           when the node is not connected
	 */
	public static ClusterContext createContext() throws IllegalStateException {
		if (!isConnected()) {
			throw new IllegalStateException("The node is not connected yet");
		}
		return new ClusterContext(hazelcastInstance, properties);
	}

	/**
	 * Creates native HazelcastInstance instance.
	 * 
	 * @param host
	 *          host to connect to
	 * @param port
	 *          port of the host to connect to
	 * @param groupName
	 *          Hazelcast group name
	 * @param groupPassword
	 *          Hazelcast group password
	 * @return HazelcastInstance with specified connection arguments
	 * @throws RuntimeException
	 *           when the node cannot connect
	 */
	public static synchronized HazelcastInstance newNativeInstance(String host, int port, String groupName,
			String groupPassword) throws RuntimeException {
		if (isConnected()) {
			throw new RuntimeException("Already connected");
		}

		final Properties userProperties = new Properties();

		userProperties.setProperty(MEMBERS, String.format("%s:%d", host, port));
		userProperties.setProperty(GROUP, groupName);
		userProperties.setProperty(PASSWORD, groupPassword);

		try {
			Instance.init(NodeType.NATIVE, userProperties);
			return getInstance();
		} catch (ServiceException e) {
			throw new RuntimeException("Cannot initialize client connection");
		}
	}

	/**
	 * Shuts down connection to the Hazelcast cluster.
	 */
	public static synchronized void shutdown() {
		if (!isConnected()) {
			log.warn("Connection to the cluster already closed");
			hazelcastInstance = null;
			return;
		}

		getInstance().getLifecycleService().shutdown();
		hazelcastInstance = null;
	}

	/**
	 * Creates native HazelcastInstance.
	 * 
	 * @param userProperties
	 *          properties to be used while connecting
	 * @return connected native Hazelcast instance
	 * @throws ServiceException
	 *           when connection cannot be established.
	 */
	private static HazelcastInstance createNativeInstance(Properties userProperties) throws ServiceException {

		ClientConfig clientConfig = InstanceConfigHelper.createClientConfig(userProperties);

		return HazelcastClient.newHazelcastClient(clientConfig);

	}

	/**
	 * Creates data HazelcastInstance.
	 * 
	 * @param userProperties
	 *          properties to be used while connecting
	 * @return connected data Hazelcast instance
	 * @throws ServiceException
	 *           when connection cannot be established.
	 */
	private static HazelcastInstance createDataInstance(Properties userProperties) throws ServiceException {

		Config config = InstanceConfigHelper.createMemberConfig(userProperties);

		return Hazelcast.newHazelcastInstance(config);
	}

	/**
	 * Joins this node to the cluster.
	 * 
	 * @param type
	 *          type of the node
	 * @param userProperties
	 *          properties to be used
	 * @return connected node
	 * @throws ServiceException
	 *           when node cannot join
	 */
	private static HazelcastInstance join(NodeType type, Properties userProperties) throws ServiceException {
		switch (type) {
			case DATA:
				return createDataInstance(userProperties);
			case LITE:
				throw new UnsupportedOperationException("LITE node not implemented!");
			case NATIVE:
				return createNativeInstance(userProperties);
			default:
				throw new UnsupportedOperationException(nodeType.toString() + " unknown mode!");
		}
	}

	/**
	 * Whether the node is connected to the cluster
	 * 
	 * @return true of connected, false otherwise
	 */
	private static boolean isConnected() {
		if (hazelcastInstance != null) {
			if (hazelcastInstance instanceof HazelcastClient) {
				return ((HazelcastClient) hazelcastInstance).isActive();
			}
			return true;
		}
		return false;
	}

}
