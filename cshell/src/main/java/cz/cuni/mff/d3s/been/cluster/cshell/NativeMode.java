package cz.cuni.mff.d3s.been.cluster.cshell;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import cz.mff.dpp.args.ParseException;
import cz.mff.dpp.args.Parser;
import jline.console.ConsoleReader;

/**
 * @author Martin Sixta
 */
public class NativeMode extends ClusterMode {

	private HostInfo hostInfo;

	public NativeMode(ConsoleReader reader) {
		super(reader, "native> ");
	}

	@Override
	protected void connect(String[] args) {
		HazelcastInstance instance = getInstance();
		if (instance != null) {
			throw new IllegalArgumentException("Already connected!");
		}

		hostInfo = new HostInfo();

		Parser parser = new Parser(hostInfo);

		try {
			parser.parse(args);
		} catch (ParseException e) {
			parser.usage();
			throw new IllegalArgumentException("Cannot connect to the cluster!");
		}


		ClientConfig clientConfig = new ClientConfig();
		clientConfig.getGroupConfig().setName("dev").setPassword("dev-pass");
		clientConfig.addInetSocketAddress(hostInfo.getInetSocketAddress());

		instance = HazelcastClient.newHazelcastClient(clientConfig);
		setInstance(instance);
	}

	@Override
	protected void status(String[] args) {
		if (getInstance() == null) {
			out.println("Not connected");
		} else {
			out.println("Connected to " + hostInfo.getInetSocketAddress());
		}
	}
}
