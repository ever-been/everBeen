package cz.cuni.d3s.mff.been.client;

import java.io.PrintWriter;
import java.net.InetSocketAddress;

import jline.console.ConsoleReader;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;

import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.NodeType;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * @author Martin Sixta
 */
public class Shell {
	@Option(name = "-h", aliases = { "--host" }, usage = "Hostname of a cluster member to connect to")
	private String host = "localhost";

	@Option(name = "-p", aliases = { "--port" }, usage = "Port of the host")
	private int port = 5701;

	@Option(name = "-ehl", aliases = { "--enable-hazelcast-logging" }, usage = "Turns on Hazelcast logging.")
	private boolean debug = false;

	@Option(name = "-gn", aliases = { "--group-name" }, usage = "Group Name")
	private String groupName = "dev";

	@Option(name = "-gp", aliases = { "--group-password" }, usage = "Group Password")
	private String groupPassword = "dev-pass";

	private final ClusterContext clusterContext;

	public static void main(String[] args) {
		new Shell(new ClusterContext(cz.cuni.mff.d3s.been.cluster.Instance.getInstance())).doMain(args);
	}

	public Shell(ClusterContext clusterContext) {
		this.clusterContext = clusterContext;
	}

	private void doMain(String[] args) {
		connectClient(args);

		try {

			ConsoleReader reader = new ConsoleReader();

			IMode mode = new ClusterMode(reader, clusterContext);

			mode.setup(reader);

			String line;
			PrintWriter out = new PrintWriter(reader.getOutput());

			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				String[] tokens = line.trim().split(" ");

				try {
					mode = mode.takeAction(tokens);
				} catch (IllegalArgumentException ex) {
					out.println(ex.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}

				out.flush();

			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	private void connectClient(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);

		HazelcastClient hazelcastClient = null;

		try {
			// parse the arguments.
			parser.parseArgument(args);

			// connect to the cluster

			if (debug) {
				System.setProperty("hazelcast.logging.type", "slf4j");
			} else {
				System.setProperty("hazelcast.logging.type", "none");
			}

			InetSocketAddress socketAddress = new InetSocketAddress(host, port);

			ClientConfig clientConfig = new ClientConfig();
			clientConfig.getGroupConfig().setName(groupName).setPassword(groupPassword);
			clientConfig.addInetSocketAddress(socketAddress);

			hazelcastClient = HazelcastClient.newHazelcastClient(clientConfig);

			Instance.registerInstance(hazelcastClient, NodeType.NATIVE);

		} catch (CmdLineException e) {

			System.err.println(e.getMessage());
			System.err.println("\nUsage:");
			parser.printUsage(System.err);

			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
}
