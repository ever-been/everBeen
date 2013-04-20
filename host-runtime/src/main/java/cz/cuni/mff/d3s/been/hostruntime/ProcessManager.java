package cz.cuni.mff.d3s.been.hostruntime;

import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.*;

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

import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.*;
import cz.cuni.mff.d3s.been.cluster.Reapable;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.Tasks;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.Property;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.hostruntime.cmdline.CmdLineBuilderFactory;
import cz.cuni.mff.d3s.been.hostruntime.cmdline.TaskCommandLine;
import cz.cuni.mff.d3s.been.hostruntime.task.TaskProcess;
import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * Manages all Host Runtime's task processes.
 * <p/>
 * All good names taken, so 'Process' is used.
 * 
 * @author Martin Sixta
 * @author Tadeáš Palusga
 */
final class ProcessManager implements Service, Reapable {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(ProcessManager.class);

	/**
	 * Represents running tasks.
	 */
	private final Map<String, TaskProcess> runningTasks = Collections.synchronizedMap(new HashMap<String, TaskProcess>());

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
	 * Receiver of task action messages.
	 */
	private final IMessageReceiver<BaseMessage> receiver;

	/**
	 * Thread dispatching task action messages.
	 */
	TaskActionThread taskActionThread;

	/**
	 * Collect results from tasks and dispatch them
	 */
	private ResultsDispatcher resultsDispatcher;

	/**
	 * Creates new instance.
	 * <p/>
	 * Call {@link #start()} to fire it up, {@link #stop()} to get rid of it.
	 * 
	 * @param clusterContext
	 * @param swRepoClientFactory
	 * @param hostInfo
	 * @param receiver
	 */
	ProcessManager(ClusterContext clusterContext, SwRepoClientFactory swRepoClientFactory, RuntimeInfo hostInfo, IMessageReceiver<BaseMessage> receiver) {
		this.clusterContext = clusterContext;
		this.hostInfo = hostInfo;
		this.receiver = receiver;
		this.softwareResolver = new SoftwareResolver(clusterContext.getServicesUtils(), swRepoClientFactory);
		this.tasks = clusterContext.getTasksUtils();
		this.executorService = Executors.newFixedThreadPool(1);

	}

	private TaskRequestBrokerThread reqThread;
	/**
	 * Starts processing messages and tasks.
	 */
	@Override
	public void start() throws ServiceException {

		reqThread = new TaskRequestBrokerThread(clusterContext);

		reqThread.start();

		taskActionThread = new TaskActionThread(receiver);
		taskActionThread.start();

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
	 * <p/>
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
			TaskProcess taskProcess = runningTasks.get(taskHandle.getId());
			taskProcess.kill();
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

			int port = reqThread.getPort();

			log.info("Task Request socket listens on port {}", port);

			TaskProcess process = createAndStartTaskProcess(taskHandle, taskDirectory, port);
			changeTaskStateTo(taskHandle, TaskState.RUNNING);
			// FIXME martin, tadeas zpracovavat return kody - (napr. TaskState.FAILED)
			runningTasks.put(taskHandle.getId(), process); // removed in finally

			// we do not care if this method takes too long, because this method should be called
			// asynchronously from parental methods

			// spawn a new thread for the task, it might take a while
			process.start();

			DebugAssistant dbg = new DebugAssistant(clusterContext);
			dbg.removeSuspendedTask(taskHandle.getId());

			changeTaskStateTo(taskHandle, TaskState.FINISHED);
		} catch (Exception e) {
			changeTaskStateTo(taskHandle, TaskState.ABORTED);
			log.error(String.format("Task '%s' has been aborted due to underlying exception.", taskHandle.getId()), e);
		} finally {
			runningTasks.remove(taskHandle.getId());
		}
		deleteTaskDir(taskDirectory);
	}

	private TaskProcess createAndStartTaskProcess(TaskEntry taskEntry,
			File taskDirectory, int port) throws IOException, BpkConfigurationException, ZipException, TaskException {

		TaskDescriptor td = taskEntry.getTaskDescriptor();

		Bpk bpk = softwareResolver.getBpk(td);
		ZipFileUtil.unzipToDir(bpk.getInputStream(), taskDirectory);

		// obtain bpk configuration
		Path dir = Paths.get(taskDirectory.toString());
		Path configPath = dir.resolve(BpkNames.CONFIG_FILE); // TODO use bpk convetions
		BpkConfiguration bpkConfiguration = BpkConfigUtils.fromXml(configPath);

		// create process for the task
		TaskCommandLine cmd = CmdLineBuilderFactory.create(bpkConfiguration.getRuntime(), td, dir.toFile()).build();

		// TODO resolve dependencies
		//Collection<ArtifactIdentifier> identifiers = taskProcess.getArtifactDependencies();
		//Collection<Artifact> artifacts = softwareResolver.resolveArtifacts(identifiers);

		// TODO move dependencies inside task's directory

		// let debug assistant know about this process
		if (cmd.isDebugListeningMode()) {
			DebugAssistant dbg = new DebugAssistant(clusterContext);
			dbg.addSuspendedTask(taskEntry.getId(), clusterContext.getInetSocketAddress().getHostName(), cmd.getDebugPort());
		}

		ExecuteStreamHandler streamhandler = new PumpStreamHandler();

		long timeout = td.isSetFailurePolicy()
				? td.getFailurePolicy().getTimeoutRun() : TaskProcess.NO_TIMEOUT;
		TaskProcess taskProcess = new TaskProcess(cmd, dir.toFile(), createEnvironmentProperties(taskEntry, port), streamhandler, timeout); // FIXMEProcesses.createProcess(bpkConfiguration.getRuntime(), td, dir);
		// run it

		return taskProcess;
	}

	private Map<String, String> createEnvironmentProperties(TaskEntry taskEntry,
			int port) {

		Map<String, String> properties = new HashMap<>();
		properties.put("REQUEST_PORT", Integer.toString(port));
		properties.put(TASK_ID, taskEntry.getId());
		properties.put(HR_COMM_PORT, Integer.toString(taskMessageDispatcher.getReceiverPort()));
		properties.put(HR_RESULTS_PORT, Integer.toString(resultsDispatcher.getPort()));

		// add properties specified in TaskDescriptor
		TaskDescriptor td = taskEntry.getTaskDescriptor();
		if (td.isSetProperties() && td.getProperties().isSetProperty()) {
			for (Property property : td.getProperties().getProperty()) {
				properties.put(property.getName(), property.getValue());
			}
		}
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
			log.warn("Results dispatcher interrupted during shutdown sequence. Socket leaks are likely.", e);
		}
		log.debug("Result dispatcher stopped.");
	}

	/**
	 * Thread listening for task action messages. Dispatches messages to its
	 * handlers.
	 * <p/>
	 * The thread is an inner class for easy access to the ProcessManager.
	 */
	private class TaskActionThread extends Thread {
		private final IMessageReceiver<BaseMessage> receiver;

		private final Logger log = LoggerFactory.getLogger(TaskActionThread.class);

		// TODO use ExecutorService for task handling, take care of proper service shutdown

		TaskActionThread(IMessageReceiver<BaseMessage> receiver) {
			this.receiver = receiver;
		}

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				try {
					final BaseMessage msg = receiver.receive();

					if (msg instanceof RunTaskMessage) {

						// spawn a new thread for the task, it might take a while
						new Thread() {
							@Override
							public void run() {
								onRunTask((RunTaskMessage) msg);
							}
						}.start();

					} else if (msg instanceof KillTaskMessage) {
						onKillTask((KillTaskMessage) msg);
					} else {
						log.warn("Host Runtime does not know how to handle message of type {}", msg.getClass());
					}

				} catch (MessagingException e) {
					log.error("Error receiving a message", e);
				} catch (Exception e) {
					break;
				}
			}

			log.info("Processing of Task Action Messages stopped");
		}
	}
}
