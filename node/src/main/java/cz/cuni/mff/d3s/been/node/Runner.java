package cz.cuni.mff.d3s.been.node;

import static cz.cuni.mff.d3s.been.core.StatusCode.EX_OK;
import static cz.cuni.mff.d3s.been.core.StatusCode.EX_USAGE;

import java.util.Stack;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.NodeType;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.StopClusterServicesHook;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.hostruntime.HostRuntimes;
import cz.cuni.mff.d3s.been.resultsrepository.ResultsRepositories;
import cz.cuni.mff.d3s.been.resultsrepository.ResultsRepository;
import cz.cuni.mff.d3s.been.swrepository.SoftwareRepositories;
import cz.cuni.mff.d3s.been.swrepository.SoftwareRepository;
import cz.cuni.mff.d3s.been.task.Managers;

/**
 * Entry point for BEEN nodes.
 * <p/>
 * Responsibilities of BEEN nodes include: - joining the cluster - scheduling
 * tasks
 * <p/>
 * Possibly, there can be three types of a node: - full: cluster membership +
 * data + event handling - lite: cluster membership + event handling - client:
 * event handling
 * <p/>
 * Clients nodes could be used as "very lite" runtimes. Lite nodes have the
 * overhead of cluster membership, but does not hold/replicate data.
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
	@Option(name = "-t", aliases = { "--node-type" }, usage = "Type of the node. DEFAULT is DATA")
	private NodeType nodeType = NodeType.DATA;

	/**
	 * Whether to run Host Runtime on this node.
	 * 
	 */
	@Option(name = "-r", aliases = { "--host-runtime" }, usage = "Whether to run Host runtime on this node")
	private boolean runHostRuntime = false;

	@Option(name = "-sw", aliases = { "--software-repository" }, usage = "Whether to run Software Repository on this node")
	private boolean runSWRepository = false;

	@Option(name = "-rr", aliases = { "--results-repository" }, usage = "Whether to run Results Repository on this node. Requires a running persistence layer")
	private boolean runResultsRepository = false;

	@Option(name = "-ehl", aliases = { "--enable-hazelcast-logging" }, usage = "Turns on Hazelcast logging")
	private boolean enableHazelcastLogging = false;

	@Option(name = "-h", aliases = { "--help" }, usage = "Prints help")
	private boolean printHelp = false;

	public static void main(String[] args) {
		new Runner().doMain(args);
	}

	// ------------------------------------------------------------------------
	// MAIN BEEN FUNCTION
	// ------------------------------------------------------------------------

	public void doMain(final String[] args) {

		parseCmdLineArguments(args);
		final Stack<IClusterService> runningServices = new Stack<IClusterService>();

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
			runningServices.push(startTaskManager(instance));
		}

		// Software Repository
		if (runSWRepository) {
			runningServices.push(startSWRepository(instance));
		}

		// Host Runtime
		if (runHostRuntime) {
			runningServices.push(startHostRuntime(instance));
		}

		if (runResultsRepository) {
			runningServices.push(startResultsRepository(instance));
		}

		Runtime.getRuntime().addShutdownHook(
				new StopClusterServicesHook(runningServices, instance));
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
	 * In case of error, an error message and usage is print to System.err, then
	 * program quits.
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

	private IClusterService startTaskManager(final HazelcastInstance instance) {
		log.info("Starting Task Manager.");
		IClusterService taskManager = Managers.getManager(instance);
		try {
			taskManager.start();
			log.info("Task Manager successfully started.");
		} catch (ServiceException e) {
			log.error("Task Manager could not be started - {}", e.getMessage());
			log.debug("Reasons for Task Manager not starting:", e);
		}
		return taskManager;
	}

	private IClusterService startHostRuntime(final HazelcastInstance instance) {
		log.warn("Starting Host Runtime");

		IClusterService hostRuntime = HostRuntimes.getRuntime(instance);
		try {
			hostRuntime.start();
			log.info("Host Runtime Started");
		} catch (ServiceException e) {
			log.error("Host Runtime cannot be started", e.getMessage());
			log.debug("Reasons for Host Runtime not starting:", e);
		}
		return hostRuntime;
	}

	private IClusterService startSWRepository(HazelcastInstance instance) {
		log.info("Starting Software repository");
		ClusterContext ctx = new ClusterContext(instance);

		String host = ctx.getInetSocketAddress().getHostName();
		int port = 8000;
		SoftwareRepository softwareRepository = SoftwareRepositories.createSWRepository(
				ctx,
				host,
				port);
		softwareRepository.init();

		try {
			softwareRepository.start();
			log.info("Software Repository successfully started.");
		} catch (ServiceException e) {
			log.error("Software Repository could not be started - {}", e.getMessage());
			log.debug("Reasons for Software Repository not starting:", e);
		}
		return softwareRepository;
	}

	private IClusterService startResultsRepository(HazelcastInstance instance) {
		log.info("Starting Results repository");
		ClusterContext ctx = new ClusterContext(instance);
		ResultsRepository resultsRepository = ResultsRepositories.createResultsRepository(ctx);
		try {
			resultsRepository.start();
			log.info("Results Repository successfully started.");
		} catch (ServiceException e) {
			log.error("Results Repository could not be started - {}", e.getMessage());
			log.debug("Reasons for Results Repository not starting:", e);
		}
		return resultsRepository;
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
