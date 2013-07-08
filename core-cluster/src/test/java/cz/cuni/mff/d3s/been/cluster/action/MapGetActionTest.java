package cz.cuni.mff.d3s.been.cluster.action;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReplyType;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequestType;
import org.junit.*;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.impl.GroupProperties;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.socketworks.twoway.Request;

/**
 * @author Martin Sixta
 */
public class MapGetActionTest extends Assert {

	private static final String KEY1 = "key1";
	private static final String VALUE1 = "value1";

	private static final String SELECTOR_SEPARATOR = "#";

	private static final String TASK_ID = "abc-def";
	private static final String CONTEXT_ID = "fed-cba";

	private static final String MAP = String.format("checkpointmap_%s", CONTEXT_ID);

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
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.GET, "nonExistingKey");
		fillIds(request);
		Action action = Actions.createAction(request, ctx);

		Reply reply = action.handle();

		assertSame(ReplyType.OK, reply.getReplyType());
		assertSame(null, reply.getValue());
	}

	@Test
	public void testExistingKey() {
		ctx.getMap(MAP).put(KEY1, VALUE1);
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.GET, KEY1);
		fillIds(request);
		Action action = Actions.createAction(request, ctx);

		Reply reply = action.handle();

		assertSame(ReplyType.OK, reply.getReplyType());
		assertEquals(VALUE1, reply.getValue());
	}

	@Test
	public void testMalformedSelectorNull() {
        CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.GET, "");
		fillIds(request);

		Action action = Actions.createAction(request, ctx);

		Reply reply = action.handle();

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

	private void fillIds(CheckpointRequest request) {
		request.setTaskId(TASK_ID);
		request.setTaskContextId(CONTEXT_ID);
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
