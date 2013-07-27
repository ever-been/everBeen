package cz.cuni.mff.d3s.been.cluster;

import static cz.cuni.mff.d3s.been.cluster.ClusterClientConfiguration.*;
import static cz.cuni.mff.d3s.been.cluster.ClusterConfiguration.*;
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
import cz.cuni.mff.d3s.been.core.PropertyReader;

/**
 * Utility class for creating Hazelcast configurations.
 * 
 */
final class InstanceConfigHelper {

	/** Separator of list values */
	static final String VALUE_SEPARATOR = ";";

	/** Path to resource with default hazelcast configuration */
	static final String CONFIG_RESOURCE = "/hazelcast.xml";

	/** Name of hazelcast property for preferred TCP/IP stack */
	static final String PROPERTY_HAZELCAST_PREFER_IPV4_STACK = "hazelcast.prefer.ipv4.stack";

	/** Name of the hazelcast property to control binding of local interfaces */
	static final String PROPERTY_HAZELCAST_SOCKET_BIND_ANY = "hazelcast.socket.bind.any";

	/** How will Hazelcast log its messages */
	private static final String PROPERTY_HAZELCAST_LOGGING_TYPE = "hazelcast.logging.type";

	/**
	 * Properties which are used to create configs.
	 * 
	 * The resulting properties are created by merging default and user
	 * properties.
	 */
	private Properties properties;

	/** Creates the helper class */
	private InstanceConfigHelper(Properties userProperties) throws ServiceException {
		this.properties = userProperties;
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
	static ClientConfig createClientConfig(Properties userProperties) throws ServiceException {
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
	static Config createMemberConfig(Properties userProperties) throws ServiceException {
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

		final PropertyReader propReader = PropertyReader.on(properties);

		final int timeout = (int) SECONDS.toMillis(propReader.getInteger(TIMEOUT, DEFAULT_TIMEOUT));
		final List<InetSocketAddress> socketAddresses = getPeers(propReader.getString(MEMBERS, DEFAULT_MEMBERS));

		final GroupConfig groupConfig = createGroupConfig();
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setConnectionTimeout(timeout).setGroupConfig(groupConfig).addInetSocketAddress(socketAddresses);

		// Enable/Disable hazelcast logging
		final boolean enableHazelcastLogging = propReader.getBoolean(LOGGING, DEFAULT_LOGGING);
		final String loggingMode = enableHazelcastLogging ? "slf4j" : "none";

		// There is no way to set property on the ClientConfig as far as I know (v2.5)
		// So you system properties
		System.setProperty(PROPERTY_HAZELCAST_LOGGING_TYPE, loggingMode);

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
			String msg = String.format("Cannot read Hazelcast's default configuration %s", CONFIG_RESOURCE);
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

		final PropertyReader propReader = PropertyReader.on(properties);

		final int port = propReader.getInteger(PORT, DEFAULT_PORT);
		final Interfaces interfaces = getInterfaces(propReader.getString(INTERFACES, DEFAULT_INTERFACES));
		final GroupConfig groupConfig = createGroupConfig();
		final Join joinConfig = createJoinConfig();

		final NetworkConfig networkConfig = new NetworkConfig();

		networkConfig.setPort(port).setInterfaces(interfaces).setJoin(joinConfig);
		mainConfig.setNetworkConfig(networkConfig).setGroupConfig(groupConfig);
		mainConfig.setProperty(PROPERTY_HAZELCAST_PREFER_IPV4_STACK, propReader.getBoolean(PREFER_IPV4, DEFAULT_PREFER_IPV4).toString());
		mainConfig.setProperty(PROPERTY_HAZELCAST_SOCKET_BIND_ANY, propReader.getBoolean(SOCKET_BIND_ANY, DEFAULT_SOCKET_BIND_ANY).toString());
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
		final PropertyReader propReader = PropertyReader.on(properties);

		String group = propReader.getString(GROUP, DEFAULT_GROUP);
		String password = propReader.getString(PASSWORD, DEFAULT_PASSWORD);
		return new GroupConfig().setName(group).setPassword(password);
	}

	private JOIN_TYPE getJoinType() throws ServiceException {
		final PropertyReader propReader = PropertyReader.on(properties);

		String joinStringValue = propReader.getString(JOIN, DEFAULT_JOIN);
		try {
			return JOIN_TYPE.valueOf(joinStringValue.toUpperCase());
		} catch (IllegalArgumentException e) {
			String msg = String.format("Unknown join type %s", joinStringValue);
			throw new ServiceException(msg, e);
		}
	}

	private MulticastConfig createMulticastConfig() throws ServiceException {
		final PropertyReader propReader = PropertyReader.on(properties);

		MulticastConfig multicastConfig = new MulticastConfig();
		multicastConfig.setEnabled(true);
		multicastConfig.setMulticastGroup(propReader.getString(MULTICAST_GROUP, DEFAULT_MULTICAST_GROUP));
		multicastConfig.setMulticastPort(propReader.getInteger(MULTICAST_PORT, DEFAULT_MULTICAST_PORT));

		return multicastConfig;
	}

	private TcpIpConfig createTcpIpConfig() throws ServiceException {
		final PropertyReader propReader = PropertyReader.on(properties);

		TcpIpConfig tcpIpConfig = new TcpIpConfig();
		tcpIpConfig.setEnabled(true);

		for (InetSocketAddress inetSocketAddress : getPeers(propReader.getString(TCP_MEMBERS, DEFAULT_TCP_MEMBERS))) {
			tcpIpConfig.addAddress(new Address(inetSocketAddress));
		}

		return tcpIpConfig;
	}

	private List<InetSocketAddress> getPeers(String peersList) {
		List<InetSocketAddress> peers = new LinkedList<>();
		for (String address : peersList.split(VALUE_SEPARATOR)) {
			peers.addAll(AddressHelper.getSocketAddresses(address));
		}

		return peers;

	}

	private Interfaces getInterfaces(String interfaceList) {
		Interfaces interfaces = new Interfaces();

		if (interfaceList == null || interfaceList.isEmpty()) {
			interfaces.setEnabled(false);
			return interfaces;
		}

		for (String ip : interfaceList.split(VALUE_SEPARATOR)) {
			interfaces.addInterface(ip);
		}

		interfaces.setEnabled(true);
		return interfaces;
	}
}
