package cz.cuni.mff.d3s.been.hostruntime;

import static cz.cuni.mff.d3s.been.core.task.TaskExclusivity.*;

import java.io.File;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.MonitorSample;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.hostruntime.task.TaskHandle;
import cz.cuni.mff.d3s.been.hostruntime.task.TaskProcess;

/**
 * 
 * Keeps track of the current state of tasks running on a Host Runtime including
 * exclusivity
 * 
 * @author Martin Sixta
 */
final class ProcessManagerContext {

	/** logging */
	private static Logger log = LoggerFactory.getLogger(ProcessManagerContext.class);

	/** connection to the cluster */
	private final ClusterContext clusterContext;

	/** current Host Runtime info */
	private final RuntimeInfo hostInfo;

	/** Maps task IDs to its Process */
	private final Map<String, TaskProcess> runningTasks = Collections.synchronizedMap(new HashMap<String, TaskProcess>());

	/** current exclusivity level */
	private volatile TaskExclusivity currentExclusivity = NON_EXCLUSIVE;

	/** current exclusive ID (task or context) */
	private volatile String currentExclusiveId = null;

	/** set of reserved / accepted tasks */
	Set<String> acceptedTasks = new HashSet<>();

	/**
	 * Creates new ProcessManagerContext
	 * 
	 * @param clusterContext
	 *          connection to the cluster
	 * @param hostInfo
	 *          Host Runtime info
	 */
	ProcessManagerContext(ClusterContext clusterContext, RuntimeInfo hostInfo) {

		this.clusterContext = clusterContext;
		this.hostInfo = hostInfo;
	}

	/**
	 * 
	 * Tries to accept a task.
	 * 
	 * If successful all necessary data structures (cluster wide) will be updated.
	 * 
	 * @param taskHandle
	 *          task handle
	 * @throws IllegalStateException
	 *           if a task cannot be accepted to run on this Host Runtime
	 */
	synchronized void tryAcceptTask(TaskHandle taskHandle) throws IllegalStateException {

		TaskExclusivity prevExclusivity = currentExclusivity;
		String prevExclusiveId = currentExclusiveId;

		boolean canAccept = tryChangeExclusivity(taskHandle);

		if (canAccept) {
			try {
				taskHandle.setAccepted();
				acceptedTasks.add(taskHandle.getTaskId());
			} catch (IllegalStateException e) {
				// reset exclusivity
				setExclusivity(prevExclusivity, prevExclusiveId);
				throw e;
			}
		} else {
			throw new IllegalStateException("EXCLUSIVITY cannot be satisfied");
		}

		updateHostInfo();

	}

	/**
	 * 
	 * Adds a running task.
	 * 
	 * Updates all necessary data structures
	 * 
	 * @param id
	 *          ID of t
	 * @param process
	 *          process representing the task
	 */
	synchronized void addTask(String id, TaskProcess process) {
		assert (acceptedTasks.contains(id));
		runningTasks.put(id, process);
	}

	/**
	 * 
	 * Removes a task.
	 * 
	 * Updates all necessary data structures
	 * 
	 * @param taskHandle
	 *          task handle
	 */
	synchronized void removeTask(TaskHandle taskHandle) {
		runningTasks.remove(taskHandle.getTaskId());
		acceptedTasks.remove(taskHandle.getTaskId());

		if (getTasksCount() == 0) {
			setExclusivity(NON_EXCLUSIVE, null);
		}

		updateHostInfo();

		DebugAssistant debugAssistant = new DebugAssistant(clusterContext);
		debugAssistant.removeSuspendedTask(taskHandle.getTaskId());
	}

	/**
	 * Returns current count of accepted tasks.
	 * 
	 * Be aware that an accepted task does not have to have it's corresponding
	 * {@link TaskProcess} created yet.
	 * 
	 * @return number of accepted tasks of the Host Runtime
	 */
	int getTasksCount() {
		return acceptedTasks.size();
	}

	/**
	 * Kills all running tasks
	 */
	void killRunningTasks() {
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
	 * Kills a running task
	 * 
	 * @param id
	 *          ID of the task to kill
	 */
	public void killTask(String id) {
		TaskProcess taskProcess = runningTasks.get(id);
		if (taskProcess != null) {
			taskProcess.kill();
		}
	}

	/**
	 * Updates load information of the Host Runtime
	 * 
	 * @param sample
	 *          monitoring sample
	 */
	public synchronized void updateMonitoringSample(MonitorSample sample) {
		hostInfo.setMonitorSample(sample);
		updateHostInfo();
	}

	/**
	 * Updates Host Runtime information in the cluster
	 */
	private void updateHostInfo() {
		hostInfo.setExclusivity(currentExclusivity.toString());
		hostInfo.setExclusiveId(currentExclusiveId);
		hostInfo.setTaskCount(getTasksCount());
        hostInfo.getTaskDirs().clear();
        hostInfo.getTaskDirs().addAll(getTaskDirs());
		clusterContext.getRuntimes().storeRuntimeInfo(hostInfo);
	}

    private List<String> getTaskDirs() {
        return Arrays.asList(new File(hostInfo.getTasksWorkingDirectory()).list());
    }

    /**
	 * Sets current exclusivity.
	 * 
	 * @param exclusivity
	 *          Host Runtime exclusivity
	 * @param exclusiveId
	 *          ID associated with the exclusivity
	 */
	private void setExclusivity(TaskExclusivity exclusivity, String exclusiveId) {
		currentExclusivity = exclusivity;
		currentExclusiveId = exclusiveId;
	}

	/**
	 * Sets current exclusivity from a task handle.
	 * 
	 * @param taskHandle
	 *          task handle
	 */
	private void setExclusivity(TaskHandle taskHandle) {
		TaskExclusivity exclusivity = taskHandle.getExclusivity();

		if (exclusivity == CONTEXT_EXCLUSIVE) {
			setExclusivity(exclusivity, taskHandle.getContextId());
		} else if (exclusivity == EXCLUSIVE) {
			setExclusivity(exclusivity, taskHandle.getTaskId());
		}
	}

	/**
	 * 
	 * Tries to change current exclusivity.
	 * 
	 * @param taskHandle
	 *          task handle
	 * @return true if current exclusivity was changed, false if the exclusivity
	 *         cannot be changed
	 */
	private boolean tryChangeExclusivity(TaskHandle taskHandle) {
		TaskExclusivity exclusivity = taskHandle.getExclusivity();

		switch (currentExclusivity) {
			case NON_EXCLUSIVE:
				boolean isExclusive = (exclusivity != NON_EXCLUSIVE);
				boolean isFree = (getTasksCount() == 0);

				if (isExclusive && isFree) {
					setExclusivity(taskHandle);
					return true;
				} else if (isExclusive) {
					return false; // there are running tasks
				} else {
					setExclusivity(taskHandle);
					return true;
				}
			case CONTEXT_EXCLUSIVE:
				return taskHandle.getContextId().equals(currentExclusiveId);
			case EXCLUSIVE:
				return false;
			default:
				// should not happen, make the compiler happy
				log.error("Unimplemented case statement!");
				return false;
		}

	}

}
