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
package cz.cuni.mff.been.taskmanager;

import java.rmi.Naming;

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;

/**
 * Runner class for adding tasks specified by their TaskDesctriptors to run to
 * the already running Task Manager.
 * 
 * @author Antonin Tomecek
 * 
 */
public class TaskRunner {

	private TaskRunner() {
		// Do nothing... (overwrites default constructor...)
	}

	/* RMI reference to the Task Manager. */
	private static TaskManagerInterface taskManager = null;

	/**
	 * Print list of all tasks (running and waiting) and their states to the
	 * standard output.
	 */
	private static void printTasks() {
		System.out.println("List of currently running and waiting " + "tasks:");

		try {
			TaskEntry[] tasks = taskManager.getTasks();

			for (TaskEntry task : tasks) {
				System.out.println("\t\"" + task.getContextId() + "\" : \""
						+ task.getTaskId() + "\" ("
						+ task.getState().toString() + ")");
			}
		} catch (Exception e) {
			System.err.println("Getting of list of tasks failed: "
					+ e.getMessage());
		}
	}

	/**
	 * @param args
	 *            TaskDescriptors represented by XML files.
	 */
	public static void main(String[] args) {
		/* Check command-line arguments. */
		if (args.length < 1) {
			System.err.println("Usage: java cz.cuni.mff.been."
					+ "taskmanager.TaskRunner -l | "
					+ "<XML_task_descriptor>[ "
					+ "<XML_task_descriptor>[ ...]]");
			System.exit(1);
		}

		try {
			/* Do naming lookup for RMI interface... */
			taskManager = (TaskManagerInterface) Naming.lookup(RMI.URL_PREFIX
					+ TaskManagerInterface.URL);

			if ((args.length == 1) && (args[0].equals("-l"))) {
				/* Do list function. */
				printTasks();
				System.exit(0);
			}

			/* Run new tasks */
			System.out.println("Submitting tasks");
			taskManager.runTask(args);
		} catch (Exception e) {
			System.err.println("Submitting of new tasks failed: "
					+ e.getMessage());
		}
	}
}
