/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.been.common.BeenException;
import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.jaxb.XSDRoot;

/**
 * Runner class for Task Manager.
 * 
 * @author Antonin Tomecek
 */
public class TaskManagerRunner {
	/** Logging via slf4j */
	private static final Logger log = LoggerFactory.getLogger(TaskManagerRunner.class);

	/** Root of the {@link TaskManagerRunner} data files */
	private static String dataRoot = null;
	/** List of optional parameters denoting tasks to run */
	private static List<String> tasksToRun = new LinkedList<String>();

	private static class Listener implements HostRuntimeRegistrationListener {

		volatile private boolean hasHostRuntime = false; // Doesn't have to be volatile.

		public boolean hasHostRuntime() {
			return hasHostRuntime;
		}

		@Override
		public synchronized void hostRuntimeRegistered(String hostname) { // Must be synchronized.
			hasHostRuntime = true;
			notify();
		}

		@Override
		public void hostRuntimeUnregistered(String hostname) {
			// Don't care			
		}
	}

	/** Root directory to search for XML Schema files. */
	private static String XSD_ROOT;

	/**
	 * Disallow default construction.
	 */
	private TaskManagerRunner() {}

	/**
	 * Run the task manager.
	 * 
	 * @param args
	 *          Command-line arguments.
	 */
	public static void main(String[] args) {
		try {
			processArgs(args);
		} catch (TaskManagerException e) {
			log.error("Incorrect invocation.", e.getMessage());
			printUsage();
		}

		try {
			LocateRegistry.createRegistry(RMI.REGISTRY_PORT);
		} catch (RemoteException e) {
			log.error("Note: Can't start the RMI registry - another instance is probably running.");
			log.error("Usually you can ignore this.");
		}

		try {
			init();
		} catch (BeenException e) {
			log.error("Task manager initialization failed.", e);
		}

		log.info("Task manager started!");

	}
	/**
	 * Process command-line arguments.
	 * 
	 * @param args
	 *          Command-line arguments to process.
	 */
	private static void processArgs(String[] args) throws TaskManagerException {
		if (args.length < 1) {
			throw new TaskManagerException("Too few arguments");
		}

		dataRoot = args[0];

		for (int i = 1; i < args.length; ++i) {
			tasksToRun.add(args[i]);
		}

		log.debug(String.format("Data directory root is \"%s\".", dataRoot));
		log.debug(String.format("Paths to boot task descriptors are %s.", tasksToRun));
	}

	/**
	 * Print this executable's usage information.
	 */
	private static void printUsage() {
		log.error(String.format("Usage: %s <path_to_TaskManager_directory>[ <XML_task_descriptor>[ <XML_task_descriptor>[ ...]]]"), TaskManagerRunner.class.getName());
	}

	/**
	 * Configure static members from environment variables.
	 * 
	 * @throws TaskManagerException
	 *           When configuration fails.
	 */
	private static void init() throws TaskManagerException {
		final String beenHome = System.getenv("BEEN_HOME");
		if (beenHome == null) {
			throw new TaskManagerException("Could not locate XSD file root because BEEN_HOME variable is not set.");
		}
		XSD_ROOT = beenHome + "/service_interfaces/src/main/xsd";
		System.setProperty(XSDRoot.XSD_ROOT, XSD_ROOT);

		TaskManagerInterface taskManager = null;
		try {
			taskManager = new TaskManagerImplementation(dataRoot);
		} catch (RemoteException e) {
			throw new TaskManagerException(String.format("Failed to instantiate %s.", TaskManagerImplementation.class.getName()), e);
		}

		log.info(String.format("There are %d startup tasks to be run.", tasksToRun.size()));

		if (hasTasksToRun()) {
			Listener listener = startListening(taskManager);
			bindTaskManager(taskManager);
			log.info("Waiting for first host runtime...");
			waitForHostRuntime(listener);
			log.info("First host runtime registered. Running startup tasks...");
			runTasks(taskManager);
			stopListening(taskManager, listener);
			log.info("Tasks successfully scheduled.");
		} else {
			log.info("Nothing to schedule.");
			bindTaskManager(taskManager);
		}
	}

	/**
	 * Check whether this {@link TaskManagerRunner} has any tasks issued from the
	 * command-line.
	 * 
	 * @return <code>true</code> if some tasks should be run, <code>false</code>
	 *         if none
	 */
	private static boolean hasTasksToRun() {
		return !tasksToRun.isEmpty();
	}

	/**
	 * Bind the task manager service as a remotely accessible interface through
	 * RMI.
	 * 
	 * @param taskManager
	 *          Task manager to bind.
	 * @throws TaskManagerException
	 *           On binding error.
	 */
	private static void bindTaskManager(TaskManagerInterface taskManager) throws TaskManagerException {
		String bindingName = RMI.URL_PREFIX + TaskManagerInterface.URL;
		log.debug(String.format("Binding task manager to rmi registry under binding name \"%s\".", bindingName));
		try {
			Naming.rebind(bindingName, taskManager);
		} catch (MalformedURLException e) {
			throw new TaskManagerException("Invalid RMI binding URL.", e);
		} catch (RemoteException e) {
			throw new TaskManagerException("RMI binding failure.", e);
		}
	}

	/**
	 * Wait until the listener is informed of an available host runtime to run the
	 * tasks on.
	 * 
	 * @param listener
	 *          Listener to use for scanning.
	 * 
	 * @throws TaskManagerException
	 *           If the listener is interrupted.
	 */
	private static void waitForHostRuntime(Listener listener) throws TaskManagerException {
		synchronized (listener) {
			while (!listener.hasHostRuntime()) {
				try {
					listener.wait();
				} catch (InterruptedException e) {
					throw new TaskManagerException("Waiting for first host runtime unexpectedly interrupted.", e);
				}
			}
		}
	}

	/**
	 * Run tasks passed via command-line arguments of this
	 * {@link TaskManagerRunner}.
	 * 
	 * @param taskManager
	 *          Task manager to use for scheduling.
	 * 
	 * @throws TaskManagerException
	 *           In case of error during task scheduling.
	 */
	private static void runTasks(TaskManagerInterface taskManager) throws TaskManagerException {
		try {
			taskManager.runTasks(tasksToRun);
		} catch (RemoteException e) {
			throw new TaskManagerException("Failure scheduling tasks issued via command-line.", e);
		}
	}

	/**
	 * Hook host runtime registration listener.
	 * 
	 * @param taskManager
	 *          Listening task manager.
	 * 
	 * @return The listener object.
	 * 
	 * @throws TaskManagerException
	 *           On error when hooking the listener object.
	 */
	private static Listener startListening(TaskManagerInterface taskManager) throws TaskManagerException {
		Listener listener = new Listener();
		try {
			taskManager.registerEventListener(listener);
		} catch (RemoteException e) {
			throw new TaskManagerException("Failed to hook host runtime registration listener.", e);
		}
		return listener;
	}

	/**
	 * Unhook host runtime registration listener.
	 * 
	 * @param taskManager
	 *          Listening task manager.
	 * @param listener
	 *          The listener object.
	 * 
	 * @throws TaskManagerException
	 *           On error when unhooking the listener object.
	 */
	private static void stopListening(TaskManagerInterface taskManager,
			Listener listener) throws TaskManagerException {
		try {
			taskManager.unregisterEventListener(listener);
		} catch (RemoteException e) {
			throw new TaskManagerException("Failed to unhook host runtime registration listener", e);
		}
	}
}
