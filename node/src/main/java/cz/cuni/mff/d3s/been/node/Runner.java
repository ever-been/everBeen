package cz.cuni.mff.d3s.been.node;

import static cz.cuni.mff.d3s.been.core.StatusCode.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.cluster.*;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.hostruntime.HostRuntime;
import cz.cuni.mff.d3s.been.hostruntime.HostRuntimes;
import cz.cuni.mff.d3s.been.logging.ServiceLogPersister;
import cz.cuni.mff.d3s.been.repository.Repository;
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
public class Runner implements Reapable {

	// ------------------------------------------------------------------------
	// LOGGING
	// ------------------------------------------------------------------------

	private static final Logger log = LoggerFactory.getLogger(Runner.class);

	// ------------------------------------------------------------------------
	// COMMAND LINE ARGUMENTS
	// ------------------------------------------------------------------------

	@Option(name = "-t", aliases = { "--node-type" }, usage = "Type of the node. DEFAULT is DATA")
	private NodeType nodeType = NodeType.DATA;

	@Option(name = "-cf", aliases = { "--config-file" }, usage = "Path or URL to BEEN config file.")
	private String configFile;

	@Option(name = "-r", aliases = { "--host-runtime" }, usage = "Whether to run Host runtime on this node")
	private boolean runHostRuntime = false;

	@Option(name = "-sw", aliases = { "--software-repository" }, usage = "Whether to run Software Repository on this node.")
	private boolean runSWRepository = false;

	@Option(name = "-rr", aliases = { "--repository" }, usage = "Whether to run Repository on this node. Requires a running matching persistence layer.")
	private boolean runRepository = false;

	@Option(name = "-h", aliases = { "--help" }, usage = "Prints help")
	private boolean printHelp = false;

	/**
	 * An ID of this BEEN running JVM
	 */
	private UUID runtimeId = null;

	/**
	 * ID of the Host Runtime service running on this node. If no HR is running,
	 * will remain <code>null</code>
	 */
	private String hostRuntimeId = null;

	/**
	 * Synthetic ID of this BEEN node. Will always be non-<code>null</code> once
	 * this BEEN node is initialized
	 */
	private String beenId = null;

	private ClusterContext clusterContext;

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
			EX_USAGE.sysExit();
		}

		this.runtimeId = UUID.randomUUID();
		try {
			this.beenId = InetAddress.getLocalHost().getHostName() + "--" + runtimeId.toString();
		} catch (UnknownHostException e) {
			log.error("Cannot determine local hostname, will terminate.", e);
			EX_NETWORK_ERROR.sysExit();
		}

		Properties properties = loadProperties();

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
		this.clusterContext = Instance.createContext();
		registerServiceCleaner();
		try {
			// standalone services
			if (runSWRepository) {
				clusterReaper.pushTarget(startSWRepository());
			}

			// Run Task Manager on DATA nodes
			if (nodeType == NodeType.DATA) {
				clusterReaper.pushTarget(startTaskManager());
			}

			if (runHostRuntime) {
				clusterReaper.pushTarget(startHostRuntime(clusterContext, properties));
			}

			clusterReaper.pushTarget(startLogPersister());

			// Services that require a persistence layer
			if (runRepository) {
				final Storage storage = StorageBuilderFactory.createBuilder(properties).build();
				clusterReaper.pushTarget(startRepository(storage));
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

		clusterReaper.pushTarget(this);

		Runtime.getRuntime().addShutdownHook(clusterReaper);
	}

	private void registerServiceCleaner() {
		// when member is removed from cluster, remove its services immediately
		clusterContext.getCluster().addMembershipListener(new ServiceCleaner(clusterContext));
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
	 *          Command line arguments
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

	private IClusterService startTaskManager() throws ServiceException {
		IClusterService taskManager = Managers.getManager(clusterContext);
		taskManager.start();
		return taskManager;
	}

	private IClusterService startHostRuntime(ClusterContext context, Properties properties) throws ServiceException {
		HostRuntime hostRuntime = HostRuntimes.createRuntime(context, properties);
		hostRuntime.start();
		this.hostRuntimeId = hostRuntime.getId();
		return hostRuntime;
	}

	private IClusterService startSWRepository() throws ServiceException {
		SoftwareRepository softwareRepository = SoftwareRepositories.createSWRepository(clusterContext, beenId);
		softwareRepository.init();
		softwareRepository.start();
		return softwareRepository;
	}

	private IClusterService startLogPersister() throws ServiceException {
		ServiceLogPersister logPersister = ServiceLogPersister.getHandlerInstance(clusterContext, beenId, hostRuntimeId);
		logPersister.start();
		return logPersister;
	}

	private IClusterService startRepository(Storage storage) throws ServiceException {
		Repository repository = Repository.create(clusterContext, storage);
		repository.start();
		return repository;
	}

	private HazelcastInstance getInstance(final NodeType nodeType, Properties properties) throws ServiceException {
		Instance.init(nodeType, properties);
		return Instance.getInstance();
	}

	private Properties loadProperties() {

		if (configFile == null || configFile.isEmpty()) {
			log.info("No config file or url specified. Will start with default configuration.");
			return new Properties();
		}

		PropertyLoader loader = null;

		// try as a file
		try {
			Path path = Paths.get(configFile);
			if (Files.exists(path)) {
				loader = PropertyLoader.fromPath(path);
			}
		} catch (InvalidPathException e) {
			// quell
		}

		// try as an URL
		if (loader == null) {
			try {
				URL url = new URL(configFile);
				loader = PropertyLoader.fromUrl(url);
			} catch (MalformedURLException e) {
				// quell
			}
		}

		if (loader == null) {
			log.error("{} is not a file nor an URL. Aborting.", configFile);
			EX_USAGE.sysExit();
			throw new AssertionError(); // make the compiler happy
		}

		try {
			Properties properties = loader.load();
			log.info("Configuration loaded from {}", configFile);
			return properties;
		} catch (IOException e) {
			String msg = String.format("Cannot load properties from %s. Aborting.", configFile);
			log.error(msg, e);
			EX_USAGE.sysExit();
		}

		throw new AssertionError(); // will not get here, make the compiler happy
	}

	@Override
	public Reaper createReaper() {
		return new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				clusterContext.stop();
			}
		};
	}
}
