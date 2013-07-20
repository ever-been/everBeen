package cz.cuni.mff.d3s.been.cmd;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiImpl;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.NodeType;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Martin Sixta
 */
public final class Client {

	private static final Logger log = LoggerFactory.getLogger(Client.class);

	@Option(name = "-cf", aliases = { "--config-file" }, usage = "Path to BEEN config file.")
	private String configFile;

	private ClusterContext clusterContext;

	public static void main(String[] args) {
		new Client().doMain(args);
	}

	private Client() {

	}

	void doMain(String[] args) {
		// parse the arguments.
		CmdLineParser parser = new CmdLineParser(this);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("\nUsage:");
			parser.printUsage(System.err);

			System.exit(1);
		}


		BeenApi been = null;


		try {
			been = connect();
		} catch (ServiceException e) {

			System.err.println("Connection to been encountered an exception");
		} finally {
			if (been != null) {
				been.shutdown();
			}
		}

	}

	private BeenApi connect() throws ServiceException {

		try{
			Properties properties = new Properties();

			if (configFile != null) {
				properties.load(new FileInputStream(configFile));
			}

			// connect to the cluster

			Instance.init(NodeType.NATIVE, properties);
			clusterContext = new ClusterContext(Instance.getInstance());


			return new BeenApiImpl(clusterContext);


		} catch (IOException e) {
			String msg = String.format("Cannot read configuration file '%s'", configFile);
			throw new ServiceException(msg, e);
		}


	}

}