package cz.cuni.mff.d3s.been.hostruntime;

import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.HR_COMM_PORT;
import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.HR_RESULTS_PORT;
import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.TASK_ID;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkConfigUtils;
import cz.cuni.mff.d3s.been.bpk.BpkConfiguration;
import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkNames;
import cz.cuni.mff.d3s.been.cluster.Reapable;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.Tasks;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.hostruntime.proc.JavaBasedProcess;
import cz.cuni.mff.d3s.been.hostruntime.proc.Processes;
import cz.cuni.mff.d3s.been.hostruntime.proc.TaskProcess;
import cz.cuni.mff.d3s.been.mq.MessagingException;
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
final class ProcessManager implements Service, Reapable {

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
	 * Threading service
	 */
	private ExecutorService executorService;

	/**
	 * Listens and takes care of task messages.
	 */
	private TaskMessageDispatcher taskMessageDispatcher;

	/**
	 * Collect results from tasks and dispatch them
	 */
	private ResultsDispatcher resultsDispatcher;

	/**
	 * Creates new instance.
	 * 
	 * Call {@link #start()} to fire it up, {@link #stop()} to get rid of it.
	 * 
	 * @param clusterContext
	 * @param swRepoClientFactory
	 * @param hostInfo
	 */
	ProcessManager(
			ClusterContext clusterContext,
			SwRepoClientFactory swRepoClientFactory,
			RuntimeInfo hostInfo) {
		this.clusterContext = clusterContext;
		this.hostInfo = hostInfo;
		this.softwareResolver = new SoftwareResolver(clusterContext.getServicesUtils(), swRepoClientFactory);
		this.tasks = clusterContext.getTasksUtils();
		this.executorService = Executors.newFixedThreadPool(1);
	}

	/**
	 * Starts processing messages and tasks.
	 */
	@Override
	public void start() throws ServiceException {
		registerTaskMessageDispatcher();
		registerResultsDispatcher();
	}

	/**
	 * Stops processing, kills all remaining running processes
	 */
	@Override
	public void stop() {
		unregisterResultsDispatcher();
		unregisterTaskMessageDispatcher();
	}

	@Override
	public Reaper createReaper() {
		final Reaper reaper = new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				resultsDispatcher.interrupt();
				executorService.shutdown();
				executorService.awaitTermination(300, TimeUnit.MILLISECONDS);
			}
		};
		reaper.pushTarget(taskMessageDispatcher);
		return reaper;
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
			log.error(String.format(
					"Task '%s' has been aborted due to underlying exception.",
					taskHandle.getId()), e);
		} finally {
			runningTasks.remove(taskHandle.getId());
		}
		deleteTaskDir(taskDirectory);
	}

	private Process createAndStartTaskProcess(
			TaskEntry taskEntry,
			File taskDirectory) throws IOException, BpkConfigurationException, ZipException, TaskException {

		TaskDescriptor td = taskEntry.getTaskDescriptor();

		Bpk bpk = softwareResolver.getBpk(td);
		ZipFileUtil.unzipToDir(bpk.getInputStream(), taskDirectory);

		// obtain bpk configuration
		Path dir = Paths.get(taskDirectory.toString());
		Path configPath = dir.resolve(BpkNames.CONFIG_FILE); // TODO use bpk convetions
		BpkConfiguration bpkConfiguration = BpkConfigUtils.fromXml(configPath);

		// create process for the task
		TaskProcess taskProcess = Processes.createProcess(
				bpkConfiguration.getRuntime(),
				td,
				dir);

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
				dbg.addSuspendedTask(
						taskEntry.getId(),
						clusterContext.getInetSocketAddress().getHostName(),
						jbp.getDebugPort());
			}
		}

		// run it
		Process process = processBuilder.start();

		return process;
	}

	private Map<String, String> createEnvironmentProperties(TaskEntry taskEntry) {
		Map<String, String> properties = new HashMap<>();
		properties.put(TASK_ID, taskEntry.getId());
		properties.put(
				HR_COMM_PORT,
				Integer.toString(taskMessageDispatcher.getReceiverPort()));
		properties.put(
				HR_RESULTS_PORT,
				Integer.toString(resultsDispatcher.getPort()));
		return properties;
	}

	private File createTaskDir(TaskEntry taskEntry) {
		String taskDirName = taskEntry.getTaskDescriptor().getName() + "_"
				+ new Date().getTime();
		File taskDir = new File(hostInfo.getWorkingDirectory(), taskDirName);
		taskDir.mkdirs();
		return taskDir;
	}

	private void deleteTaskDir(File taskDirectory) {
		try {
			FileUtils.deleteDirectory(taskDirectory);
		} catch (IOException e) {
			log.warn(String.format(
					"Taks directory '%s' couldn't be deleted",
					taskDirectory), e);
		}
	}

	private TaskEntry loadTask(String taskId) {
		return tasks.getTask(taskId);
	}

	private void changeTaskStateTo(TaskEntry taskEntry, TaskState state) {
		String logMsgTemplate = "State of task '%s' has been changed to '%s'.";
		log.info(String.format(logMsgTemplate, taskEntry.getId(), state));
		tasks.updateTaskState(
				taskEntry,
				state,
				logMsgTemplate,
				taskEntry.getId(),
				getNodeId());
	}

	private void registerTaskMessageDispatcher() throws ServiceException {
		taskMessageDispatcher = new TaskMessageDispatcher();
		taskMessageDispatcher.start();
	}

	private void unregisterTaskMessageDispatcher() {
		log.debug("Stopping task message dispatcher...");
		taskMessageDispatcher.stop();
		log.debug("Result dispatcher stopped.");
	}

	private void registerResultsDispatcher() throws ServiceException {
		resultsDispatcher = new ResultsDispatcher(clusterContext, "localhost");
		try {
			resultsDispatcher.init();
		} catch (MessagingException e) {
			throw new ServiceException("Failed to register result dispatcher", e);
		}
		executorService.submit(resultsDispatcher);
	}
	private void unregisterResultsDispatcher() {
		log.debug("Stopping result dispatcher...");
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.warn(
					"Results dispatcher interrupted during shutdown sequence. Socket leaks are likely.",
					e);
		}
		log.debug("Result dispatcher stopped.");
	}
}
