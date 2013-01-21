package cz.cuni.mff.d3s.been.node;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.NodeType;
import cz.cuni.mff.d3s.been.task.Managers;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cz.cuni.mff.d3s.been.node.StatusCode.EX_OK;
import static cz.cuni.mff.d3s.been.node.StatusCode.EX_USAGE;


/**
 * Entry point for BEEN nodes.
 * <p/>
 * Responsibilities of BEEN nodes include:
 * - joining the cluster
 * - scheduling tasks
 * <p/>
 * Possibly, there can be three types of a node:
 * - full: cluster membership + data + event handling
 * - lite: cluster membership + event handling
 * - client: event handling
 * <p/>
 * Clients nodes could be used as "very lite" runtimes.
 * Lite nodes have the overhead of cluster membership, but does not hold/replicate data.
 * <p/>
 * <p/>
 * So far only full node is implemented.
 *
 * @author Martin Sixta
 */
public class Runner {

	// ------------------------------------------------------------------------
	// LOGGING
	// ------------------------------------------------------------------------

	private static final Logger log = LoggerFactory.getLogger(Runner.class);


	// ------------------------------------------------------------------------
	// COMMAND LINE ARGUMENTS
	// ------------------------------------------------------------------------

	/**
	 * Type of the node.
	 */
	@Option(name = "-t", aliases = {"--node-type"}, usage = "Type of the node. DEFAULT is DATA")
	private NodeType nodeType = NodeType.DATA;


	/**
	 * Whether to run Host Runtime on this node.
	 *
	 */
	@Option(name = "-r", aliases = {"--host-runtime"}, usage = "Whether to run Host runtime on this node")
	private boolean runHostRuntime = false;

	@Option(name = "-ehl", aliases = {"--enable-hazelcast-logging"}, usage = "Turns on Hazelcast logging")
	private boolean enableHazelcastLogging = false;

	@Option(name = "-h", aliases = {"--help"}, usage = "Prints help")
	private boolean printHelp = false;



	public static void main(String[] args) {
		new Runner().doMain(args);
	}


	// ------------------------------------------------------------------------
	// MAIN BEEN FUNCTION
	// ------------------------------------------------------------------------

	public void doMain(final String[] args) {

		parseCmdLineArguments(args);

		if (printHelp) {
			printUsage();
			System.exit(EX_OK.getCode());
		}

		configureLogging(enableHazelcastLogging);


		// Join the cluster
		log.info("The node is connecting to the cluster");
		HazelcastInstance instance = getInstance(nodeType);
		log.info("The node is now connected to the cluster");


		// Run Task Manager on DATA nodes
		if (nodeType == NodeType.DATA) {
			log.info("Starting Task Manager");
			startTaskManager(instance);
			log.info("Task Manager started.");
		}

		// Host Runtime
		if (runHostRuntime) {
			log.warn("Starting Host Runtime.");
			startHostRuntime(instance);
			log.info("Host Runtime Started");
		}
	}

	private void printUsage() {
		CmdLineParser parser = new CmdLineParser(this);
		parser.printUsage(System.out);
	}


	// ------------------------------------------------------------------------
	// AUXILIARY FUNCTIONS
	// ------------------------------------------------------------------------

	/**
	 * Parses supplied command line arguments for this object.
	 * <p/>
	 * In case of error, an error message and usage is print to System.err,
	 * then program quits.
	 *
	 * @param args
	 */
	private void parseCmdLineArguments(final String[] args) {
		// Handle command-line arguments
		CmdLineParser parser = new CmdLineParser(this);

		try {
			// parse the arguments.
			parser.parseArgument(args);

		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("\nUsage:");
			parser.printUsage(System.err);

			System.exit(EX_USAGE.getCode());
		}

	}

	private void startTaskManager(final HazelcastInstance instance) {
		IClusterService taskManager = Managers.getManager(instance);
		taskManager.start();
	}

	private void startHostRuntime(final HazelcastInstance instance) {

	}

	private HazelcastInstance getInstance(final NodeType nodeType) {
		return Instance.newInstance(nodeType);
	}

	private void configureLogging(final boolean enableHazelcastLogging) {
		if (enableHazelcastLogging) {
			System.setProperty("hazelcast.logging.type", "slf4j");
		} else {
			System.setProperty("hazelcast.logging.type", "none");
		}
	}
}
