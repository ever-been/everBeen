package cz.cuni.mff.d3s.been.hostruntime;

import static cz.cuni.mff.d3s.been.core.TaskPropertyNames.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.*;
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
import cz.cuni.mff.d3s.been.core.task.TaskProperty;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.hostruntime.task.*;
import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
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
final class ProcessManager implements Service {

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
	 * Thread dispatching task action messages.
	 */
	TaskActionThread taskActionThread;

	/**
	 * Collect results from tasks and dispatch them
	 */
	private ResultsDispatcher resultsDispatcher;

	private TaskRequestBrokerThread reqThread;

	/**
	 * Creates new instance.
	 * <p/>
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
		this.executorService = Executors.newFixedThreadPool(1);
	}

	/**
	 * Starts processing messages and tasks.
	 */
	@Override
	public void start() throws ServiceException {
		startTaskRequestBroker();
		startTaskActionThread();
		startTaskMessageDispatcher();
		startResultsDispatcher();
	}

	/** Starts the {@link TaskRequestBrokerThread} */
	private void startTaskRequestBroker() {
		reqThread = new TaskRequestBrokerThread(clusterContext);
		reqThread.start();

		int port = reqThread.getPort();

		// register the property so it can be passed to tasks
		System.setProperty(REQUEST_PORT, Integer.toString(port));
	}

	/** Starts the {@link TaskActionThread} */
	private void startTaskActionThread() throws ServiceException {
		taskActionThread = new TaskActionThread();

		taskActionThread.start();
	}

	/** Starts the {@link TaskMessageDispatcher} */
	private void startTaskMessageDispatcher() throws ServiceException {
		taskMessageDispatcher = new TaskMessageDispatcher(clusterContext);
		taskMessageDispatcher.start();
	}

	/** Starts the {@link ResultsDispatcher} */
	private void startResultsDispatcher() throws ServiceException {
		resultsDispatcher = new ResultsDispatcher(clusterContext, "localhost");
		try {
			resultsDispatcher.init();
		} catch (MessagingException e) {
			throw new ServiceException("Failed to register result dispatcher", e);
		}
		executorService.submit(resultsDispatcher);
	}

	/**
	 * Stops processing, kills all remaining running processes
	 */
	@Override
	public void stop() {
		stopResultsDispatcher();
		stopTaskMessageDispatcher();
		stopTaskActionThread();
		stopTaskRequestBroker();

		// Kill all remaining running tasks
		killRunningTasks();

	}

	/** Stops the {@link ResultsDispatcher} */
	private void stopResultsDispatcher() {
		log.debug("Stopping result dispatcher...");
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.warn("Results dispatcher interrupted during shutdown sequence. Socket leaks are likely.", e);
		}
		log.debug("Result dispatcher stopped.");
	}

	/** Stops the {@link TaskMessageDispatcher} */
	private void stopTaskMessageDispatcher() {
		log.debug("Stopping task message dispatcher...");
		taskMessageDispatcher.stop();
		log.debug("Task message dispatcher stopped.");
	}

	/** Stops the {@link TaskActionThread} */
	private void stopTaskActionThread() {
		log.debug("Stopping task action thread");
		try {
			taskActionThread.poison();
			taskActionThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.debug("Task action thread stopped");
	}

	/** Stops the {@link TaskRequestBrokerThread} */
	private void stopTaskRequestBroker() {
		// TODO
	}

	/**
	 * Kills all running tasks
	 */
	private void killRunningTasks() {
		try {
			for (TaskProcess process : runningTasks.values()) {
				log.debug("Killing task process {}", process);
				process.kill();
			}

			while (!runningTasks.isEmpty()) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			// give up
		}
	}

	/**
	 * Handles RunTaskMessage.
	 * 
	 * Tries to run a task.
	 * 
	 * @param message
	 */
	void onRunTask(RunTaskMessage message) {
		TaskEntry taskHandle = loadTask(message.taskId);
		if (taskHandle == null) {
			log.warn("No such task to run: {}", message.taskId);
		} else {
			runTask(taskHandle);
		}
	}

	/**
	 * Handles KillTaskMessage.
	 * 
	 * Tries to kill a task.
	 * 
	 * @param message
	 */
	synchronized void onKillTask(KillTaskMessage message) {
		TaskEntry taskHandle = loadTask(message.taskId);
		if (taskHandle == null) {
			log.warn("No such task to kill: {}", message.taskId);
		} else {
			TaskProcess taskProcess = runningTasks.get(taskHandle.getId());
			if (taskProcess != null) {
				taskProcess.kill();
			}
		}
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

	private void runTask(TaskEntry taskEntry) {

		String id = taskEntry.getId();
		TaskHandle taskHandle = new TaskHandle(taskEntry, clusterContext);

		// TODO check whether the task can be accepted to run on the Host Runtime
		// the check must be synchronized since runTask can run in several threads

		try {
			taskHandle.setAccepted();
		} catch (IllegalStateException e) {
			String msg = String.format("Was about to accept a task %s but could not make it happen.", taskEntry.getId());
			log.debug(msg, e);
			return;
		}

		File taskDir = createTaskDir(taskEntry);

		try (TaskProcess process = createTaskProcess(taskEntry, taskDir)) {
			runningTasks.put(id, process);

			if (process.isDebugListeningMode()) {
				taskHandle.setDebug(process.getDebugPort(), process.isSuspended());
			}

			taskHandle.setRunning(process);

			int exitValue = process.start();

			taskHandle.setFinished(exitValue);

		} catch (Exception e) {
			String msg = String.format("Task '%s' has been aborted due to underlying exception.", id);
			taskHandle.setAborted(msg);
			log.error(msg, e);
		} finally {
			runningTasks.remove(id);
			DebugAssistant debugAssistant = new DebugAssistant(clusterContext);
			debugAssistant.removeSuspendedTask(taskEntry.getId());
		}
	}

	/**
	 * Creates a new task processes.
	 * 
	 * TODO: Refactoring might be useful. Fortunately the mess is concentrated
	 * only in this function
	 * 
	 * @param taskEntry
	 *          entry associated with the new process
	 * @param taskDirectory
	 *          root directory of the task
	 * 
	 * @return task process representation
	 * 
	 * @throws IOException
	 * @throws BpkConfigurationException
	 * @throws TaskException
	 */
	private TaskProcess createTaskProcess(TaskEntry taskEntry, File taskDirectory) throws IOException, BpkConfigurationException, TaskException {

		TaskDescriptor taskDescriptor = taskEntry.getTaskDescriptor();

		Bpk bpk = getBpk(taskDescriptor);

		ZipFileUtil.unzipToDir(bpk.getInputStream(), taskDirectory);

		// obtain bpk configuration
		Path taskWrkDir = taskDirectory.toPath();

		// obtain runtime information
		BpkRuntime runtime = getBpkRuntime(taskDirectory);

		// create process for the task
		CmdLineBuilder cmdLineBuilder = CmdLineBuilderFactory.create(runtime, taskDescriptor, taskDirectory);

		// create dependency downloader
		DependencyDownloader dependencyDownloader = DependencyDownloaderFactory.create(runtime);

		// create output handler
		ExecuteStreamHandler streamHandler = createStreamHandler(taskEntry);

		// create environment properties
		Map<String, String> environment = createEnvironmentProperties(taskEntry);

		TaskProcess taskProcess = new TaskProcess(cmdLineBuilder, taskWrkDir, environment, streamHandler, dependencyDownloader);

		long timeout = determineTimeout(taskDescriptor);

		taskProcess.setTimeout(timeout);

		return taskProcess;
	}

	private Bpk getBpk(TaskDescriptor taskDescriptor) throws TaskException {
		BpkIdentifier bpkIdentifier = BpkIdentifierCreator.createBpkIdentifier(taskDescriptor);
		return softwareResolver.getBpk(bpkIdentifier);
	}

	private BpkRuntime getBpkRuntime(File workingDirectory) throws BpkConfigurationException {
		// obtain bpk configuration
		Path configPath = workingDirectory.toPath().resolve(BpkNames.CONFIG_FILE);
		BpkConfiguration bpkConfiguration = BpkConfigUtils.fromXml(configPath);

		return bpkConfiguration.getRuntime();

	}

	private long determineTimeout(TaskDescriptor td) {
		return td.isSetFailurePolicy() ? td.getFailurePolicy().getTimeoutRun()
				: TaskProcess.NO_TIMEOUT;
	}

	private ExecuteStreamHandler createStreamHandler(TaskEntry entry) {
		ClusterStreamHandler stdOutHandler = new ClusterStreamHandler(clusterContext, entry.getId(), entry.getTaskContextId(), "stdout");
		ClusterStreamHandler stdErrHandler = new ClusterStreamHandler(clusterContext, entry.getId(), entry.getTaskContextId(), "stderr");

		return new PumpStreamHandler(stdOutHandler, stdErrHandler);

	}

	private Map<String, String> createEnvironmentProperties(TaskEntry taskEntry) {

		Map<String, String> properties = new HashMap<>(System.getenv());
		properties.put(LOGGER, System.getProperty(LOGGER));
		properties.put(REQUEST_PORT, System.getProperty(REQUEST_PORT));
		properties.put(TASK_ID, taskEntry.getId());
		properties.put(TASK_CONTEXT_ID, taskEntry.getTaskContextId());
		properties.put(HR_COMM_PORT, Integer.toString(taskMessageDispatcher.getReceiverPort()));
		properties.put(HR_RESULTS_PORT, Integer.toString(resultsDispatcher.getPort()));

		properties.put(HR_HOSTNAME, clusterContext.getInetSocketAddress().getHostName());

		// add properties specified in the TaskDescriptor
		TaskDescriptor td = taskEntry.getTaskDescriptor();
		if (td.isSetProperties() && td.getProperties().isSetProperty()) {
			for (TaskProperty property : td.getProperties().getProperty()) {
				properties.put(property.getName(), property.getValue());
			}
		}
		return properties;
	}

	private File createTaskDir(TaskEntry taskEntry) {
		String taskDirName = taskEntry.getTaskDescriptor().getName() + "_" + taskEntry.getId();
		File taskDir = new File(hostInfo.getWorkingDirectory(), taskDirName);
		// TODO check return value
		taskDir.mkdirs();
		return taskDir;
	}

	private TaskEntry loadTask(String taskId) {
		return tasks.getTask(taskId);
	}

	/**
	 * Thread listening for task action messages. Dispatches messages to its
	 * handlers.
	 * <p/>
	 * The thread is an inner class for easy access to the ProcessManager.
	 */
	private class TaskActionThread extends Thread {

		final MessageQueues queues;

		private final Logger log = LoggerFactory.getLogger(TaskActionThread.class);

		// TODO use ExecutorService for task handling

		TaskActionThread() {
			this.queues = MessageQueues.getInstance();
		}

		@Override
		public void run() {
			IMessageReceiver<BaseMessage> receiver;

			try {
				receiver = queues.getReceiver(HostRuntime.ACTION_QUEUE_NAME);
			} catch (MessagingException e) {
				String msg = String.format("Cannot start %s", TaskActionThread.class);
				log.error(msg, e);
				return;
			}

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
					} else if (msg instanceof PoisonMessage) {
						break;
					} else {
						log.warn("Host Runtime does not know how to handle message of type {}", msg.getClass());
					}

				} catch (MessagingException e) {
					log.error("Error receiving a message", e);
				} catch (Exception e) {
					log.error("Unknown error", e);
					break;
				}
			}

			log.info("Processing of Task Action Messages stopped");
		}

		public void poison() {
			IMessageSender<BaseMessage> sender = null;
			try {
				sender = queues.createSender(HostRuntime.ACTION_QUEUE_NAME);
				PoisonMessage msg = new PoisonMessage("0", "0");
				sender.send(msg);
			} catch (MessagingException e) {
				log.error("Cannot poison Task Action queue", e);
			} finally {
				if (sender != null) {
					sender.close();
				}
			}

		}
	}

	/** Poison message for the task action thread */
	private static class PoisonMessage extends BaseMessage {
		public PoisonMessage(String senderId, String receiverId) {
			super(senderId, receiverId);
		}
	}
}
