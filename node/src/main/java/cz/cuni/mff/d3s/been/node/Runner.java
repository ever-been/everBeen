package cz.cuni.mff.d3s.been.node;

import static cz.cuni.mff.d3s.been.core.StatusCode.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import cz.cuni.mff.d3s.been.repository.Repository;
import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.cluster.*;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.hostruntime.HostRuntimes;
import cz.cuni.mff.d3s.been.storage.Storage;
import cz.cuni.mff.d3s.been.storage.StorageBuilderFactory;
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

	@Option(name = "-t", aliases = { "--node-type" }, usage = "Type of the node. DEFAULT is DATA")
	private NodeType nodeType = NodeType.DATA;

	@Option(name = "-cf", aliases = { "--config-file" }, usage = "Path to BEEN config file.")
	private String configFile = "../conf/been.properties";

	@Option(name = "-r", aliases = { "--host-runtime" }, usage = "Whether to run Host runtime on this node")
	private boolean runHostRuntime = false;

	@Option(name = "-sw", aliases = { "--software-repository" }, usage = "Whether to run Software Repository on this node")
	private boolean runSWRepository = false;

	@Option(name = "-swp", aliases = { "--software-repository-port" }, usage = "Set the port for Software Repository")
	private Integer swRepoPort = 8000;

	@Option(name = "-rr", aliases = { "--repository" }, usage = "Whether to run Repository on this node. Requires a running matching persistence layer")
	private boolean runRepository = false;

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

		if (printHelp) {
			printUsage();
			System.exit(EX_OK.getCode());
		}

		Properties properties = loadProperties();

		configureLogging(enableHazelcastLogging);

		HazelcastInstance instance = null;

		try {
			// Join the cluster
			log.info("The node is connecting to the cluster");
			instance = getInstance(nodeType, properties);
			log.info("The node is now connected to the cluster");
		} catch (ServiceException e) {
			log.error("Failed to initialize cluster instance", e);

		}

		Reaper clusterReaper = new ClusterReaper(instance);

		try {
			// Run Task Manager on DATA nodes
			if (nodeType == NodeType.DATA) {
				clusterReaper.pushTarget(startTaskManager(instance));
			}

			// standalone services
			if (runSWRepository) {
				clusterReaper.pushTarget(startSWRepository(instance, properties));
			}

			if (runHostRuntime) {
				clusterReaper.pushTarget(startHostRuntime(instance, properties));
			}

			// Services that require a persistence layer
			if (runRepository) {
				final Storage storage = StorageBuilderFactory.createBuilder(properties).build();
				clusterReaper.pushTarget(startRepository(instance, storage));
			}

		} catch (ServiceException se) {
			log.error("Service bootstrap failed.", se);
			clusterReaper.start();
			try {
				clusterReaper.join();
			} catch (InterruptedException e) {
				log.error("Failed to perform cleanup due to user interruption. Exiting dirty.");
			}
			EX_COMPONENT_FAILED.sysExit();
		}

		Runtime.getRuntime().addShutdownHook(clusterReaper);
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

	private
			IClusterService
			startTaskManager(final HazelcastInstance instance) throws ServiceException {
		IClusterService taskManager = Managers.getManager(instance);
		taskManager.start();
		return taskManager;
	}

	private
			IClusterService
			startHostRuntime(final HazelcastInstance instance, Properties properties) throws ServiceException {
		IClusterService hostRuntime = HostRuntimes.getRuntime(instance, properties);
		hostRuntime.start();
		return hostRuntime;
	}

	private IClusterService startSWRepository(HazelcastInstance instance, Properties properties) throws ServiceException {
		ClusterContext ctx = new ClusterContext(instance);
		SoftwareRepository softwareRepository = SoftwareRepositories.createSWRepository(
				ctx,
				properties);
		softwareRepository.init();

		softwareRepository.start();
		return softwareRepository;
	}

	private IClusterService startRepository(HazelcastInstance instance,
			Storage storage) throws ServiceException {
		ClusterContext ctx = new ClusterContext(instance);
		Repository repository = Repository.create(ctx, storage);
		repository.start();
		return repository;
	}

	private HazelcastInstance getInstance(final NodeType nodeType,
			Properties properties) throws ServiceException {
		Instance.init(nodeType, properties);
		return Instance.getInstance();
	}

	private void configureLogging(final boolean enableHazelcastLogging) {
		if (enableHazelcastLogging) {
			System.setProperty("hazelcast.logging.type", "slf4j");
		} else {
			System.setProperty("hazelcast.logging.type", "none");
		}
	}

	private Properties loadProperties() {

		final Properties properties = new Properties();

		final File propertiesFile = new File(configFile);
		if (!propertiesFile.exists()) {
			log.warn(
					"Could not find property file \"{}\". Will start with default configuration.",
					propertiesFile.getAbsolutePath());
			return properties;
		}

		FileReader propertyFileReader = null;
		try {
			propertyFileReader = new FileReader(propertiesFile);
		} catch (IOException e) {
			log.warn(
					"Failed to open properties file \"{}\" for reading.",
					propertiesFile.getAbsolutePath());
			return properties;
		}

		try {
			properties.load(propertyFileReader);
		} catch (IOException e) {
			log.warn(
					"Could not parse properties file \"{}\".",
					propertiesFile.getAbsolutePath());
		} finally {
			IOUtils.closeQuietly(propertyFileReader);
		}

		log.info(
				"Properties loaded from file \"{}\".",
				propertiesFile.getAbsolutePath());

		return properties;
	}
}
