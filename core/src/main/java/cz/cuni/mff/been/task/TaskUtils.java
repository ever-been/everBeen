/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */
package cz.cuni.mff.been.task;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.been.taskmanager.CheckPoint;
import cz.cuni.mff.been.taskmanager.TaskManagerException;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;

/**
 * Class containing utility functions for working with tasks. These functions
 * are used in several modules of the web interface.
 * 
 * @author David Majda
 */
public class TaskUtils {
	/**
	 * Get checkpoints for a list of tasks.
	 * 
	 * @param taskManager Task Manager interface RMI reference
	 * @param tasks list of tasks for which to get the checkpoints
	 * @return map, mapping <code>TaskEntry</code> objects representing the tasks
	 *          to the arrays of <code>CheckPoint</code> objects representing
	 *          checkpoints the task reached
	 * @throws RemoteException if the RMI call to the Task Manager fails
	 */
	public static Map<TaskEntry, CheckPoint[]> getCheckPointsForTasks(
			TaskManagerInterface taskManager, TaskEntry[] tasks)
			throws RemoteException { 
		Map<TaskEntry, CheckPoint[]> result = new HashMap<TaskEntry, CheckPoint[]>();
		for (TaskEntry task: tasks) {
			try {
				result.put(task, taskManager.checkPointLook(
						new CheckPoint(task.getTaskId(), task.getContextId(), null, null),
						0
				));
			} catch (TaskManagerException e) {
				result.put(task, new CheckPoint[0]);
			}
		}
		return result;
	}

	/**
	 * Private constructor so that no instances can be created.
	 */
	private TaskUtils() {
	}
}
