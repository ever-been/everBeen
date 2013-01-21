package cz.cuni.mff.d3s.been.cluster;

import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.io.IOException;
import java.net.URL;

/**
 * Singleton for the HazelcastInstance object.
 * <p/>
 * The class is responsible for joining the cluster with appropriate NodeType.
 * The NodeType must be set before attempt to obtain a HazelcastInstance.
 * <p/>
 *
 * WARNING: this class is not thread-safe! The instance should be created
 * by calling getInstance() in the main thread!
 *
 * @author Martin Sixta
 */
public final class Instance {
	private static HazelcastInstance instance;
	private static NodeType nodeType = null;

	public static NodeType getNodeType() {
		return nodeType;
	}

	/**
	 *
	 * Creates new HazelcastInstance according to supplied type.
	 *
	 * The function can be called only once! If you need the instance
	 * call getInstance().
	 *
	 * @param type
	 * @return
	 */
	public static HazelcastInstance newInstance(NodeType type) {
		if (nodeType != null) {
			throw new IllegalArgumentException("Only one instance allowed!");
		}

		nodeType = type;

		return getInstance();
	}

	public static HazelcastInstance getInstance() {
		if (nodeType == null) {
			throw new IllegalStateException("Must call Instance.setNodeType first!");
		}

		if (instance == null) {
			join();
		}

		assert (instance != null);
		return instance;
	}

	private static void join() {
		switch (nodeType) {
			case DATA:
				join_data();
				break;
			case LITE:
				throw new UnsupportedOperationException("LITE node not implemented!");
			case NATIVE:
				throw new UnsupportedOperationException("NATIVE mode not implemeted!");
			default:
				throw new UnsupportedOperationException(nodeType.toString() + " unknown mode!");
		}
	}


	private static void join_data() {
		URL url = Instance.class.getResource("/hazelcast.xml");

		Config config;
		try {
			config = new UrlXmlConfig(url);
		} catch (IOException e) {
			e.printStackTrace();
			config = null;
		}

		instance = Hazelcast.newHazelcastInstance(config);
	}
}
