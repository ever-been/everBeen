package cz.cuni.mff.d3s.been.cluster.action;

import java.util.Arrays;
import java.util.List;

import org.junit.*;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.impl.GroupProperties;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
import cz.cuni.mff.d3s.been.mq.rep.ReplyType;
import cz.cuni.mff.d3s.been.mq.req.Request;
import cz.cuni.mff.d3s.been.mq.req.RequestType;

/**
 * @author Martin Sixta
 */
public class MapGetActionTest extends Assert {

	private static final String KEY1 = "key1";
	private static final String VALUE1 = "value1";

	private static final String MAP = "map1";
	private static final String SELECTOR_SEPARATOR = "#";

	private static HazelcastInstance hazelcastInstance;
	private static ClusterContext ctx;

	@Before
	public void setUp() {
		hazelcastInstance.getMap(MAP).clear();
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.setProperty("hazelcast.logging.type", "none");
		Config c1 = buildConfig(false);

		c1.getNetworkConfig().setPort(35701);

		List<String> allMembers = Arrays.asList("127.0.0.1:35701");
		c1.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(allMembers);

		hazelcastInstance = Hazelcast.newHazelcastInstance(c1);
		ctx = new ClusterContext(hazelcastInstance);

	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ctx = null;
		hazelcastInstance.getLifecycleService().shutdown();
	}

	@Test
	public void testNonExistingKey() {
		Request request = new Request(RequestType.GET, getSelector("nonExistingKey"));
		Action action = Actions.createAction(request, ctx);

		Reply reply = action.goGetSome();

		assertSame(ReplyType.OK, reply.getReplyType());
		assertSame("", reply.getValue());
	}

	@Test
	@Ignore
	public void testExistingKey() {
		ctx.getMap(MAP).put(KEY1, VALUE1);
		Request request = new Request(RequestType.GET, getSelector(KEY1));
		Action action = Actions.createAction(request, ctx);

		Reply reply = action.goGetSome();

		assertSame(ReplyType.OK, reply.getReplyType());
		assertEquals(VALUE1, reply.getValue());
	}

	@Test
	@Ignore
	public void testMalformedSelectorNoSeparator() {
		Request request = new Request(RequestType.GET, "testMapkey1");
		Action action = Actions.createAction(request, ctx);

		Reply reply = action.goGetSome();

		assertSame(ReplyType.ERROR, reply.getReplyType());
	}

	@Test
	@Ignore
	public void testMalformedSelectorNull() {
		Request request = new Request(RequestType.GET, null);
		Action action = Actions.createAction(request, ctx);

		Reply reply = action.goGetSome();

		assertSame(ReplyType.ERROR, reply.getReplyType());
	}

	@Test
	@Ignore
	public void testMalformedSelectorNoMap() {
		Request request = new Request(RequestType.GET, concat("", KEY1));
		Action action = Actions.createAction(request, ctx);

		Reply reply = action.goGetSome();

		assertSame(ReplyType.ERROR, reply.getReplyType());
	}

	@Test
	@Ignore
	public void testMalformedSelectorNoKey() {
		Request request = new Request(RequestType.GET, concat(MAP, ""));
		Action action = Actions.createAction(request, ctx);

		Reply reply = action.goGetSome();

		assertSame(ReplyType.ERROR, reply.getReplyType());
	}

	// ------------------------------------------------------------------------
	// AUXILIARY FUNCTIONS
	// ------------------------------------------------------------------------

	private String concat(String map, String key) {
		return map + SELECTOR_SEPARATOR + key;
	}

	private String getSelector(String key) {
		return concat(MAP, key);
	}

	private static Config buildConfig(boolean multicastEnabled) {
		Config c = new Config();
		c.getGroupConfig().setName("group").setPassword("pass");
		c.setProperty(GroupProperties.PROP_MERGE_FIRST_RUN_DELAY_SECONDS, "10");
		c.setProperty(GroupProperties.PROP_MERGE_NEXT_RUN_DELAY_SECONDS, "5");
		c.setProperty(GroupProperties.PROP_MAX_NO_HEARTBEAT_SECONDS, "10");
		c.setProperty(GroupProperties.PROP_MASTER_CONFIRMATION_INTERVAL_SECONDS, "2");
		c.setProperty(GroupProperties.PROP_MAX_NO_MASTER_CONFIRMATION_SECONDS, "10");
		c.setProperty(GroupProperties.PROP_MEMBER_LIST_PUBLISH_INTERVAL_SECONDS, "10");
		final NetworkConfig networkConfig = c.getNetworkConfig();
		networkConfig.getJoin().getMulticastConfig().setEnabled(multicastEnabled);
		networkConfig.getJoin().getTcpIpConfig().setEnabled(!multicastEnabled);
		networkConfig.setPortAutoIncrement(false);
		return c;
	}
}
