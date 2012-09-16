/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager.data;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedList;

import cz.cuni.mff.been.hostruntime.TaskInterface;
import cz.cuni.mff.been.taskmanager.tasktree.IllegalAddressException;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeInput;

/**
 * This class holds <code>DataStructures</code> of <code>Task Manager</code> and
 * serves as an interface to it.
 * 
 * @author Antonin Tomecek
 */
public class Data {

	/**
	 * Rescue object (storing of TM's data to file system).
	 */
	// private final Rescue rescue;

	/**
	 * <code>DataStructures</code> of Task Manager.
	 */
	private final DataStructures data;

	/** A reference to Task Manager's tree of tasks, useful for node cleanup */
	private final TaskTreeInput taskTree;

	/**
	 * Constructor of this <code>Data</code> object. Should be used by
	 * <code>Task Manager</code>.
	 */
	public Data(@SuppressWarnings("unused") File rescueRootDir, TaskTreeInput taskTree) {
		this.data = new DataStructures();
		this.taskTree = taskTree;
	}

	/**
	 * Add new Task.
	 * 
	 * @param taskEntry
	 *          <code>TaskEntry</code> for new Task.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	public synchronized void newTask(TaskEntryImplementation taskEntry,
			TaskData taskData) {
		/* Check input parameters. */
		if (taskEntry == null) {
			throw new NullPointerException("taskEntry is null");
		}
		if (taskData == null) {
			throw new NullPointerException("taskData is null");
		}

		/*
		 * TODO AP: This cloning caused CLI to receive out-of-data 'submitted'
		 * state all the time. There is no point in cloning the task entry on
		 * input. OK, it should be cloned on output, but that definitely does
		 * happen on RMI transfers anyway. All those methods like
		 * getTaskBySomething() clone, but there seems to be no reason for that.
		 */

		// TaskEntry newTaskEntry;

		/* Clone TaskEntry. */
		// try {
		// newTaskEntry = taskEntry.clone();
		// } catch (CloneNotSupportedException e) {
		// throw new DataRuntimeException("Could not clone taskEntry", e);
		// }

		/* Set values of new TaskEntry. */
		// newTaskEntry.setState(TaskState.SUBMITTED);
		// newTaskEntry.setTimeSubmitted(System.currentTimeMillis());

		/* Add new TaskEntry to Data. */
		// this.data.addTask(newTaskEntry, taskData);
		this.data.addTask(taskEntry, taskData);

		/* Add task to rescue. */
		// this.rescue.addTask(newTaskEntry, taskData);
	}

	/**
	 * Add new Context.
	 * 
	 * @param contextEntry
	 *          <code>ContextEntry</code> for new Context.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	public synchronized void newContext(ContextEntry contextEntry) {
		/* Check input parameters. */
		if (contextEntry == null) {
			throw new NullPointerException("contextEntry is null");
		}

		ContextEntry newContextEntry;

		/* Clone ContextEntry. */
		try {
			// FIXME remove cast by fixing ContextEntry type hierarchy
			newContextEntry = ((ContextEntryImplementation) contextEntry).clone();
		} catch (CloneNotSupportedException e) {
			throw new DataRuntimeException("Could not clone contextEntry", e);
		}

		/* Set values of new ContextEntry. */
		// nothing to do

		/* Add new ContextEntry to Data. */
		this.data.addContext(newContextEntry);

		/* Add context to rescue. */
		// this.rescue.addContext(newContextEntry);
	}

	/**
	 * Add new CheckPoint.
	 * 
	 * @param checkPointEntry
	 *          <code>CheckPointEntry</code> for new CheckPoint.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	public synchronized void newCheckPoint(CheckPointEntry checkPointEntry) {
		/* Check input parameters. */
		if (checkPointEntry == null) {
			throw new NullPointerException("checkPointEntry is null");
		}

		CheckPointEntry newCheckPointEntry;

		/* Clone CheckPointEntry. */
		try {
			newCheckPointEntry = checkPointEntry.clone();
		} catch (CloneNotSupportedException e) {
			throw new DataRuntimeException("Could not clone checkPointEntry", e);
		}

		/* Set values of new CheckPointEntry. */
		newCheckPointEntry.setTimeReached(System.currentTimeMillis());

		/* Add new CheckPointEntry to Data. */
		this.data.addCheckPoint(newCheckPointEntry);

		/* Add checkPoint to rescue. */
		// this.rescue.addCheckPoint(newCheckPointEntry);
	}

	/**
	 * Add new CheckPoint. It finds all CheckPoints with the same name, taskId and
	 * contextId and name as newly added one and removes them from list before new
	 * one is added.
	 * 
	 * @param checkPointEntry
	 *          <code>CheckPointEntry</code> for new CheckPoint.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 */
	public synchronized void newCheckPointOver(CheckPointEntry checkPointEntry) {
		/* Check input parameters. */
		if (checkPointEntry == null) {
			throw new NullPointerException("checkPointEntry is null");
		}

		/* Find checkPoints with the same name, taskId and contextId. */
		CheckPointEntry[] foundedCheckPoints = this.data.getCheckPoints(checkPointEntry.getName(), checkPointEntry.getTaskId(), checkPointEntry.getContextId(), null);
		/* Remove all founded. */
		for (CheckPointEntry checkPoint : foundedCheckPoints) {
			this.data.removeCheckPoint(checkPoint);
			/* Remove checkPoint from rescue. */
			// this.rescue.remCheckPoint(checkPoint.getName(),
			// checkPoint.getTaskId(), checkPoint.getContextId());
		}

		/* Add new checkPoint. */
		this.newCheckPoint(checkPointEntry);
	}

	/**
	 * Delete Task.
	 * 
	 * @param taskId
	 *          ID of Task.
	 * @param contextId
	 *          ID of Context.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If task not found.
	 */
	public synchronized void delTask(String taskId, String contextId) {
		/* Check input parameters. */
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		TaskEntryImplementation taskEntry;

		/* Get TaskEntry. */
		taskEntry = this.data.getTask(taskId, contextId);
		if (taskEntry == null) {
			throw new IllegalArgumentException("Task not found (taskId \"" + taskId + "\", contextId \"" + contextId + "\")");
		}

		/* Remove Task from Data. */
		this.data.removeTask(taskEntry);

		/* Remove Task from Task Tree. */
		try {
			taskTree.clearInclusive(taskEntry.getTreeAddress());
		} catch (IllegalAddressException exception) {
			/*
			 * There's nothing we can do here. The task may have been killed and
			 * deleted explicitely from the CLI or a race condition (automatic
			 * and manual context deletion) could occur. If the leaf doesn't
			 * exist, there's no need to worry.
			 */
		} catch (RemoteException exception) { // Local call. Should never
			// happen.
			assert false : "RemoteException from a loacal call.";
		}

		/* Remove Task from rescue. */
		// this.rescue.remTask(taskId, contextId);
	}

	/**
	 * Delete Context.
	 * 
	 * @param contextId
	 *          ID of Context.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If context not found.
	 */
	public synchronized void delContext(String contextId) {
		/* Check input parameters. */
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		ContextEntry contextEntry;

		/* Get ContextEntry. */
		contextEntry = this.data.getContext(contextId);
		if (contextEntry == null) {
			throw new IllegalArgumentException("Context not found (contextId " + "\"" + contextId + "\")");
		}

		/* Remove Context from Data. */
		this.data.removeContext(contextEntry);

		/* Remove Context from rescue. */
		// this.rescue.remContext(contextId);
	}

	/**
	 * Delete Context by force (i.e. if context contains some tasks then they are
	 * deleted from system automatically).
	 * 
	 * @param contextId
	 *          ID of Context.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If context not found.
	 */
	public synchronized void delContextByForce(String contextId) {
		/* Check input parameters. */
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		ContextEntry contextEntry;

		/* Get ContextEntry. */
		contextEntry = this.data.getContext(contextId);
		if (contextEntry == null) {
			throw new IllegalArgumentException("Context not found (contextId " + "\"" + contextId + "\")");
		}

		/* Remove Context from Data. */
		this.data.removeContextByForce(contextEntry);

		/* Remove Context from rescue. */
		// this.rescue.remContext(contextId);
	}

	/**
	 * Set <code>TaskInterface</code> in <code>TaskData</code>.
	 * 
	 * @throws NullPointerException
	 *           If some input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If task not found.
	 * @throws IllegalStateException
	 *           If taskInterface is already set.
	 */
	public synchronized void setTaskInterface(String taskId, String contextId,
			TaskInterface taskInterface) {
		/* Check input parameters. */
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}
		if (taskInterface == null) {
			throw new NullPointerException("taskInterface is null");
		}

		/* Get and check TaskData. */
		TaskData taskData = this.data.getTaskData(taskId, contextId);
		if (taskData == null) {
			throw new IllegalArgumentException("Task not found (taskId \"" + taskId + "\", contextId \"" + contextId + "\")");
		}

		/* Check current TaskInterface. */
		if (taskData.getTaskInterface() != null) {
			throw new IllegalStateException("TaskInterface is already set");
		}

		/* Set taskInterface. */
		taskData.setTaskInterface(taskInterface);
	}

	/**
	 * Get the hostname where the task was running.
	 * 
	 * @param taskId
	 *          ID of the task.
	 * @param contextId
	 *          ID of the context.
	 * @return hostname.
	 */
	public synchronized String getTaskHost(String taskId, String contextId) {
		/* Check input parameters. */
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		/* Find task. */
		TaskEntryImplementation task = this.data.getTask(taskId, contextId);
		if (task == null) {
			throw new IllegalArgumentException("Task not found (taskId \"" + taskId + "\", contextId \"" + contextId + "\")");
		}

		return task.getHostName();
	}

	/**
	 * Set hostRuntime for task. This can be done only once for each task.
	 * 
	 * @param taskId
	 *          ID of Task.
	 * @param contextId
	 *          ID of Context.
	 * @param hostName
	 *          Name of host running HostRuntime.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If task not found.
	 */
	public synchronized void setTaskHostRuntime(String taskId, String contextId,
			String hostName) {
		/* Check input parameters. */
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}
		if (hostName == null) {
			throw new NullPointerException("hostName is null");
		}

		/* Find task. */
		TaskEntryImplementation task = this.data.getTask(taskId, contextId);
		if (task == null) {
			throw new IllegalArgumentException("Task not found (taskId \"" + taskId + "\", contextId \"" + contextId + "\")");
		}

		HostRuntimeEntry hostRuntime = this.data.getHostRuntime(hostName);
		if (hostRuntime == null) {
			throw new IllegalArgumentException("HostRuntime not found " + "(hostName \"" + hostName + "\")");
		}

		/* Link Task with HostRuntime. */
		this.data.linkTaskWithHostRuntime(task, hostRuntime);

		/* Get TaskData. */
		// TaskData taskData = this.data.getTaskData(taskId, contextId);

		/* Change task in rescue. */
		// this.rescue.changeTask(task, taskData);
	}

	/**
	 * Set paths to task's directories. This can be done only once for each task.
	 * 
	 * @param taskId
	 *          ID of Task.
	 * @param contextId
	 *          ID of Context.
	 * @param taskDirectory
	 *          Path to Task directory.
	 * @param workingDirectory
	 *          Path to Working directory.
	 * @param temporaryDirectory
	 *          Path to Temporary directory.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If task not found or already set.
	 */
	public synchronized void setTaskDirectories(String taskId, String contextId,
			String taskDirectory, String workingDirectory, String temporaryDirectory) {
		/* Check input parameters. */
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}
		if (taskDirectory == null) {
			throw new NullPointerException("taskDirectory is null");
		}
		if (workingDirectory == null) {
			throw new NullPointerException("workingDirectory is null");
		}
		if (temporaryDirectory == null) {
			throw new NullPointerException("temporaryDirectory is null");
		}

		/* Find task. */
		TaskEntryImplementation task = this.data.getTask(taskId, contextId);
		if (task == null) {
			throw new IllegalArgumentException("Task not found (taskId \"" + taskId + "\", contextId \"" + contextId + "\")");
		}

		/* Check if paths are not already set. */
		if (!(task.getDirectoryPathTask().equals("") && task.getDirectoryPathWorking().equals("") && task.getDirectoryPathTemporary().equals(""))) {
			throw new IllegalArgumentException("Task (taskId \"" + taskId + "\", contextId \"" + contextId + "\") has its directory " + "paths already set");
		}

		/* Set directory paths. */
		task.setDirectoryPathTask(taskDirectory);
		task.setDirectoryPathWorking(workingDirectory);
		task.setDirectoryPathTemporary(temporaryDirectory);

		/* Get TaskData. */
		// TaskData taskData = this.data.getTaskData(taskId, contextId);

		/* Change task in rescue. */
		// this.rescue.changeTask(task, taskData);
	}

	/**
	 * Change state of specified task. Allowed changes: SUBMITTED : not allowed
	 * SCHEDULED : allowed only from SUBMITTED RUNNING : allowed only from
	 * SHEDULED or SLEEPING SLEEPING : allowed only from RUNNING FINISHED :
	 * allowed only from RUNNING
	 * 
	 * @param taskId
	 *          ID of task.
	 * @param contextId
	 *          ID of context.
	 * @param newState
	 *          New state of task.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If task not found or change to required state is not allowed.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	public synchronized void changeTaskState(String taskId, String contextId,
			TaskState newState) {
		/* Check input parameters. */
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}
		if (newState == null) {
			throw new NullPointerException("newState is null");
		}

		/* Find task. */
		TaskEntryImplementation task = this.data.getTask(taskId, contextId);
		if (task == null) {
			throw new IllegalArgumentException("Task not found (taskId \"" + taskId + "\", contextId \"" + contextId + "\")");
		}

		/* Get old (current) state. */
		TaskState oldState = task.getState();

		switch (newState) {
			case SUBMITTED:
				throw new IllegalArgumentException("State SUBMITTED should " + "not be set explicitly (taskId \"" + taskId + "\", " + "contextId \"" + contextId + "\")");
				// break; // Unreachable code
			case SCHEDULED:
				if (oldState != TaskState.SUBMITTED) {
					throw new IllegalArgumentException("State SCHEDULED can " + "be set only if current state is SUBMITTED, but " + "current state is " + oldState + " (taskId \"" + taskId + "\", contextId \"" + contextId + "\")");
				}
				task.setState(TaskState.SCHEDULED);
				task.setTimeScheduled(System.currentTimeMillis());
				break;
			case RUNNING:
				if ((oldState != TaskState.SCHEDULED) && (oldState != TaskState.SLEEPING)) {
					throw new IllegalArgumentException("State RUNNING can be " + "set only if current state is SCHEDULED or " + "SLEEPING, but current state is " + oldState + " (taskId \"" + taskId + "\", contextId " + "\"" + contextId + "\")");
				}
				task.setState(TaskState.RUNNING);
				if (oldState == TaskState.SCHEDULED) {
					task.setTimeStarted(System.currentTimeMillis());
				}
				break;
			case SLEEPING:
				if (oldState != TaskState.RUNNING) {
					throw new IllegalArgumentException("State SLEEPING can be " + "set only if current state is RUNNING, but " + "current state is " + oldState + " (taskId " + "\"" + taskId + "\", contextId \"" + contextId + "\")");
				}
				task.setState(TaskState.SLEEPING);
				break;
			case FINISHED:
				if ((oldState != TaskState.RUNNING) && (oldState != TaskState.FINISHED)) {
					throw new IllegalArgumentException("State FINISHED can be " + "set only if current state is RUNNING, but " + "current state is " + oldState + "  (taskId " + "\"" + taskId + "\", contextId \"" + contextId + "\")");
				}
				task.setState(TaskState.FINISHED);
				task.setTimeFinished(System.currentTimeMillis());
				break;
			case ABORTED:
				if (oldState != TaskState.FINISHED) {
					task.setState(TaskState.ABORTED);
					task.setTimeFinished(System.currentTimeMillis());
				}
				break;
			default:
				throw new DataRuntimeException("Required unknown state for " + "task (taskId \"" + taskId + "\", contextId \"" + contextId + "\")");
				// break; // Unreachable code
		}

		/* Get TaskData. */
		// TaskData taskData = this.data.getTaskData(taskId, contextId);

		/* Change task in rescue. */
		// this.rescue.changeTask(task, taskData);
	}

	/**
	 * Change reservation of specified hostRuntime.
	 * 
	 * @param hostName
	 *          Name of host running HostRuntime.
	 * @param newReservation
	 *          ID of Context, <code>null</code> or <code>""</code>.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If hostRuntime not found or required change is not allowed.
	 */
	public synchronized void changeHostRuntimeReservation(String hostName,
			String newReservation) {
		/* Check input parameters. */
		if (hostName == null) {
			throw new NullPointerException("hostName is null");
		}

		/* Find hostRuntime. */
		HostRuntimeEntry hostRuntime = this.data.getHostRuntime(hostName);
		if (hostRuntime == null) {
			throw new IllegalArgumentException("Host Runtime not found " + "(hostName \"" + hostName + "\")");
		}

		/* Get old (current) reservation. */
		String oldReservation = hostRuntime.getReservation();

		if (newReservation == null) { // clean reservation
			hostRuntime.setReservation(null);
		} else { // set up new reservation
			if (oldReservation != null) {
				if (!oldReservation.equals(newReservation)) {
					// the same context can be reserved several times (e.g. more
					// roles
					// on the same host)
					throw new IllegalArgumentException("HostRuntime (hostName \"" + hostName + "\") is already reserved for \"" + oldReservation + "\" so reservation for \"" + newReservation + "\" can not be set");
				}
			}
			hostRuntime.setReservation(newReservation);
		}

		/* Change hostRuntime in rescue. */
		// this.rescue.changeHostRuntime(hostRuntime);
	}

	/**
	 * Increase restart count of specified task.
	 * 
	 * @param taskId
	 *          ID of task.
	 * @param contextId
	 *          ID of context.
	 * @throws NullPointerException
	 *           If some input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If task not found.
	 */
	public synchronized void notifyTaskRestarted(String taskId, String contextId) {
		/* Check input parameters. */
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		/* Find task. */
		TaskEntryImplementation task = this.data.getTask(taskId, contextId);
		if (task == null) {
			throw new IllegalArgumentException("Task not found (taskId \"" + taskId + "\", contextId \"" + contextId + "\")");
		}

		/* Increase restart count. */
		int currentRestartCount = task.getRestartCount();
		task.setRestartCount(currentRestartCount + 1);
	}

	/**
	 * Close context.
	 * 
	 * @param contextId
	 *          ID of context.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If context not found.
	 */
	public synchronized void closeContext(String contextId) {
		/* Check input parameters. */
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		/* Find context. */
		ContextEntryImplementation context = (ContextEntryImplementation) this.data.getContext(contextId);
		if (context == null) {
			throw new IllegalArgumentException("Context not found (contextId " + "\"" + contextId + "\")");
		}

		/* Close context. */
		context.close();

		/* Change context in rescue. */
		// this.rescue.changeContext(context);
	}

	/**
	 * Clone all <code>TaskEntry</code> objects from input array.
	 * 
	 * @param taskEntries
	 *          Array of <code>TaskEntry</code> objects to clone.
	 * @return Array of cloned <code>TaskEntry</code> objects.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	private TaskEntryImplementation[] cloneTaskEntries(
			TaskEntryImplementation[] taskEntries) {
		TaskEntryImplementation[] taskEntriesClone = new TaskEntryImplementation[taskEntries.length];

		for (int i = 0; i < taskEntries.length; i++) {
			try {
				taskEntriesClone[i] = taskEntries[i].clone();
			} catch (CloneNotSupportedException e) {
				throw new DataRuntimeException("Could not clone taskEntry", e);
			}
		}

		return taskEntriesClone;
	}

	/**
	 * Get tasks specified by none or more parameters from taskId, contextId,
	 * hostName. Returned array contains cloned objects.
	 * 
	 * @param taskId
	 *          ID of task.
	 * @param contextId
	 *          ID of context.
	 * @param hostName
	 *          Name of host.
	 * @return Array of cloned <code>TaskEntry</code> objects.
	 */
	private TaskEntryImplementation[] getTasks(String taskId, String contextId,
			String hostName) {
		TaskEntryImplementation[] taskEntries = this.data.getTasks(taskId, contextId, hostName);
		return this.cloneTaskEntries(taskEntries);
	}

	/**
	 * Get all tasks.
	 * 
	 * @return Array of <code>TaskEntry</code> objects.
	 */
	public synchronized TaskEntryImplementation[] getTasks() {
		return this.getTasks(null, null, null);
	}

	/**
	 * Get tasks from specified context.
	 * 
	 * @param contextId
	 *          ID of context.
	 * @return Array of <code>TaskEntry</code> objects.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 */
	public synchronized TaskEntryImplementation[] getTasksInContext(
			String contextId) {
		if (contextId == null)
			throw new NullPointerException("Attempt to get tasks in null context.");
		return this.getTasks(null, contextId, null);
	}

	/**
	 * Get tasks from specified host.
	 * 
	 * @param hostName
	 *          Name of host.
	 * @return Array of <code>TaskEntry</code> objects.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 */
	public synchronized TaskEntryImplementation[] getTasksOnHost(String hostName) {
		if (hostName == null)
			throw new NullPointerException("Attempt to get tasks on null host.");
		return this.getTasks(null, null, hostName);
	}

	/**
	 * Get tasks with specified state.
	 * 
	 * @param taskState
	 *          State of task.
	 * @return Array of <code>TaskEntry</code> objects.
	 * @throws NullPointerException
	 *           If input paremeter is <code>null</code>.
	 */
	public synchronized TaskEntryImplementation[] getTasksByState(
			TaskState taskState) {
		/* Check input parameters. */
		if (taskState == null) {
			throw new NullPointerException("taskState is null");
		}

		/*
		 * Get list of all tasks and select all from them with required state.
		 */
		TaskEntryImplementation[] allTasks = this.getTasks(null, null, null);
		LinkedList<TaskEntryImplementation> matchingTasks = new LinkedList<TaskEntryImplementation>();
		for (int i = 0; i < allTasks.length; i++) {
			if (allTasks[i].getState() == taskState) {
				matchingTasks.add(allTasks[i]);
			}
		}

		return matchingTasks.toArray(new TaskEntryImplementation[matchingTasks.size()]);
	}

	/**
	 * Get task with specified ID's.
	 * 
	 * @param taskId
	 *          ID of task.
	 * @param contextId
	 *          ID of context.
	 * @return <code>TaskEntry</code> object or <code>null</code> if not found.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	public synchronized TaskEntryImplementation getTaskById(String taskId,
			String contextId) {
		/* Check input parameters. */
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		/* Get and check result... */
		TaskEntryImplementation[] taskEntries = this.getTasks(taskId, contextId, null);
		if (taskEntries.length > 1) {
			throw new DataRuntimeException("There is more then one task with " + "taskId \"" + taskId + "\" and contextId \"" + contextId + "\"");
		}
		if (taskEntries.length == 0) {
			return null;
		}

		/* Return the first entry. */
		return taskEntries[0];
	}

	/**
	 * Get data of task with specified ID's.
	 * 
	 * @param taskId
	 *          ID of task.
	 * @param contextId
	 *          ID of context.
	 * @return <code>TaskData</code> object or <code>null</code> if not found.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	public synchronized TaskData getTaskData(String taskId, String contextId) {
		/* Check input parameters. */
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		/* Get taskData... */
		TaskData taskData = this.data.getTaskData(taskId, contextId);

		return taskData;
	}

	/**
	 * Clone all <code>ContextEntry</code> objects from input array.
	 * 
	 * @param contextEntries
	 *          Array of <code>ContextEntry</code> objects to clone.
	 * @return Array of cloned <code>ContextEntry</code> objects.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	private ContextEntry[] cloneContextEntries(ContextEntry[] contextEntries) {
		ContextEntry[] contextEntriesClone = new ContextEntry[contextEntries.length];

		for (int i = 0; i < contextEntries.length; i++) {
			try {
				contextEntriesClone[i] = contextEntries[i].clone();
			} catch (CloneNotSupportedException e) {
				throw new DataRuntimeException("Could not clone contextEntry", e);
			}
		}

		return contextEntriesClone;
	}

	/**
	 * Get contexts specified by none parameter or by contextId. Returned array
	 * contains cloned objects.
	 * 
	 * @param contextId
	 *          ID of context.
	 * @return Array of cloned <code>ContextEntry</code> objects.
	 */
	private ContextEntry[] getContexts(String contextId) {
		/* Get entries from Data. */
		ContextEntry[] contextEntries = this.data.getContexts(contextId);

		/* Return cloned entries. */
		return this.cloneContextEntries(contextEntries);
	}

	/**
	 * Get all contexts.
	 * 
	 * @return Array of <code>ContextEntry</code> objects.
	 */
	public synchronized ContextEntry[] getContexts() {
		return this.getContexts(null);
	}

	/**
	 * Get context with specified ID.
	 * 
	 * @param contextId
	 *          ID of context.
	 * @return <code>ContextEntry</code> object or <code>null</code> if not found.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	public synchronized ContextEntry getContextById(String contextId) {
		/* Check input parameters. */
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		/* Get and check result... */
		ContextEntry[] contextEntries = this.getContexts(contextId);
		if (contextEntries.length > 1) {
			throw new DataRuntimeException("There is more then one context " + "with contextId \"" + contextId + "\"");
		}
		if (contextEntries.length == 0) {
			return null;
		}

		/* Return the first entry. */
		return contextEntries[0];
	}

	/**
	 * Clone all <code>CheckPointEntry</code> objects from input array.
	 * 
	 * @param checkPointEntries
	 *          Array of <code>CheckPointEntry</code> objects to clone.
	 * @return Array of cloned <code>CheckPointEntry</code> objects.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	private CheckPointEntry[] cloneCheckPointEntries(
			CheckPointEntry[] checkPointEntries) {
		CheckPointEntry[] checkPointEntriesClone = new CheckPointEntry[checkPointEntries.length];

		for (int i = 0; i < checkPointEntries.length; i++) {
			try {
				checkPointEntriesClone[i] = checkPointEntries[i].clone();
			} catch (CloneNotSupportedException e) {
				throw new DataRuntimeException("Could not clone " + "checkPointEntry", e);
			}
		}

		return checkPointEntriesClone;
	}

	/**
	 * Get checkPoints specified by none or more parameters from type, value,
	 * taskId, contextId. Returned array contains cloned objects.
	 * 
	 * @param type
	 *          Type of checkPoint.
	 * @param taskId
	 *          ID of task.
	 * @param contextId
	 *          ID of context.
	 * @param magicObject
	 *          Some magic object from outside... (no one understands to this).
	 * @return Array of cloned <code>CheckPointEntry</code> objects.
	 */
	public synchronized CheckPointEntry[] getCheckPoints(String type,
			String taskId, String contextId, Serializable magicObject) {
		/* Get entries from Data. */
		CheckPointEntry[] checkPointEntries = this.data.getCheckPoints(type, taskId, contextId, magicObject);

		/* Return cloned entries. */
		return this.cloneCheckPointEntries(checkPointEntries);
	}

	/**
	 * Get (only last) checkPoint specified by none or more parameters from type,
	 * value, taskId, contextId.
	 * 
	 * @param type
	 *          Type of checkPoint.
	 * @param value
	 *          Value of checkPoint.
	 * @param taskId
	 *          ID of task.
	 * @param contextId
	 *          ID of context.
	 * @return Cloned <code>CheckPointEntry</code> objects or <code>null</code> if
	 *         not found.
	 */
	public synchronized CheckPointEntry getCheckPoint(String type, String value,
			String taskId, String contextId) {
		/* Get all matching checkPoints. */
		CheckPointEntry[] checkPoints = this.getCheckPoints(type, value, taskId, contextId);

		/* Return appropriate thing. */
		int length = checkPoints.length;
		return ((length > 0) ? checkPoints[length - 1] : null);
	}

	// /**
	// * Clone all <code>HostRuntimeEntry</code> objects from input array.
	// *
	// * @param hostRuntimeEntries Array of <code>HostRuntimeEntry</code>
	// objects
	// * to clone.
	// * @return Array of cloned <code>HostRuntimeEntry</code> objects.
	// * @throws DataRuntimeException If internal error occurred.
	// */
	// private HostRuntimeEntry[] cloneHostRuntimeEntries(
	// HostRuntimeEntry[] hostRuntimeEntries) {
	// HostRuntimeEntry[] hostRuntimeEntriesClone
	// = new HostRuntimeEntry[hostRuntimeEntries.length];
	//
	// for (int i = 0; i < hostRuntimeEntries.length; i++) {
	// try {
	// hostRuntimeEntriesClone[i] = hostRuntimeEntries[i].clone();
	// } catch (CloneNotSupportedException e) {
	// throw new DataRuntimeException("Could not clone "
	// + "hostRuntimeEntry", e);
	// }
	// }
	//
	// return hostRuntimeEntriesClone;
	// }

	// /**
	// * Get hostRuntime specified by hostName. Returned object is cloned.
	// *
	// * @param hostName Name of host.
	// * @return Cloned <code>HostRuntimeEntry</code> object or
	// <code>null</code>
	// * if not found.
	// */
	// public synchronized HostRuntimeEntry getHostRuntime(String hostName) {
	// /* Check input parameters. */
	// if (hostName == null) {
	// throw new NullPointerException("hostName is null");
	// }
	//
	// /* Get entry from Data. */
	// HostRuntimeEntry hostRuntimeEntry = this.data.getHostRuntime(hostName);
	//
	// /* If hostRuntime not found then return null. */
	// if (hostRuntimeEntry == null) {
	// return null;
	// }
	//
	// /* Clone entry. */
	// HostRuntimeEntry hostRuntimeEntryClone;
	// try {
	// hostRuntimeEntryClone = hostRuntimeEntry.clone();
	// }
	// catch (CloneNotSupportedException e) {
	// throw new DataRuntimeException("Could not clone "
	// + "HostRuntimeEntry", e);
	// }
	//
	// /* Return cloned entry. */
	// return hostRuntimeEntryClone;
	// }

	/**
	 * Get hostRuntimes specified by none parameter or by hostName. Returned array
	 * contains cloned objects.
	 * 
	 * @param hostName
	 *          Name of host.
	 * @return Array of cloned <code>HostRuntimeEntry</code> objects.
	 */
	private HostRuntimeEntry[] getHostRuntimes(String hostName) {
		/* Get entries from Data. */
		return data.getHostRuntimes(hostName);
	}

	/**
	 * Get all hostRuntimes.
	 * 
	 * @return Array of <code>HostRuntimeEntry</code> objects.
	 */
	public synchronized HostRuntimeEntry[] getHostRuntimes() {
		return this.getHostRuntimes(null);
	}

	/**
	 * Get hostRuntime with specified hostName.
	 * 
	 * @param hostName
	 *          Name of host.
	 * @return <code>HostRuntimeEntry</code> object or <code>null</code> if not
	 *         found.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws DataRuntimeException
	 *           If internal error occurred.
	 */
	public synchronized HostRuntimeEntry getHostRuntimeByName(String hostName) {
		/* Check input parameters. */
		if (hostName == null) {
			throw new NullPointerException("hostName is null");
		}

		/* Get and check result... */
		HostRuntimeEntry[] hostRuntimeEntries = this.getHostRuntimes(hostName);
		if (hostRuntimeEntries.length > 1) {
			throw new DataRuntimeException("There is more then one hostRuntime " + "with hostName \"" + hostName + "\"");
		}
		if (hostRuntimeEntries.length == 0) {
			return null;
		}

		/* Return the first entry. */
		return hostRuntimeEntries[0];
	}

	/**
	 * Registers a host runtime.
	 * 
	 * @param hostRuntime
	 */
	public synchronized void addHostRuntime(HostRuntimeEntry hostRuntime) {
		if (this.data.getHostRuntime(hostRuntime.getHostName()) == null) {
			this.data.addHostRuntime(hostRuntime);
		}

		/* Add hostRuntime to rescue. */
		// this.rescue.addHostRuntime(hostRuntime);
	}

	/**
	 * Unregisters a host runtime.
	 * 
	 * @param hostName
	 *          Name of the host.
	 * @throws IllegalArgumentException
	 *           if removing failed.
	 */
	public synchronized void removeHostRuntime(String hostName) throws IllegalArgumentException {
		/*
		 * Remove all tasks from removed hostRuntime but leave them in
		 * TaskManager (if task is not FINISHED then set its state to ABORTED)
		 */
		TaskEntryImplementation[] tasks = this.data.getTasks(null, null, hostName);
		for (TaskEntryImplementation task : tasks) {
			// if (task.getState() != TaskState.FINISHED) {
			// task.setState(TaskState.ABORTED);
			// }
			this.data.unlinkTaskWithHostRuntime(task);
		}
		this.data.removeHostRuntime(hostName);

		/* Remove hostRuntime from rescue. */
		// this.rescue.remHostRuntime(hostRuntime.getHostName());
	}

}
