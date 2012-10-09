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
package cz.cuni.mff.been.hostruntime;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.hostmanager.load.LoadMonitorException;

/**
 * Runs the Host Runtime - parses the options, initializes the RMI registry,
 * creates the implementation and binds it to the RMI URL.
 * 
 * @author Antonin Tomecek
 * @author David Majda
 */
public class HostRuntimeRunner {

	private static final Logger logger = LoggerFactory.getLogger(HostRuntimeRunner.class);

	/**
	 * Writes the usage information and exits.
	 */
	private static void writeUsageAndExit() {
		System.out.println("Usage: java cz.cuni.mff.been.hostruntime.HostRuntimeRunner task_manager_host_name root_directory");
		System.exit(1);
	}

	/**
	 * Checks command-line parameters.
	 * 
	 * @param args
	 *          command-line parameters
	 */
	private static void checkParams(String[] args) {
		if (args.length != 2) {
			writeUsageAndExit();
		}
	}

	/**
	 * Initializes the RMI Registry.
	 * 
	 * @throws When
	 *           RMI registry initialization fails.
	 */
	private static void initializeRMIRegistry() throws RemoteException {
		if (LocateRegistry.getRegistry(RMI.REGISTRY_PORT) != null) {
			logger.info("A running RMI registry instance detected. Using it.");
		} else {
			logger.info("Creating a new RMI registry instance.");
			LocateRegistry.createRegistry(RMI.REGISTRY_PORT);
		}
	}

	/**
	 * Main method, which runs the Host Runtime.
	 * 
	 * @param args
	 *          command-line parameters
	 */
	public static void main(String[] args) {
		checkParams(args);
		try {
			initializeRMIRegistry();
		} catch (RemoteException e) {
			logger.error("Failed to obtain RMI registry.", e);
		}
		try {
			new HostRuntimeImplementation(args[0], args[1]);
		} catch (RemoteException e) {
			logger.error("Error executing remote call.", e);
			System.exit(1);
		} catch (LoadMonitorException e) {
			logger.error("Error constructiong HostRuntime.", e);
			System.exit(1);
		} catch (IOException e) {
			logger.error("Error constructiong HostRuntime.", e);
			System.exit(1);
		} catch (NotBoundException e) {
			logger.error(String.format("Can't connect to the Task Manager on host \"%s\".", args[0]), e);
			System.exit(1);
		}
		logger.info("Host Runtime started...");
	}

	/**
	 * Disallow default construction.
	 */
	private HostRuntimeRunner() {}
}
