/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Jiri Tauber
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package cz.cuni.mff.been.task.contextmonitor;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerCallbackInterface;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerService;
import cz.cuni.mff.been.common.serialize.Deserialize;
import cz.cuni.mff.been.common.serialize.DeserializeException;
import cz.cuni.mff.been.jaxb.td.Dependencies;
import cz.cuni.mff.been.jaxb.td.DependencyCheckPoint;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.cuni.mff.been.task.TaskUtils;
import cz.cuni.mff.been.taskmanager.CheckPoint;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.taskmanager.data.TaskState;

/**
 * @author Jiri Tauber
 * 
 */
public class ContextMonitor extends Job {

	private enum ContextState {
		RUNNING, FINISHED, FAILED, BLOCKED
	}

	// -----------------------------------------------------------------------//

	/** Wait time between checks */
	public static int CHECK_INTERVAL_MILIS = 45 * 1000; // checks every 45s

	/** How many times in a row can call to task manager fail */
	public static int FAILURE_TOLERANCE = 5;

	// -----------------------------------------------------------------------//
	// Task property names:
	/** Property indicates whether to notify Benchmark Manager */
	public static String DONT_NOTIFY_BM = "dont.notify.bm";

	// -----------------------------------------------------------------------//
	private final String contextId;

	private ContextState contextState = ContextState.RUNNING;

	// -----------------------------------------------------------------------//
	public ContextMonitor() throws TaskInitializationException {
		super();
		contextId = getTaskDescriptor().getContextId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.task.Job#checkRequiredProperties()
	 */
	@Override
	protected void checkRequiredProperties() throws TaskException {
		// None required

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	protected void run() throws TaskException {
		TaskEntry[] tasks = null;
		Map<TaskEntry, CheckPoint[]> checkpoints = null;

		int failures = 0;

		while (contextState.equals(ContextState.RUNNING)) {

			// get tasks in context
			try {
				TaskManagerInterface taskManager = getTasksPort()
						.getTaskManager();
				tasks = taskManager.getTasksInContext(contextId);

				checkpoints = TaskUtils.getCheckPointsForTasks(
						taskManager,
						tasks);
			} catch (Exception e) {
				failures++;
				if (failures > FAILURE_TOLERANCE) {
					logFatal("Couldn't retrieve list of tasks in context "
							+ failures + " times in a row");
					e.printStackTrace();
					exitError();
				} else {
					continue;
				}
			}

			failures = 0;

			contextState = detectContextState(tasks, checkpoints);
			if (!contextState.equals(ContextState.RUNNING)) {
				break;
			}

			// wait for a while
			try {
				Thread.sleep(CHECK_INTERVAL_MILIS);
			} catch (InterruptedException e) {
			}
			if (Thread.interrupted()) {
				logError("Task was interrupted");
				exitError();
			}
		}

		switch (contextState) {
			case FINISHED:
				logInfo("All tasks in context are either ABORTED or FINISHED correctly. Context will be closed.");
				break;
			case FAILED:
				logInfo("All tasks in context are either ABORTED or FINISHED. Some tasks in this context are FAILED. Context will not be closed.");
				break;
			case BLOCKED:
				logInfo("All tasks in context are either ABORTED, FINISHED or SCHEDULED. The SCHEDULED tasks are not expected to run because of their unsatisfiable dependencies. Context will not be closed.");
				break;
			default:
				throw new TaskException("Invalid context state: "
						+ contextState);
		}

		notifyBenchmarkManager();
		closeContex();
	}

	// -----------------------------------------------------------------------//
	/**
	 * Notifies the Benchmark Manager about end of work in this context. Context
	 * is reported to the BM regardless of the context state.
	 */
	private void notifyBenchmarkManager() {
		if (getBooleanTaskProperty(DONT_NOTIFY_BM)) {
			return;
		}
		logInfo("Reporting inactive context to Benchmark Manager (next analysis run will be allowed)");
		BenchmarkManagerCallbackInterface benchmarkManager;
		try {
			benchmarkManager = (BenchmarkManagerCallbackInterface) getTasksPort()
					.getTaskManager().serviceFind(
							BenchmarkManagerService.SERVICE_NAME,
							BenchmarkManagerService.CALLBACK_INTERFACE);
			benchmarkManager.reportAnalysisFinish(contextId);
		} catch (Exception e) {
			logError("Couldn't notify Benchmark Manager because error occured: "
					+ e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Closes the context. Context is closed only if all its tasks finished
	 * properly.
	 * 
	 */
	private void closeContex() {
		if (!contextState.equals(ContextState.FINISHED)) {
			return;
		}
		// formally close the monitored context in Task Manager
		// do so only if context is really finished (not if it's blocked)
		try {
			getTasksPort().getTaskManager().closeContext(contextId);
			logInfo("Current context closed");
		} catch (RemoteException e) {
			logError("Couldn't close context because error occurred: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * returns whether all tasks in context except this task are either finished
	 * or aborted or blocked.
	 * 
	 * Blocked task means that task have unsatisfiable dependency on another
	 * tasks and is in state submitted. This unsatisfiability is evaluated
	 * transitively.
	 * 
	 * @param tasks
	 *            List of tasks in the context (may include this task)
	 * @param checkpoints
	 * @return true if all tasks in context except this task are either finished
	 *         or aborted or blocked.
	 * 
	 * 
	 */
	private ContextState detectContextState(
			TaskEntry[] tasks,
			Map<TaskEntry, CheckPoint[]> checkpoints) throws TaskException {
		String myTID = getTaskDescriptor().getTaskId();
		/* flag saying whether there is at least one failed task */
		boolean existsFailedTask = false;

		/* TID-TaksEntry mapping of all tasks in the context */
		Map<String, TaskEntry> taskMap = new HashMap<String, TaskEntry>();

		/* task that are not going to have any activity */
		Set<String> blockedTasks = new HashSet<String>();

		/* task that are in state submitted */
		Set<String> submittedTasks = new HashSet<String>();

		for (TaskEntry taskEntry : tasks) {
			String tid = taskEntry.getTaskId();
			if (!myTID.equals(tid)) {
				taskMap.put(tid, taskEntry);

				if (taskEntry.getState().equals(TaskState.FINISHED)) {
					if (existsFailedTask) {
						// we don't need to look for another failed task
						continue;
					}
					// check if task is failed or succeeded
					for (CheckPoint checkPoint : checkpoints.get(taskEntry)) {
						if (checkPoint.getName().equals(
								Task.CHECKPOINT_NAME_FINISHED)) {
							if (!checkPoint.getValue().equals(
									new Integer(Task.EXIT_CODE_SUCCESS))) {
								existsFailedTask = true;
							}
							break;
						}
					}
				} else if (!taskEntry.getState().equals(TaskState.ABORTED)) {
					// not finished nor aborted => submitted or running
					if (taskEntry.getState().equals(TaskState.SUBMITTED)) {
						submittedTasks.add(tid);
					} else {
						/*
						 * we have task, that is nor finished, aborted or
						 * submitted - i.e. this task is active
						 */
						return ContextState.RUNNING;
					}
				}
			}
		}

		if (submittedTasks.isEmpty()) {
			// all tasks are either finished or aborted
			return existsFailedTask ? ContextState.FAILED
					: ContextState.FINISHED;
		}

		// look for blocked tasks
		Set<String> newBlocked = new HashSet<String>();
		do {
			submittedTasks.removeAll(newBlocked);
			blockedTasks.addAll(newBlocked);
			newBlocked.clear();

			for (String tid : submittedTasks) {
				TaskEntry task = taskMap.get(tid);
				TaskDescriptor descriptor = task.getModifiedTaskDescriptor();

				if (descriptor.isSetDependencies()) {
					Dependencies dependencies = descriptor.getDependencies();
					if (dependencies.isSetDependencyCheckPoint()) {
						for (DependencyCheckPoint dependency : dependencies
								.getDependencyCheckPoint()) {
							String prereqTid = dependency.getTaskId();
							TaskEntry prereqTask = taskMap.get(prereqTid);
							if (prereqTask == null)
								continue;

							if (blockedTasks.contains(prereqTid)) {
								newBlocked.add(tid);
								break;
							}

							if (prereqTask.getState().equals(TaskState.ABORTED)
									|| prereqTask.getState().equals(
											TaskState.FINISHED)) {

								CheckPoint[] prereqCheckpoints = checkpoints
										.get(prereqTask);

								if (!isDependencyMet(
										dependency,
										prereqCheckpoints)) {
									newBlocked.add(tid);
									break;
								}

							}
						}
					}
				}
			}

		} while (!newBlocked.isEmpty());

		/* context is blocked if all submitted tasks are blocked */
		return submittedTasks.isEmpty() ? ContextState.BLOCKED
				: ContextState.RUNNING;
	}

	/**
	 * Evaluates whether dependency is met by given checkpoints
	 * 
	 * @param dependency
	 *            dependency to meet
	 * @param checkpoints
	 *            checkpoints of task
	 * @return true if dependency is met
	 */
	private boolean isDependencyMet(
			DependencyCheckPoint dependency,
			CheckPoint[] checkpoints) throws TaskException {
		for (CheckPoint checkpoint : checkpoints) {

			if (dependency.getType().equals(checkpoint.getName())) {

				Serializable value = getDependencyValue(dependency);

				if (value == null /* && checkpoint.getValue() == null */) { // equals()
																			// must
																			// be
																			// null-proof...
					return true;
				} else {
					if (value.equals(checkpoint.getValue())) { // ...else
																// potential
																// null access.
						return true;
					}
				}
			}

		}
		return false;
	}

	/**
	 * The correct way how to retrieve dependencyCheckpointValue
	 * 
	 * @return Serializable value of a checkpoint
	 * @throws DeserializeException
	 */
	private Serializable getDependencyValue(
			DependencyCheckPoint dependencyCheckpoint) throws TaskException {
		try {
			Serializable magicObject;
			if (dependencyCheckpoint.isSetBinVal()) {

				magicObject = Deserialize.fromBase64(dependencyCheckpoint
						.getBinVal());

			} else if (dependencyCheckpoint.isSetStrVal()) {
				magicObject = Deserialize.fromString(dependencyCheckpoint
						.getStrVal());
			} else if (dependencyCheckpoint.isSetValue()) {
				magicObject = dependencyCheckpoint.getValue();
			} else {
				magicObject = null;
			}

			return magicObject;
		} catch (DeserializeException e) {
			throw new TaskException(
					"Error retrieving dependency checkpoint's value",
					e);
		}
	}
}
