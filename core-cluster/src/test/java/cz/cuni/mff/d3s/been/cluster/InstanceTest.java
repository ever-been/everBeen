package cz.cuni.mff.d3s.been.cluster;

import static cz.cuni.mff.d3s.been.cluster.Instance.*;
import static cz.cuni.mff.d3s.been.cluster.InstanceConfigHelper.DEFAULT_PROPERTIES_RESOURCE;
import static cz.cuni.mff.d3s.been.cluster.InstanceConfigHelper.PROPERTY_HAZELCAST_PREFER_IPV4_STACK;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.config.*;
import com.hazelcast.nio.Address;

/**
 * @author Martin Sixta
 */
public class InstanceTest extends Assert {
	Properties defaults;

	Properties userProperties;

	@Before
	public void setUp() throws Exception {
		userProperties = new Properties();
		defaults = new Properties();

		defaults.load(InstanceTest.class.getResourceAsStream(DEFAULT_PROPERTIES_RESOURCE));
	}

	@After
	public void tearDown() throws Exception {
		userProperties = null;
		defaults = null;
	}

	@Test
	public
			void
			testCreateDefaultClientConfig() throws ServiceException, URISyntaxException {

		ClientConfig clientConfig = InstanceConfigHelper.createClientConfig(userProperties);

		GroupConfig groupConfig = clientConfig.getGroupConfig();

		// group config
		assertEquals(
				defaults.getProperty(PROPERTY_CLUSTER_GROUP),
				groupConfig.getName());
		assertEquals(
				defaults.getProperty(PROPERTY_CLUSTER_PASSWORD),
				groupConfig.getPassword());

		// timeout
		int timeoutExpected = getInt(defaults, PROPERTY_CLIENT_TIMEOUT);
		int timeoutActual = (int) TimeUnit.MILLISECONDS.toSeconds(clientConfig.getConnectionTimeout());
		assertEquals(timeoutExpected, timeoutActual);

		// address list
		final Collection<InetSocketAddress> clientAddressList = clientConfig.getAddressList();
		assertEquals(1, clientAddressList.size());
		URI uri = new URI("myuri://" + defaults.getProperty(PROPERTY_CLIENT_MEMBERS));

		InetSocketAddress defaultAddress = new InetSocketAddress(uri.getHost(), uri.getPort());

		assertTrue(clientAddressList.contains(defaultAddress));

	}

	@Test
	public void testCreateClientConfigWithMembers() throws ServiceException {
		userProperties.setProperty(
				PROPERTY_CLIENT_MEMBERS,
				"192.168.1.1:5555;192.168.1.10");

		ClientConfig clientConfig = InstanceConfigHelper.createClientConfig(userProperties);

		// address list
		final Collection<InetSocketAddress> clientAddressList = clientConfig.getAddressList();

		assertEquals(2, clientAddressList.size());

		assertTrue(clientAddressList.contains(new InetSocketAddress("192.168.1.1", 5555)));
		assertTrue(clientAddressList.contains(new InetSocketAddress("192.168.1.10", 5701)));

	}

	@Test
	public
			void
			testCreateDefaultMemberConfig() throws ServiceException, URISyntaxException {
		Config config = InstanceConfigHelper.createMemberConfig(userProperties);

		NetworkConfig networkConfig = config.getNetworkConfig();
		Join join = networkConfig.getJoin();

		assertEquals(
				defaults.getProperty(PROPERTY_CLUSTER_PREFER_IPV4),
				config.getProperty(PROPERTY_HAZELCAST_PREFER_IPV4_STACK));
		assertEquals(
				(int) Integer.valueOf(defaults.getProperty(PROPERTY_CLUSTER_PORT)),
				networkConfig.getPort());
		assertTrue(join.getMulticastConfig().isEnabled());
		assertFalse(join.getTcpIpConfig().isEnabled());

		MulticastConfig multicastConfig = join.getMulticastConfig();

		assertEquals(
				defaults.getProperty(PROPERTY_CLUSTER_MULTICAST_GROUP),
				multicastConfig.getMulticastGroup());
		assertEquals(
				getInt(defaults, PROPERTY_CLUSTER_MULTICAST_PORT),
				multicastConfig.getMulticastPort());

	}

	@Test
	public
			void
			testCreateMemberConfigWithInterfaces() throws ServiceException, URISyntaxException {
		String expectedInterface = "10.1.1.1-2";
		userProperties.setProperty(PROPERTY_CLUSTER_INTERFACES, "10.1.1.1-2");

		Config config = InstanceConfigHelper.createMemberConfig(userProperties);

		NetworkConfig networkConfig = config.getNetworkConfig();

		final Interfaces interfaces = networkConfig.getInterfaces();

		assertTrue(interfaces.getInterfaces().size() == 1);

		assertTrue(interfaces.getInterfaces().contains(expectedInterface));
	}

	@Test
	public
			void
			testCreateMemberConfigEnableIPv6() throws ServiceException, URISyntaxException {
		userProperties.setProperty(PROPERTY_CLUSTER_PREFER_IPV4, "false");

		Config config = InstanceConfigHelper.createMemberConfig(userProperties);

		assertEquals(
				"false",
				config.getProperty(PROPERTY_HAZELCAST_PREFER_IPV4_STACK));
	}

	@Test
	public
			void
			testCreateMemberConfigWithMulticastOptions() throws ServiceException, URISyntaxException {
		int port = 1111;
		String group = "224.2.2.1";

		userProperties.setProperty(
				PROPERTY_CLUSTER_MULTICAST_PORT,
				Integer.toString(port));
		userProperties.setProperty(PROPERTY_CLUSTER_MULTICAST_GROUP, group);

		Config config = InstanceConfigHelper.createMemberConfig(userProperties);

		NetworkConfig networkConfig = config.getNetworkConfig();
		Join join = networkConfig.getJoin();
		MulticastConfig multicastConfig = join.getMulticastConfig();

		join.getMulticastConfig();

		assertTrue(multicastConfig.isEnabled());
		assertEquals(port, multicastConfig.getMulticastPort());
		assertEquals(group, multicastConfig.getMulticastGroup());

		assertFalse(join.getTcpIpConfig().isEnabled());
	}

	@Test
	public
			void
			testCreateMemberConfigWithTcp() throws ServiceException, URISyntaxException, UnknownHostException {
		int port = 1111;

		Address member1 = new Address("192.168.1.1", 5555);
		Address member2 = new Address("192.168.1.10", 5701);

		String memberList = String.format(
				"%s:%d;%s",
				member1.getHost(),
				member1.getPort(),
				member2.getHost());

		userProperties.setProperty(PROPERTY_CLUSTER_JOIN, "tcp");
		userProperties.setProperty(PROPERTY_CLUSTER_TCP_MEMBERS, memberList);

		Config config = InstanceConfigHelper.createMemberConfig(userProperties);

		final NetworkConfig networkConfig = config.getNetworkConfig();
		final Join join = networkConfig.getJoin();
		final MulticastConfig multicastConfig = join.getMulticastConfig();
		final TcpIpConfig tcpIpConfig = join.getTcpIpConfig();

		assertTrue(tcpIpConfig.isEnabled());
		assertFalse(multicastConfig.isEnabled());

		assertEquals(2, tcpIpConfig.getAddresses().size());
		assertTrue(tcpIpConfig.getAddresses().contains(member1));
		assertTrue(tcpIpConfig.getAddresses().contains(member2));

	}

	private static int getInt(Properties properties, String key) {
		return Integer.valueOf(properties.getProperty(key));
	}

}
