package cz.cuni.mff.d3s.been.node;

import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.io.IOException;
import java.net.URL;

/**
 * Singleton for the HazelcastInstance object.
 *
 *
 * WARNING: this class is not thread-safe! The instance should be created
 * by calling getInstance() in the main thread!
 *
 * @author Martin Sixta
 */
final class Instance {
	private static HazelcastInstance instance;


	public static HazelcastInstance getInstance() {
		if (instance == null) {
			join();
		}

		assert(instance != null);
		return instance;
	}

	private static void join() {
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
