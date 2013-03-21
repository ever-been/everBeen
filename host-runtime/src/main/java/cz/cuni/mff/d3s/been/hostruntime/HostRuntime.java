package cz.cuni.mff.d3s.been.hostruntime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * 
 * This is the main implementation of Host Runtime.
 * 
 * Host runtime is responsible for launching new tasks trigged by appropriate
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
	 * Translates relevant messages sent via Hazelcast into corresponding method
	 * calls.
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
	public void start() {
		startProcessManager();

		// All listeners must be initialized before any message will be
		// received.
		registerListeners();

		// Now, we can register the runtime without missing any messages.
		registerHostRuntime();

		// HR is now prepared to consume all important messages.
	}

	/**
	 * Causes clean hostruntime shutdown.
	 */
	@Override
	public void stop() {
		// Runtime must be unregistered first
		unregisterHostRuntime();

		// Now, no new message should be received and we can unregister
		// listeners
		unregisterListeners();

		// stops processes
		stopProcessManager();
	}

	/**
	 * Starts process manger.
	 */
	private void startProcessManager() {
		processManager = new ProcessManager(clusterContext, swRepoClientFactory, hostRuntimeInfo);
		processManager.start();
	}

	/**
	 * Stops process manager.
	 */
	private void stopProcessManager() {
		processManager.stop();
		processManager = null;
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
	private void registerListeners() {
		messageListener = new HostRuntimeMessageListener(processManager, clusterContext);
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
		clusterContext.getRuntimesUtils().removeRuntimeInfo(hostRuntimeInfo.getId());
	}
}
