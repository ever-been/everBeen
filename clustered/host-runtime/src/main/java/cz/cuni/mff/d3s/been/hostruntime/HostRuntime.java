package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import org.apache.commons.io.FileUtils;

import cz.cuni.mff.d3s.been.bpk.BpkConfiguration;
import cz.cuni.mff.d3s.been.bpk.BpkDependencies;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkResolver;
import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.JSONUtils.JSONSerializerException;
import cz.cuni.mff.d3s.been.core.RuntimeInfoUtils;
import cz.cuni.mff.d3s.been.core.RuntimesUtils;
import cz.cuni.mff.d3s.been.core.ServicesUtils;
import cz.cuni.mff.d3s.been.core.TasksUtils;
import cz.cuni.mff.d3s.been.core.TopicUtils;
import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskFinishedMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskKilledMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskStartedMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * 
 * @author Tadeáš Palusga
 * 
 */
final class HostRuntime implements IClusterService {

	/**
	 * Stores basic information (name, id, host, port, OS, memory, Java) about
	 * this {@link HostRuntime} instance.
	 */
	private final RuntimeInfo hostRuntimeInfo;

	private HostRuntimeMessageListener messageListener;

	private RestBridgeListener restBridgeListener;

	/**
	 * Creates new {@link HostRuntime} with cluster-unique id.
	 * 
	 * @param nodeId
	 *            cluster-unique id of {@link HostRuntime} node
	 */
	public HostRuntime(String nodeId) {
		this.hostRuntimeInfo = RuntimeInfoUtils.newInfo(nodeId);
	}

	/**
	 * Starts this {@link HostRuntime}. Registers all listeners and register
	 * itself in cluster.
	 */
	@Override
	public void start() {
		// All listeners must be initialized before any message will be
		// received.
		registerListeners();

		// Now, we can register the runtime without missing any messages.
		registerHostRuntime();

		// HR is now prepared to consume all important messages.
	}

	@Override
	public void stop() {
		// Runtime must be unregistered first
		unregisterHostRuntime();

		// Now, no new message should be received and we can unregister
		// listeners
		unregisterListeners();
	}

	private void unregisterListeners() {
		restBridgeListener.stop();
		messageListener.stop();
	}

	private void registerListeners() {
		messageListener = new HostRuntimeMessageListener(this);
		restBridgeListener = new RestBridgeListener();

		messageListener.start();

		// TODO: unlike messageListener, here is race condition. But do we care?
		// FIXME: Martin Sixta: please make your comments more descriptive -
		// could
		// you describe the race condition you mentioned please?
		restBridgeListener.start();
	}

	/**
	 * Stores {@link RuntimeInfo} (created in constructor) in cluster.
	 */
	private void registerHostRuntime() {
		RuntimesUtils.storeRuntimeInfo(hostRuntimeInfo);
	}

	/**
	 * Removes {@link RuntimeInfo} (created in constructor) from cluster.
	 */
	private void unregisterHostRuntime() {
		RuntimesUtils.removeRuntimeInfo(hostRuntimeInfo.getId());
	}

	void sendTaskStartedMessage(TaskStartedMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	void sendTaskFinishedMessage(TaskFinishedMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	void sendTaskKilledMessage(TaskKilledMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	synchronized void onRunTask(RunTaskMessage message) {
		tryRunTask(message);
	}

	synchronized void onKillTask(KillTaskMessage message) {
		killTask(message);
	}

	public String getNodeId() {
		return hostRuntimeInfo.getId();
	}

	private void sendMessage(final BaseMessage message) {
		TopicUtils.publish(Context.GLOBAL_TOPIC.getName(), message);
	}

	// /**
	// * SHOULD BE THREAD SAFE TODO verify if it is true :)
	// */
	// private final Map<String, Process> runningTasks = new
	// ConcurrentHashMap<>();

	public final boolean tryRunTask(final RunTaskMessage runTaskMessage) {
		// THIS IS NOT REAL IMPLEMENTATION YET ..
		String taskId = runTaskMessage.taskId;
		TaskEntry task = TasksUtils.getTask(taskId);
		TaskEntries.setState(task, TaskState.ACCEPTED, "Task will be run");
		try {


			// FIXME main block ot this method should be in one big try-catch block
			// FIXME do not forget to update state later

			TaskDescriptor descriptor = task.getTaskDescriptor();

			SWRepositoryInfo swRepositoryInfo = ServicesUtils.getSWRepositoryInfo();
			String httpHost = swRepositoryInfo.getHost();
			int httpPort = swRepositoryInfo.getHttpServerPort();

			// FIXME Radek Macha, Tadeas Palusga: Radek, where is the library
			// for
			// downloading packages from Software Repository you mentioned?

			// FIXME Temporary solution ......................
			String baseAddress = httpHost + ":" + httpPort + "/";

			File bpkFile = null;
			// FIXME Temporary solution ... do not use tmp folders and files
			try {
				bpkFile = File.createTempFile(descriptor.getName(), ".bpk");
				FileUtils.copyURLToFile(new URL(baseAddress + "bpk/" + descriptor.getName()), bpkFile);
			} catch (Exception e) {
				// TODO: handle exception
				// return or rethrow .......
			}

			BpkConfiguration resolvedConfiguration = null;
			try {
				// bpkFile should exist here
				resolvedConfiguration = BpkResolver.resolve(bpkFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			BpkDependencies dependencies = resolvedConfiguration.getBpkDependencies();
			List<File> downloadedDependencies = new ArrayList<>();
			for (BpkIdentifier dependency : dependencies.getDependency()) {
				// FIXME Radek Macha: What is "bpkId" field on BpkDependency?
				// where is the name of the BpkDependency?
				File dependencyFile;
				try {
					dependencyFile = File.createTempFile(dependency.getBpkId(), ".bpk");
					FileUtils.copyURLToFile(
							new URL(baseAddress + "bpk/" + dependency.getBpkId() + "/" + dependency.getGroupId() + "/" + dependency.getVersion()),
							dependencyFile);
					downloadedDependencies.add(dependencyFile);
				} catch (IOException e) {
					// should rethrow or return on first erro
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (Throwable e) {
			TaskEntries.setState(task, TaskState.ABORTED, e.getMessage());
			// TODO: handle exception
		}
		TaskEntries.setState(task, TaskState.FINISHED, "Task has finished normally");

		// if (runningTasks.containsKey(runTaskMessage.name)) {
		// // already running ... FIXME
		// return false;
		// }
		//
		// Thread thread = new Thread() {
		// public void run() {
		// try {
		// // 0. add tasks in node info
		//
		// // extract files from bpk
		// // determine task type (java/python/shell)
		// // determine main class and dependencies
		// // download dependencies
		// // determine classpath from downloaded deps
		// // prepare JVM args
		// // prepare TASK args
		//
		// // prepare log interface in separate thread (rest hazelcast
		// // queue on exact ip:port)
		//
		// // ... HOW TO DO THIS WITHOUT DEPENDENCY ON HC?
		//
		// // ATOMIC OP START
		// /*
		// * 1. set running tasks in node info
		// *
		// * 2. start task in apache executor wrapped in special
		// * thread ... define retry count and timeouts
		// *
		// * 3. after task finish, remove from running tasks in node
		// * info
		// *
		// * 4. send task finished info
		// */
		// // ATOMIC OP END
		//
		// String pName = new String(runTaskMessage.name);
		// Process p = Runtime.getRuntime().exec("sleep 1000");
		// runningTasks.put(pName, p);
		// Thread taskShutdownHook = createTaskShutdownHook(p);
		// Runtime.getRuntime().addShutdownHook(taskShutdownHook);
		// p.waitFor();
		// Runtime.getRuntime().removeShutdownHook(taskShutdownHook);
		// runningTasks.remove(pName);
		// } catch (IOException | InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		//
		// private Thread createTaskShutdownHook(final Process p) {
		// return new Thread() {
		// @Override
		// public void run() {
		// p.destroy();
		// }
		// };
		// }
		// };
		// thread.start();
		// return true;
		return true;
	}

	public final void killTask(final KillTaskMessage message) {
		// Process p = runningTasks.get(message.taskName);
		// try {
		// p.destroy();
		// } catch (Exception e) {
		// // TODO: handle exception
		// e.printStackTrace();
		// }
		// runningTasks.remove(message.taskName);
	}

}
