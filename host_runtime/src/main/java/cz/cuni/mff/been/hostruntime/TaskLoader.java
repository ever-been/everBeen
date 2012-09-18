/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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
package cz.cuni.mff.been.hostruntime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskException;

/**
 * Loads a BEEN task and starts it. The task is given as a command parameter and
 * is started in a correct way depending on whether it's a Job or a Service.
 * 
 * @author Jaroslav Urban
 */
public class TaskLoader {

	private static final Logger logger = LoggerFactory
			.getLogger(TaskLoader.class);

	/** The loaded task. */
	protected Task task;

	/**
	 * Loads a task.
	 * 
	 * @param taskClass
	 *            full classname of the task.
	 */
	protected void loadTask(String taskClass) {
		// load the task's class
		Class<?> clazz = null;
		try {
			clazz = Class.forName(taskClass);
		} catch (ClassNotFoundException e) {
			logger.error("Task class not found.", e);
			System.exit(Task.EXIT_CODE_ERROR);
		}

		// create instance of the task
		try {
			task = (Task) clazz.newInstance();
		} catch (InstantiationException e) {
			logger.error(
					String.format(
							"Cannot create instance of the task \"%s\". Possible reasons: no nullary constructor,"
									+ " the class class cannot be instantiated (is abstract, interface, etc)"
									+ " or instantiation failed for some other reason",
							taskClass), e.getMessage());
			System.exit(Task.EXIT_CODE_ERROR);
		} catch (IllegalAccessException e) {
			logger.error(
					String.format(
							"Cannot create instance of the task \"%s\". Possible reasons: "
									+ "class or it's nullary contructor are not accessible.",
							taskClass), e);
			System.exit(Task.EXIT_CODE_ERROR);
		}
	}

	/**
	 * Runs the task. It uses the correct way for a Job or a Service.
	 * 
	 */
	protected void runTask() {
		if (task instanceof Job) {
			try {
				((Job) task).runJob();
			} catch (TaskException e) {
				logger.error("Cannot run task.", e);
				task.logFatal(e.getMessage());
				System.exit(Task.EXIT_CODE_ERROR);
			}
			System.exit(Task.EXIT_CODE_SUCCESS);
		} else if (task instanceof Service) {
			try {
				((Service) task).startService();
			} catch (TaskException e) {
				logger.error("Cannot start service.", e);
				System.exit(Task.EXIT_CODE_ERROR);
			}
		} else {
			logger.error(String.format(
					"The loaded class is not a Job nor a Service: %s", task
							.getClass().getCanonicalName()));
			System.exit(Task.EXIT_CODE_ERROR);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			logger.error("You must give the task's classname as a command line parameter");
			System.exit(Task.EXIT_CODE_ERROR);
		}

		String taskClass = args[0];
		TaskLoader loader = new TaskLoader();
		loader.loadTask(taskClass);
		loader.runTask();
	}
}
