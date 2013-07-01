package cz.cuni.mff.d3s.been.cluster;

import static cz.cuni.mff.d3s.been.cluster.Instance.*;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.hazelcast.client.AddressHelper;
import com.hazelcast.client.ClientConfig;
import com.hazelcast.config.*;
import com.hazelcast.nio.Address;

/**
 * Utility class for creating Hazelcast configurations.
 * 
 * ClientConfig and (Member)Config mixed together.
 */
final class InstanceConfigHelper {

	/** Separator of list values */
	static final String VALUE_SEPARATOR = ";";

	/** Path to resource with default hazelcast configuration */
	static final String CONFIG_RESOURCE = "/hazelcast.xml";

	/** Path to resource with default user configuration */
	static final String DEFAULT_PROPERTIES_RESOURCE = "/default-network.properties";

	/** Name of hazelcast property for preferred TCP/IP stack */
	static final String PROPERTY_HAZELCAST_PREFER_IPV4_STACK = "hazelcast.prefer.ipv4.stack";

	/** Name of the hazelcast property to control binding of local interfaces */
	static final String PROPERTY_HAZELCAST_SOCKET_BIND_ANY = "hazelcast.socket.bind.any";

	/** Type of the Hazelcast join method. */
	private static enum JOIN_TYPE {
		MULTICAST, TCP
	}

	/**
	 * Properties which are used to create configs.
	 * 
	 * The resulting properties are created by merging default and user
	 * properties.
	 */
	private Properties properties;

	/** Default properties */
	private Properties defaults;

	/** Creates the helper class */
	private InstanceConfigHelper(Properties userProperties)
			throws ServiceException {

		// Create and load default network properties
		defaults = new Properties();

		try {
			defaults.load(Instance.class.getResourceAsStream(DEFAULT_PROPERTIES_RESOURCE));
		} catch (IOException e) {
			throw new ServiceException("Cannot load default properties for cluster configuration", e);
		}

		initProperties(userProperties);

	}

	/**
	 * Creates configuration for a Hazelcast client
	 * 
	 * @param userProperties
	 *          BEEN's user-defined properties
	 * @return Hazelcast client configuration
	 * @throws ServiceException
	 *           if configuration cannot be created
	 */
	static
			ClientConfig
			createClientConfig(Properties userProperties) throws ServiceException {
		return new InstanceConfigHelper(userProperties).createClientConfig();
	}

	/**
	 * Creates configuration for a Hazelcast cluster member
	 * 
	 * @param userProperties
	 *          BEEN's user-defined properties
	 * @return Hazelcast member configuration
	 * @throws ServiceException
	 *           if configuration cannot be created
	 */
	static
			Config
			createMemberConfig(Properties userProperties) throws ServiceException {
		return new InstanceConfigHelper(userProperties).createMemberConfig();
	}

	/**
	 * The actual function which creates ClientConfig.
	 * 
	 * @return {ClientConfig
	 * @throws ServiceException
	 *           if configuration cannot be created
	 */
	ClientConfig createClientConfig() throws ServiceException {

		final int timeout = (int) SECONDS.toMillis(getInt(PROPERTY_CLIENT_TIMEOUT));
		final List<InetSocketAddress> socketAddresses = getPeers(PROPERTY_CLIENT_MEMBERS);
		final GroupConfig groupConfig = createGroupConfig();

		ClientConfig clientConfig = new ClientConfig();

		clientConfig.setConnectionTimeout(timeout).setGroupConfig(groupConfig).addInetSocketAddress(
				socketAddresses);

		return clientConfig;
	}

	/**
	 * 
	 * The actual function which creates Config.
	 * 
	 * @return Config
	 * @throws ServiceException
	 *           if configuration cannot be created
	 */
	Config createMemberConfig() throws ServiceException {
		URL url = InstanceConfigHelper.class.getResource(CONFIG_RESOURCE);

		Config config;
		try {
			// create default config
			config = new UrlXmlConfig(url);
		} catch (IOException e) {
			String msg = String.format(
					"Cannot read Hazelcast's default configuration %s",
					CONFIG_RESOURCE);
			throw new ServiceException(msg, e);
		}

		// override with user-defined configuration
		overrideConfiguration(config);

		return config;
	}

	/**
	 * Overrides the default configuration with user-defined values.
	 * 
	 * @param mainConfig
	 *          Config to override
	 * @throws ServiceException
	 */
	private void overrideConfiguration(Config mainConfig) throws ServiceException {

		final int port = getInt(PROPERTY_CLUSTER_PORT);
		final Interfaces interfaces = getInterfaces(PROPERTY_CLUSTER_INTERFACES);
		final GroupConfig groupConfig = createGroupConfig();
		final Join joinConfig = createJoinConfig();

		final NetworkConfig networkConfig = new NetworkConfig();

		networkConfig.setPort(port).setInterfaces(interfaces).setJoin(joinConfig);

		mainConfig.setNetworkConfig(networkConfig).setGroupConfig(groupConfig);

		mainConfig.setProperty(
				PROPERTY_HAZELCAST_PREFER_IPV4_STACK,
				getString(PROPERTY_CLUSTER_PREFER_IPV4));

		mainConfig.setProperty(
				PROPERTY_HAZELCAST_SOCKET_BIND_ANY,
				getString(PROPERTY_CLUSTER_SOCKET_BIND_ANY));

	}

	private Join createJoinConfig() throws ServiceException {
		final Join join = new Join();

		switch (getJoinType()) {
			case MULTICAST:
				join.setMulticastConfig(createMulticastConfig());
				join.getTcpIpConfig().setEnabled(false);
				break;
			case TCP:
				join.setTcpIpConfig(createTcpIpConfig());
				join.getMulticastConfig().setEnabled(false); // enabled by default
				break;
			default:
				throw new ServiceException("Unimplemented join type " + getJoinType());
		}

		return join;

	}

	private GroupConfig createGroupConfig() {
		String group = getString(PROPERTY_CLUSTER_GROUP);
		String password = getString(PROPERTY_CLUSTER_PASSWORD);
		return new GroupConfig().setName(group).setPassword(password);
	}

	private JOIN_TYPE getJoinType() throws ServiceException {
		String joinStringValue = getString(PROPERTY_CLUSTER_JOIN);
		try {
			return JOIN_TYPE.valueOf(joinStringValue.toUpperCase());
		} catch (IllegalArgumentException e) {
			String msg = String.format("Unknown join type %s", joinStringValue);
			throw new ServiceException(msg, e);
		}
	}

	private MulticastConfig createMulticastConfig() throws ServiceException {

		MulticastConfig multicastConfig = new MulticastConfig();
		multicastConfig.setEnabled(true);
		multicastConfig.setMulticastGroup(getString(PROPERTY_CLUSTER_MULTICAST_GROUP));
		multicastConfig.setMulticastPort(getInt(PROPERTY_CLUSTER_MULTICAST_PORT));

		return multicastConfig;
	}

	private TcpIpConfig createTcpIpConfig() throws ServiceException {
		TcpIpConfig tcpIpConfig = new TcpIpConfig();
		tcpIpConfig.setEnabled(true);

		for (InetSocketAddress inetSocketAddress : getPeers(PROPERTY_CLUSTER_TCP_MEMBERS)) {
			tcpIpConfig.addAddress(new Address(inetSocketAddress));
		}

		return tcpIpConfig;
	}

	private List<InetSocketAddress> getPeers(String key) {
		List<InetSocketAddress> peers = new LinkedList<>();
		String peersList = getString(key);

		for (String address : peersList.split(VALUE_SEPARATOR)) {
			peers.addAll(AddressHelper.getSocketAddresses(address));
		}

		return peers;

	}

	private Interfaces getInterfaces(String key) {
		Interfaces interfaces = new Interfaces();
		String value = getString(key);

		if (value == null || value.isEmpty()) {
			interfaces.setEnabled(false);
			return interfaces;
		}

		for (String ip : value.split(VALUE_SEPARATOR)) {
			interfaces.addInterface(ip);
		}

		interfaces.setEnabled(true);
		return interfaces;
	}

	/**
	 * Merges default properties with user-defined properties.
	 * 
	 * @param userProperties
	 *          user-defined properties
	 */
	private void initProperties(Properties userProperties) {
		if (userProperties == null) {
			properties = new Properties();
		} else {
			properties = userProperties;
		}

		for (String key : defaults.stringPropertyNames()) {
			String propertyValue = properties.getProperty(key, "");
			if (propertyValue.isEmpty()) {
				String defaultValue = defaults.getProperty(key);
				properties.setProperty(key, defaultValue);
			}
		}
	}

	/**
	 * 
	 * 
	 * @param key
	 *          key
	 * @return value for the given key or an empty String
	 */
	private String getString(String key) {
		String value = properties.getProperty(key);

		if (value == null) {
			return "";
		}

		return value;
	}

	/**
	 * Returns a property value as an integer.
	 * 
	 * @param key
	 *          key
	 * @return integer representation of a value
	 * @throws ServiceException
	 */
	private int getInt(String key) throws ServiceException {
		String stringValue = properties.getProperty(key);

		try {
			return Integer.parseInt(stringValue);
		} catch (NumberFormatException e) {
			String msg = String.format(
					"Cannot convert '%s' to integer value for property '%s'",
					stringValue,
					key);
			throw new ServiceException(msg, e);
		}
	}

}
