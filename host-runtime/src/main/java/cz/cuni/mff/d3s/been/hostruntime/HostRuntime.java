package cz.cuni.mff.d3s.been.hostruntime;

import static cz.cuni.mff.d3s.been.cluster.Names.ACTION_QUEUE_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.TaskPropertyNames;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.detectors.Monitoring;
import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * 
 * This is the main implementation of Host Runtime.
 * 
 * Host runtime is responsible for launching new tasks triggered by appropriate
 * message. It is also responsible for operating with already running tasks on
 * parent machine. Operation are as follows: killing tasks, allowing and
 * supporting communication between tasks and results repository, allowing
 * logging).
 * 
 * @author Tadeáš Palusga
 * 
 */
public final class HostRuntime implements IClusterService {

	private static final Logger log = LoggerFactory.getLogger(HostRuntime.class);

	/**
	 * Stores basic information (name, id, host, port, OS, memory, Java) about
	 * this {@link HostRuntime} instance.
	 */
	private final RuntimeInfo hostRuntimeInfo;

	/**
	 * Picks up relevant messages from Hazelcast and queues them for internal
	 * processing.
	 */
	private HostRuntimeMessageListener messageListener;

	/**
	 * Factory for creating {@link SwRepoClient} instances from real-time obtained
	 * IP:port
	 */
	private final SwRepoClientFactory swRepoClientFactory;

	/**
	 * Grants access to all instantiated cluster-dependent utils.
	 */
	private final ClusterContext clusterContext;

	/**
	 * Takes care of task's processes.
	 */
	private ProcessManager processManager;

	/**
	 * Message Queues manager.
	 */
	private final MessageQueues messageQueues;

	/**
	 * Name of the resource with the logger.py
	 */
	private static final String LOGGER_RESOURCE_NAME = "scripts/logger.py";

	/**
	 * Monitoring object.
	 */
	private Monitoring monitoring;

	/**
	 * Creates new {@link HostRuntime} with cluster-unique id.
	 * 
	 * @param clusterContext
	 *          Grants access to all instantiated cluster-dependent utils.
	 * @param swRepoClientFactory
	 *          factory for creating {@link SwRepoClient} instances from real-time
	 *          obtained IP and port
	 * @param hostRuntimeInfo
	 *          object which stores basic information about HostRuntime
	 */
	HostRuntime(ClusterContext clusterContext, SwRepoClientFactory swRepoClientFactory, RuntimeInfo hostRuntimeInfo) {
		this.clusterContext = clusterContext;
		this.hostRuntimeInfo = hostRuntimeInfo;
		this.swRepoClientFactory = swRepoClientFactory;
		this.messageQueues = MessageQueues.getInstance();
	}

	/**
	 * Starts this {@link HostRuntime}. Registers all listeners and register
	 * itself in cluster.
	 */
	@Override
	public void start() throws ServiceException {
		log.info("Starting Host Runtime...");
		try {
			// creates necessary files and directories
			prepareFiles(hostRuntimeInfo.getWorkingDirectory(), hostRuntimeInfo.getTasksWorkingDirectory());

			startProcessManager();

			// All listeners must be initialized before any message will be
			// received.
			startListeners();

			// Now, we can register the runtime without missing any messages.
			registerHostRuntime();

			// HR is now prepared to consume all important messages.
			startMonitoring();

		} catch (Exception e) {
			throw new ServiceException("Cannot start Host Runtime", e);
		}
		log.info("Host Runtime started.");
	}

	/**
	 * Get the ID of the Host Runtime instance
	 * 
	 * @return The Host Runtime ID
	 */
	public String getId() {
		return hostRuntimeInfo.getId();
	}

	private void startMonitoring() {
		Path monitoringLogPath = FileSystems.getDefault().getPath(hostRuntimeInfo.getWorkingDirectory(), "monitoring.log");

		monitoring = new Monitoring(clusterContext.getProperties());
		try {
			monitoring.addListener(ResendMonitoringListener.create(MessageQueues.getInstance().createSender(ACTION_QUEUE_NAME)));
			monitoring.addListener(new PersistMonitoringListener(clusterContext, this));
		} catch (MessagingException e) {
			throw new RuntimeException("Cannot request message.", e);
		}

		monitoring.startMonitoring(monitoringLogPath);
	}

	private void prepareFiles(String workingDirName, String tasksWorkingDirName) throws IOException {
		Path workingDir = Paths.get(workingDirName).toAbsolutePath();
		Files.createDirectories(workingDir);

		Path tasksWorkingDir = Paths.get(tasksWorkingDirName).toAbsolutePath();
		Files.createDirectories(tasksWorkingDir);

		extractLogger(workingDir);
	}

	/**
	 * Extracts logger and exports logger property setting
	 * 
	 * @param workingDir
	 */
	private void extractLogger(Path workingDir) {

		InputStream input = HostRuntime.class.getClassLoader().getResourceAsStream(LOGGER_RESOURCE_NAME);
		try {
			Path scriptDir = workingDir.resolve("scripts");
			Path resourcePath = workingDir.resolve(LOGGER_RESOURCE_NAME);
			Files.createDirectories(scriptDir);
			Files.deleteIfExists(resourcePath);
			Files.copy(input, resourcePath);
			System.setProperty(TaskPropertyNames.LOGGER, resourcePath.toString());
		} catch (IOException e) {
			String msg = String.format("Cannot extract %s. Native task logging will not work", LOGGER_RESOURCE_NAME);
			log.error(msg, e);
		}

	}
	/**
	 * Causes clean Host Runtime shutdown.
	 */
	@Override
	public void stop() {
		log.info("Stopping Host Runtime...");
		monitoring.stopMonitoring();
		unregisterHostRuntime();
		stopListeners();
		stopProcessManager();
		log.info("Host Runtime stopped.");
	}

	@Override
	public Reaper createReaper() {
		return new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				HostRuntime.this.stop();
			}
		};
	}
	/**
	 * Starts process manger.
	 */
	private void startProcessManager() throws ServiceException {
		try {
			IMessageQueue<BaseMessage> queue = messageQueues.createInprocQueue(ACTION_QUEUE_NAME);
			queue.getReceiver(); //binds receiver

			processManager = new ProcessManager(clusterContext, swRepoClientFactory, hostRuntimeInfo);
			processManager.start();
		} catch (MessagingException e) {
			String msg = String.format("Cannot start %s queue", ACTION_QUEUE_NAME);
			throw new ServiceException(msg, e);
		}

	}

	/**
	 * Stops process manager.
	 */
	private void stopProcessManager() {
		log.debug("Stopping process manager...");
		processManager.stop();
		processManager = null;
		try {
			messageQueues.terminate(ACTION_QUEUE_NAME);
		} catch (MessagingException e) {
			String msg = String.format("Cannot terminate %s", ACTION_QUEUE_NAME);
			log.error(msg, e);
		}
		log.debug("Process manager stopped.");
	}

	/**
	 * Stops all cluster listeners.
	 */
	private void stopListeners() {
		messageListener.stop();
		messageListener = null;
	}

	/**
	 * Registers all needed cluster listeners.
	 */
	private void startListeners() throws ServiceException {
		messageListener = new HostRuntimeMessageListener(clusterContext, processManager.getNodeId());
		messageListener.start();
	}

	/**
	 * Stores {@link RuntimeInfo} (created in constructor) in cluster.
	 */
	private void registerHostRuntime() {
		clusterContext.getRuntimes().storeRuntimeInfo(hostRuntimeInfo);
	}

	/**
	 * Removes {@link RuntimeInfo} (created in constructor) from cluster.
	 */
	private void unregisterHostRuntime() {
		try {
			clusterContext.getRuntimes().removeRuntimeInfo(hostRuntimeInfo.getId());
		} catch (IllegalStateException e) {
			// an attempt is made to unregister on a cluster instance that is no longer active
			// this happens when Hazelcast shutdown hook snags runtime control before BEEN shutdown hooks
			log.warn("Failed to unhook HostRuntime from the cluster. HostRuntime data is likely to linger.", e);
		}
	}

}
