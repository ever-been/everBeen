package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkArtifact;
import cz.cuni.mff.d3s.been.bpk.BpkConfiguration;
import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkResolver;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;
import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.Tasks;
import cz.cuni.mff.d3s.been.core.TaskPropertyNames;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.core.task.Arguments;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

// FIXME logging

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

	private final Map<String, Process> runningTasks = Collections.synchronizedMap(new HashMap<String, Process>());

	private TaskMessageDispatcher taskMessageDispatcher;

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
		// We have to prepare TaskLogProcessor before any task can be run
		registerTaskMessageDispatcher();

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

		// Now, we can easily remove taskMessageDispatcher
		unregisterTaskMessageDispatcher();
	}

	private void unregisterTaskMessageDispatcher() {
		taskMessageDispatcher.terminate();
	}

	private void registerTaskMessageDispatcher() {
		taskMessageDispatcher = new TaskMessageDispatcher();
		taskMessageDispatcher.start();
	}

	private void unregisterListeners() {
		messageListener.stop();
	}

	private void registerListeners() {
		messageListener = new HostRuntimeMessageListener(this, clusterContext);
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

	/**
	 * This method should be called from outside when task should be started.
	 * 
	 * @param message
	 */
	synchronized void onRunTask(RunTaskMessage message) {
		loadAndRunTask(message);
	}

	/**
	 * This method should be called from outside when task should be killed.
	 * 
	 * @param message
	 */
	synchronized void onKillTask(KillTaskMessage message) {
		loadAndKillTask(message);
	}

	/**
	 * Returns cluster-wide identifier of this hostruntime node.
	 * 
	 * @return node identifier
	 */
	public String getNodeId() {
		return hostRuntimeInfo.getId();
	}

	/**
	 * This method is responsible for task starting.
	 * 
	 * @param message
	 */
	private void loadAndRunTask(RunTaskMessage message) {
		TaskEntry taskHandle = loadTask(message.taskId);
		if (taskHandle == null) {
			return;
		} else {
			runTask(taskHandle);
		}
	}

	private void runTask(TaskEntry taskHandle) {
		// FIXME tadeas
		changeTaskStateTo(taskHandle, TaskState.ACCEPTED);
		File taskDirectory = createTaskDir(taskHandle);
		try {
			Process process = createAndStartTaskProcess(taskHandle, taskDirectory);
			changeTaskStateTo(taskHandle, TaskState.RUNNING);
			// FIXME martin, tadeas zpracovavat return kody - (napr. TaskState.FAILED)
			runningTasks.put(taskHandle.getId(), process); // removed in finally

			// we do not care if this method takes too long, because this method should be called 
			// asynchronously from parental methods
			process.waitFor();
			changeTaskStateTo(taskHandle, TaskState.FINISHED);
		} catch (Exception e) {
			changeTaskStateTo(taskHandle, TaskState.ABORTED);
			log.error(String.format("Task '%s' has been aborted due to underlaying exception.", taskHandle.getId()), e);
		} finally {
			runningTasks.remove(taskHandle.getId());
		}
		deleteTaskDir(taskDirectory);
	}

	private Process createAndStartTaskProcess(TaskEntry taskEntry,
			File taskDirectory) throws IOException, BpkConfigurationException, ZipException, Exception {
		SwRepoClient swRepoClient = createSRClient();
		BpkIdentifier bpkIdentifier = createBpkIdentifier(taskEntry.getTaskDescriptor());
		Bpk bpk = downloadBpk(swRepoClient, bpkIdentifier);
		if (bpk == null) {
			throw new Exception(String.format("Missing bpk '%s:%s:%s' in software repository. ", bpkIdentifier.getGroupId(), bpkIdentifier.getBpkId(), bpkIdentifier.getVersion()));
		}
		BpkConfiguration bpkResolvedConfiguration = BpkResolver.resolve(bpk.getInputStream());

		List<String> additionalArgs = readTaskArguments(taskEntry);
		TaskRunOpts runOpts;

		if (isJavaTask(bpkResolvedConfiguration)) {
			JavaRuntime runtime = (JavaRuntime) bpkResolvedConfiguration.getRuntime();
			File bpkJarFile = unzipBpkJarFileTo(bpk, runtime, taskDirectory);
			List<File> dependencies = downloadJavaClasspathDependencies(swRepoClient, runtime);
			runOpts = new JavaTaskRunOpts(taskDirectory, bpkJarFile, dependencies, additionalArgs);
		} else {
			throw new Exception("Unsupported runtime type");
		}

		ProcessBuilder processBuilder = new ProcessBuilder(runOpts.createCommandLine());
		processBuilder.inheritIO();
		processBuilder.environment().putAll(createEnvironmentProperties(taskEntry));
		Process process = processBuilder.start();
		return process;
	}

	private Map<String, String> createEnvironmentProperties(TaskEntry taskEntry) {
		Map<String, String> properties = new HashMap<>();
		properties.put(TaskPropertyNames.TASK_ID, taskEntry.getId());
		properties.put(TaskPropertyNames.HR_COMM_PORT, Integer.toString(taskMessageDispatcher.getReceiverPort()));
		return properties;
	}

	private File unzipBpkJarFileTo(Bpk bpk, JavaRuntime runtime,
			File taskDirectory) throws ZipException, IOException {
		File unzippedDir = new File(taskDirectory, "bpk");
		ZipFileUtil.unzipToDir(bpk.getFile(), unzippedDir);
		return new File(new File(unzippedDir, "files"), runtime.getJarFile());
	}

	private File createTaskDir(TaskEntry taskEntry) {
		String taskDirName = taskEntry.getTaskDescriptor().getName() + "_" + new Date().getTime();
		File taskDir = new File(hostRuntimeInfo.getWorkingDirectory(), taskDirName);
		taskDir.mkdirs();
		return taskDir;
	}

	private void deleteTaskDir(File taskDirectory) {
		try {
			FileUtils.deleteDirectory(taskDirectory);
		} catch (IOException e) {
			log.warn(String.format("Taks directory '%s' couldn't be deleted", taskDirectory), e);
		}
	}

	private List<String> readTaskArguments(TaskEntry taskEntry) {
		Arguments argument = taskEntry.getTaskDescriptor().getArguments();
		return (argument != null && argument.getArgument() != null)
				? argument.getArgument() : Collections.<String> emptyList();
	}

	private List<File> downloadJavaClasspathDependencies(SwRepoClient client,
			JavaRuntime runtime) {
		List<File> dependencies = new ArrayList<>();
		for (BpkArtifact bpkArtifact : runtime.getBpkArtifacts().getArtifact()) {
			Artifact artifact = client.getArtifact(bpkArtifact.getGroupId(), bpkArtifact.getArtifactId(), bpkArtifact.getVersion());
			dependencies.add(artifact.getFile());
		}
		return dependencies;
	}

	private boolean isJavaTask(BpkConfiguration bpkResolvedConfiguration) {
		return bpkResolvedConfiguration.getRuntime() instanceof JavaRuntime;
	}

	private Bpk downloadBpk(SwRepoClient sRClient, BpkIdentifier bpkIdentifier) {
		return sRClient.getBpk(bpkIdentifier);
	}

	private synchronized TaskEntry loadTask(String taskId) {
		return getTaskUtils().getTask(taskId);
	}

	private void changeTaskStateTo(TaskEntry taskEntry, TaskState state) {
		String logMsgTemplate = "State of task '%s' has been changed to '%s'.";
		log.info(String.format(logMsgTemplate, taskEntry.getId(), state));
		getTaskUtils().updateTaskState(taskEntry, state, logMsgTemplate, taskEntry.getId(), getNodeId());
	}

	private Tasks getTaskUtils() {
		return clusterContext.getTasksUtils();
	}

	private BpkIdentifier createBpkIdentifier(TaskDescriptor descriptor) {
		BpkIdentifier bpkMetaInfo = new BpkIdentifier();
		bpkMetaInfo.setGroupId(descriptor.getGroupId());
		bpkMetaInfo.setBpkId(descriptor.getBpkId());
		bpkMetaInfo.setVersion(descriptor.getVersion());
		return bpkMetaInfo;
	}

	private SwRepoClient createSRClient() {
		SWRepositoryInfo swRepositoryInfo = clusterContext.getServicesUtils().getSWRepositoryInfo();

		String host = swRepositoryInfo.getHost();
		int port = swRepositoryInfo.getHttpServerPort();

		SwRepoClient swRepoClient = swRepoClientFactory.getClient(host, port);
		return swRepoClient;
	}

	private void loadAndKillTask(KillTaskMessage message) {
		TaskEntry taskHandle = loadTask(message.taskId);
		if (taskHandle == null) {
			return;
		} else {
			Process taskProcess = runningTasks.get(taskHandle.getId());
			taskProcess.destroy();
		}
	}
	static abstract class TaskRunOpts {
		protected File taskDirectory;
		protected List<String> additionalArgs;

		abstract String[] createCommandLine();
	}

	static final class JavaTaskRunOpts extends TaskRunOpts {
		File bpkJarFile;
		List<File> dependencies;

		JavaTaskRunOpts(File taskDirectory, File bpkFile, List<File> dependencies, List<String> additionalArgs) {
			this.taskDirectory = taskDirectory;
			this.bpkJarFile = bpkFile;
			this.dependencies = dependencies;
			this.additionalArgs = additionalArgs;
		}

		@Override
		String[] createCommandLine() {
			List<String> cmd = new ArrayList<>();

			cmd.add("java");
			cmd.add("-jar");
			cmd.add(bpkJarFile.getAbsolutePath());

			cmd.add("-cp");
			StringBuilder cp = new StringBuilder();
			for (File dep : dependencies) {
				cp.append(dep.getAbsolutePath());
			}
			cmd.add(cp.toString());

			for (String arg : additionalArgs) {
				cmd.add(arg);
			}

			return cmd.toArray(new String[cmd.size()]);
		}

	}
}
