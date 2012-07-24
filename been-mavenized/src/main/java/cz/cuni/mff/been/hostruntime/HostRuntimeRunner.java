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
	/**
	 * Writes the usage information and exits. 
	 */
	private static void writeUsageAndExit() {
		System.out.println("Usage: "
				+ "java cz.cuni.mff.been.hostruntime.HostRuntimeRunner "
				+ "task_manager_host_name root_directory");
		System.exit(1);
	}
	
	/**
	 * Checks command-line parameters.
	 * 
	 * @param args command-line parameters
	 */
	private static void checkParams(String[] args) {
		if (args.length != 2) {
			writeUsageAndExit();
		}
	}
	
	/**
	 * Initializes the RMI Registry.
	 */
	private static void initializeRMIRegistry() {
		try {
			LocateRegistry.createRegistry(RMI.REGISTRY_PORT);
		} catch (RemoteException e) {
			System.err.println("Note: Can't start the RMI registry - another instance is probably running.");
		}
	}

	/**
	 * Main method, which runs the Host Runtime.
	 * 
	 * @param args command-line parameters
	 */
	public static void main(String[] args) {
		checkParams(args);
		initializeRMIRegistry();
		try {
			new HostRuntimeImplementation(args[0], args[1]);
		} catch (RemoteException e) {
			System.err.println("Error executing remote call ("+e.getMessage()+")");
			System.exit(1);
		} catch (LoadMonitorException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (NotBoundException e) {
			System.err.println("Can't connect to the Task Manager on host \""
				+ args[0] + "\".");
			System.exit(1);
		}
		System.out.println("Host Runtime started...");
	}
	
	/**
	 * Private construcor is so no instances can be created.
	 */
	private HostRuntimeRunner() {
	}
}
