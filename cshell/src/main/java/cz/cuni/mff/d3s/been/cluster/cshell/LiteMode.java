package cz.cuni.mff.d3s.been.cluster.cshell;

import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.net.URL;

/**
 * @author Martin Sixta
 */
class LiteMode extends ClusterMode {

	public LiteMode(ConsoleReader reader) {
		super(reader, "lite> ");

	}


	@Override
	protected void connect(String[] args) {
		HazelcastInstance instance = getInstance();

		if (instance != null) {
			throw new IllegalArgumentException("Already connected!");
		}

		System.setProperty("hazelcast.lite.member", "true");

		URL url = this.getClass().getResource("/hazelcast.xml");

		Config config;
		try {
			config = new UrlXmlConfig(url);
		} catch (IOException e) {
			e.printStackTrace();
			config = null;
		}

		instance = Hazelcast.newHazelcastInstance(config);
		setInstance(instance);
	}

	@Override
	protected void status(String[] args) {
		//To change body of implemented methods use File | Settings | File Templates.
	}


}
