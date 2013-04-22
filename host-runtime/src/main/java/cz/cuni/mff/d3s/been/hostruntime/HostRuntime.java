package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

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
import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.Messaging;
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
class HostRuntime implements IClusterService {

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
	 * Queue used to send task action messages for processing.
	 */
	IMessageQueue<BaseMessage> taskActionQueue;

	/**
	 * Name of the task action queue.
	 */

	private static final String ACTION_QUEUE_NAME = "been.hostruntime.actions";

	/**
	 * Name of the resource with the logger.py
	 */
	private static final String LOGGER_RESOURCE_NAME = "scripts/logger.py";

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
	public HostRuntime(ClusterContext clusterContext, SwRepoClientFactory swRepoClientFactory, RuntimeInfo hostRuntimeInfo) {
		this.clusterContext = clusterContext;
		this.hostRuntimeInfo = hostRuntimeInfo;
		this.swRepoClientFactory = swRepoClientFactory;
	}

	/**
	 * Starts this {@link HostRuntime}. Registers all listeners and register
	 * itself in cluster.
	 */
	@Override
	public void start() throws ServiceException {
		try {
			taskActionQueue = Messaging.createInprocQueue(ACTION_QUEUE_NAME);

			// create ".HostRuntime" working directory
			File workingDir = new File(hostRuntimeInfo.getWorkingDirectory());
			workingDir.mkdirs();

			extractLogger(workingDir);

			startProcessManager(taskActionQueue.getReceiver());

			// All listeners must be initialized before any message will be
			// received.
			registerListeners(taskActionQueue.createSender());

			// Now, we can register the runtime without missing any messages.
			registerHostRuntime();

			// HR is now prepared to consume all important messages.

			// start monitoring
			Path monitoringLogPath = FileSystems.getDefault().getPath(hostRuntimeInfo.getWorkingDirectory(), "monitoring.log");
			Monitoring.startMonitoring(monitoringLogPath);
		} catch (Exception e) {
			throw new ServiceException("Cannot start Host Runtime", e);
		}
	}

	/**
	 * Extracts logger and exports logger property setting
	 * 
	 * @param workingDir
	 */
	private void extractLogger(File workingDir) {

		InputStream input = HostRuntime.class.getClassLoader().getResourceAsStream(LOGGER_RESOURCE_NAME);
		try {
			Path scriptDir = workingDir.toPath().resolve("scripts").toAbsolutePath();
			Path resourcePath = workingDir.toPath().resolve(LOGGER_RESOURCE_NAME).toAbsolutePath();
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
	 * Causes clean hostruntime shutdown.
	 */
	@Override
	public void stop() {
		log.info("Stopping Host Runtime...");
		unregisterHostRuntime();
		unregisterListeners();
		stopProcessManager();
		taskActionQueue.terminate();
		log.info("Host Runtime stopped.");
	}

	@Override
	public Reaper createReaper() {
		final Reaper reaper = new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				unregisterHostRuntime();
				unregisterListeners();
				taskActionQueue.terminate();
			}
		};
		reaper.pushTarget(processManager);
		return reaper;
	}
	/**
	 * Starts process manger.
	 */
	private void startProcessManager(IMessageReceiver<BaseMessage> receiver) throws ServiceException {
		processManager = new ProcessManager(clusterContext, swRepoClientFactory, hostRuntimeInfo, receiver);
		processManager.start();
	}

	/**
	 * Stops process manager.
	 */
	private void stopProcessManager() {
		log.debug("Stopping process manager...");
		processManager.stop();
		processManager = null;
		log.debug("Process manager stopped.");
	}

	/**
	 * Unregisters all cluster listeners.
	 */
	private void unregisterListeners() {
		messageListener.stop();
	}

	/**
	 * Registers all needed cluster listeners.
	 */
	private void registerListeners(IMessageSender<BaseMessage> sender) {
		messageListener = new HostRuntimeMessageListener(clusterContext, sender, processManager.getNodeId());
		messageListener.start();
	}

	/**
	 * Stores {@link RuntimeInfo} (created in constructor) in cluster.
	 */
	private void registerHostRuntime() {
		clusterContext.getRuntimesUtils().storeRuntimeInfo(hostRuntimeInfo);
	}

	/**
	 * Removes {@link RuntimeInfo} (created in constructor) from cluster.
	 */
	private void unregisterHostRuntime() {
		try {
			clusterContext.getRuntimesUtils().removeRuntimeInfo(hostRuntimeInfo.getId());
		} catch (IllegalStateException e) {
			// an attempt is made to unregister on a cluster instance that is no longer active
			// this happens when Hazelcast shutdown hook snags runtime control before BEEN shutdown hooks
			log.warn("Failed to unhook HostRuntime from the cluster. HostRuntime data is likely to linger.", e);
		}
	}

}
