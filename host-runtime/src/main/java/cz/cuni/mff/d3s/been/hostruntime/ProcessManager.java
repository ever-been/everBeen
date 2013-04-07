package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.hostruntime.proc.JavaBasedProcess;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.*;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.Tasks;
import cz.cuni.mff.d3s.been.core.TaskPropertyNames;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.hostruntime.proc.Processes;
import cz.cuni.mff.d3s.been.hostruntime.proc.TaskProcess;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * 
 * Manages all Host Runtime's task processes.
 * 
 * All good names taken, so 'Process' is used.
 * 
 * @author Martin Sixta
 * @author Tadeáš Palusga
 */
final class ProcessManager {

	/** Logger */
	private static final Logger log = LoggerFactory.getLogger(ProcessManager.class);

	/**
	 * Represents running tasks.
	 */
	private final Map<String, Process> runningTasks = Collections.synchronizedMap(new HashMap<String, Process>());

	/**
	 * Host Runtime info
	 */
	private RuntimeInfo hostInfo;

	/**
	 * Connection to the cluster.
	 */
	private ClusterContext clusterContext;

	/**
	 * Manages software resources.
	 */
	private SoftwareResolver softwareResolver;

	/**
	 * Shortcut to task cluster context.
	 */
	private Tasks tasks;

	/**
	 * Listens and takes care of task messages.
	 */
	private TaskMessageDispatcher taskMessageDispatcher;

	/**
	 * Creates new instance.
	 * 
	 * Call {@link #start()} to fire it up, {@link #stop()} to get rid of it.
	 * 
	 * @param clusterContext
	 * @param swRepoClientFactory
	 * @param hostInfo
	 */
	ProcessManager(ClusterContext clusterContext, SwRepoClientFactory swRepoClientFactory, RuntimeInfo hostInfo) {
		this.clusterContext = clusterContext;
		this.hostInfo = hostInfo;
		this.softwareResolver = new SoftwareResolver(clusterContext.getServicesUtils(), swRepoClientFactory);
		this.tasks = clusterContext.getTasksUtils();
	}

	/**
	 * Starts processing messages and tasks.
	 */
	public void start() {
		registerTaskMessageDispatcher();
	}

	/**
	 * Stops processing, kills all remaining running processes
	 */
	public void stop() {
		// TODO this should kill/flush all remaining processes
		unregisterTaskMessageDispatcher();
	}

	/**
	 * This method should be called from outside when task should be started.
	 * 
	 * @param message
	 */
	void onRunTask(RunTaskMessage message) {
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
	 * TODO should not be here
	 * 
	 * @return node identifier
	 */
	public String getNodeId() {
		return hostInfo.getId();
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

			DebugAssistant dbg = new DebugAssistant(clusterContext);
			dbg.removeSuspendedTask(taskHandle.getId());

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
			File taskDirectory) throws IOException, BpkConfigurationException, ZipException, TaskException {

		TaskDescriptor td = taskEntry.getTaskDescriptor();

		// obtain bpk
		Bpk bpk = softwareResolver.getBpk(td);

		// unzip to task dir
		ZipFileUtil.unzipToDir(bpk.getInputStream(), taskDirectory);

		// obtain bpk configuration
		Path dir = Paths.get(taskDirectory.toString());
		Path configPath = dir.resolve(PackageNames.CONFIG_FILE); // TODO use bpk convetions
		BpkConfiguration bpkConfiguration = BpkConfigUtils.fromXml(configPath);

		// create process for the task
		TaskProcess taskProcess = Processes.createProcess(bpkConfiguration.getRuntime(), td, dir);

		// TODO resolve dependencies
		//Collection<ArtifactIdentifier> identifiers = taskProcess.getArtifactDependencies();
		//Collection<Artifact> artifacts = softwareResolver.resolveArtifacts(identifiers);

		// TODO move dependencies inside task's directory

		// start the thing
		CommandLine cmdLine = taskProcess.createCommandLine();
		ProcessBuilder processBuilder = new ProcessBuilder(cmdLine.toStrings());
		processBuilder.inheritIO();
		processBuilder.directory(dir.toFile());

		processBuilder.environment().putAll(createEnvironmentProperties(taskEntry));

		// let debug assistant know about this process
		if (taskProcess instanceof JavaBasedProcess) {
			JavaBasedProcess jbp = ((JavaBasedProcess) taskProcess);
			if (jbp.isDebugListeningMode()) {
				DebugAssistant dbg = new DebugAssistant(clusterContext);
				dbg.addSuspendedTask(taskEntry.getId(), clusterContext.getInetSocketAddress().getHostName(), jbp.getDebugPort());
			}
		}

		// run it
		Process process = processBuilder.start();

		return process;
	}

	private Map<String, String> createEnvironmentProperties(TaskEntry taskEntry) {
		Map<String, String> properties = new HashMap<>();
		properties.put(TaskPropertyNames.TASK_ID, taskEntry.getId());
		properties.put(TaskPropertyNames.HR_COMM_PORT, Integer.toString(taskMessageDispatcher.getReceiverPort()));
		return properties;
	}

	private File createTaskDir(TaskEntry taskEntry) {
		String taskDirName = taskEntry.getTaskDescriptor().getName() + "_" + new Date().getTime();
		File taskDir = new File(hostInfo.getWorkingDirectory(), taskDirName);
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

	private TaskEntry loadTask(String taskId) {
		return tasks.getTask(taskId);
	}

	private void changeTaskStateTo(TaskEntry taskEntry, TaskState state) {
		String logMsgTemplate = "State of task '%s' has been changed to '%s'.";
		log.info(String.format(logMsgTemplate, taskEntry.getId(), state));
		tasks.updateTaskState(taskEntry, state, logMsgTemplate, taskEntry.getId(), getNodeId());
	}

	private void registerTaskMessageDispatcher() {
		taskMessageDispatcher = new TaskMessageDispatcher();
		taskMessageDispatcher.start();
	}

	private void unregisterTaskMessageDispatcher() {
		taskMessageDispatcher.terminate();
	}

}
