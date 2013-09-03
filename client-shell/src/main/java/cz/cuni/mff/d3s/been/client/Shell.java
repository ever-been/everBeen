package cz.cuni.mff.d3s.been.client;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Properties;

import jline.console.ConsoleReader;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.NodeType;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * @author Martin Sixta
 * 
 *         WARNING: this code is in incubator phase
 * 
 */
public class Shell {
	@Option(name = "-cf", aliases = { "--config-file" }, usage = "Path to BEEN config file.")
	private String configFile;

	private ClusterContext clusterContext;

	public static void main(String[] args) {
		new Shell().doMain(args);
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

		try {
			// parse the arguments.
			parser.parseArgument(args);

			Properties properties = new Properties();

			if (configFile != null) {
				properties.load(new FileInputStream(configFile));
			}

			// connect to the cluster

			Instance.init(NodeType.NATIVE, properties);
			clusterContext = Instance.createContext();

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
