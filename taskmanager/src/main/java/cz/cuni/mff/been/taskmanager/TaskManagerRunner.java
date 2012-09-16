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

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import cz.cuni.mff.been.common.BeenException;
import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.jaxb.XSDRoot;
import cz.cuni.mff.been.logging.LogLevel;

/**
 * Runner class for Task Manager.
 * 
 * @author Antonin Tomecek
 */
public class TaskManagerRunner {

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

	private TaskManagerRunner() {
		// Do nothing... (overwrites default constructor...)
	}

	/**
	 * Configure static members from environment variables.
	 * 
	 * @throws BeenException
	 *           When configuration fails.
	 */
	private static void init() throws BeenException {
		final String beenHome = System.getenv("BEEN_HOME");
		if (beenHome == null) {
			throw new BeenException("Could not locate XSD file root because BEEN_HOME variable is not set.");
		}
		XSD_ROOT = beenHome + "/service_interfaces/src/main/xsd";
	}

	public static void main(String[] args) {
		try {
			init();
		} catch (BeenException e) {
			System.err.println("Task manager initialization failed because:");
			System.err.println(e.getMessage());
		}

		for (String propName : System.getProperties().stringPropertyNames()) {
			System.out.println(String.format("%s = %s", propName, System.getProperties().getProperty(propName)));
		}

		/* Check command-line arguments. */
		if (args.length < 1) {
			System.out.println("Usage: java cz.cuni.mff.been." + "taskmanager.TaskManagerRunner " + "<TRACE | DEBUG | INFO | WARN | ERROR | FATAL> " + "<path_to_TaskManager_directory>[ " + "<XML_task_descriptor>[ " + "<XML_task_descriptor>[ ...]]]");
			System.exit(1);
		}

		/* Number of currently (lastly) processed argument. */
		int argNo = 0;

		/* Set log level. */
		LogLevel level = LogLevel.valueOf(args[0]);
		System.out.println("Log level: " + level);
		argNo = 1;

		System.out.println(String.format("Security manager is %s", System.getSecurityManager()));
		try {
			LocateRegistry.createRegistry(RMI.REGISTRY_PORT);
		} catch (RemoteException e) {
			System.err.println("Note: Can't start the RMI registry - another instance is probably running. " + "Usually you can ignore this.");
		}

		/* This sets where to search for schema files. */
		System.setProperty(XSDRoot.XSD_ROOT, XSD_ROOT);

		TaskManagerInterface taskManager;
		try {
			/* Root directory for Task Manager. */
			String rootDirectory = args[argNo++];

			/* Create a new Task Manager. */
			taskManager = new TaskManagerImplementation(rootDirectory, level);

			/* Print message to the standard output stream. */
			System.out.println("Task manager started...");

			if (args.length - argNo > 0) {
				/* Array containing paths to TaskDescriptors to run. */
				String[] taskDescriptors = new String[args.length - argNo];
				for (int i = 0; i < taskDescriptors.length; i++) {
					taskDescriptors[i] = args[argNo++];
				}

				System.out.println("There are " + taskDescriptors.length + " startup tasks to be run. Waiting for first host runtime...");

				Listener listener = new Listener();
				try {
					taskManager.registerEventListener(listener);
					/* Register main RMI interface of this Task Manager AFTER listener creation. */
					Naming.rebind(RMI.URL_PREFIX + TaskManagerInterface.URL, taskManager);
					synchronized (listener) {
						while (!listener.hasHostRuntime()) {
							try {
								listener.wait();
							} catch (InterruptedException exception) {} // Should not happen.
						}
					}
					System.out.println("First host runtime registered, running startup tasks...");
					taskManager.runTask(taskDescriptors);
					taskManager.unregisterEventListener(listener);
				} catch (RemoteException e) {
					System.err.println("Running of tasks specified by their task descriptors failed: " + e.getMessage());
				}
			} else {
				/* Register main RMI interface of this Task Manager. */
				Naming.rebind(RMI.URL_PREFIX + TaskManagerInterface.URL, taskManager);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Unexpected failure!", ex);
		}
	}
}
