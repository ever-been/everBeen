/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager.examples;

import java.rmi.Naming;
import java.rmi.RemoteException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;

import cz.cuni.mff.been.hostmanager.database.HostGroup;

import cz.cuni.mff.been.hostmanager.OperationHandle;
import cz.cuni.mff.been.hostmanager.HostOperationStatus;

/**
 * This example shows how to add host to the Host Manager's database remotely using addHost method.
 * Localhost will be scanned and its data will be added to the database.
 * After host is added  it is refreshed several times. This example also shows how you can use
 * OperationHandle to retrieve information about progress of the add/refresh operation.
 *
 * @author Branislav Repcek
 */
public class AddHost {
	
	/**
	 * Default path to the Host Manager - it is assumed that it runs on the localhost.
	 */
	private static final String DEFAULT_MANAGER_URL = "localhost:" + RMI.REGISTRY_PORT;
	
	/**
	 * Maximum time to wait for the host to be included in database in milliseconds.
	 */
	private static final long MAX_WAIT_TIME = 40000;
	
	/**
	 * How many host refreshes we want.
	 */
	private static final long REFRESH_COUNT = 4;
	
	/**
	 * How long we will wait for refresh to end. 
	 */
	private static final long REFRESH_WAIT = 40000;
	
	/**
	 * Entry point of application.
	 * 
	 * @param args Command line arguments. Only one argument is supported - URL to the host on which HM
	 *        is running.
	 *        
	 * @throws RemoteException If RMI error occurred.
	 * @throws UnknownHostException Host to add to the database has not been found.
	 */
	public static void main(String []args) throws RemoteException, UnknownHostException {
	
		String managerURL = DEFAULT_MANAGER_URL;
		
		/* set path to the manager from the commandline.
		 */
		if (args.length > 0) {
			managerURL = args[0];
		}
		
		/* Connect to the Host Manager
		 */
		HostManagerInterface manager = null;
		
		try {
			manager = (HostManagerInterface) Naming.lookup("rmi://" + managerURL + HostManagerInterface.URL);
		} catch (Exception e) {
			System.err.println("Unable to connect to the Host Manager.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		// Get default group.
		HostGroup defaultGroup = null;
		
		try {
			defaultGroup = manager.getGroup(HostGroup.DEFAULT_GROUP_NAME);
		} catch (Exception e) {
			System.err.println("Unable to query default group.");
			System.err.println("Error message: " + e.getMessage());
		}
		
		// Write out hosts in the default group.
		System.out.println("Default group:");
		System.out.println(defaultGroup);
		
		System.out.println();
		System.out.println("Executing detector task on selected host...");
		long timeStart = System.currentTimeMillis();
		
		String hostName = InetAddress.getLocalHost().getCanonicalHostName();
		OperationHandle handle = null;
		
		// Add localhost to the database. addHost call is not blocking.
		try {
			handle = manager.addHost(hostName);
		} catch (Exception e) {
			System.err.println("Error adding localhost to the database.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		System.out.println("Waiting for the host to appear in database.");

		// wait for change of host's status or until we wait too long
		waitForStatus(manager, handle, MAX_WAIT_TIME);
		
		System.out.println();
		
		long timeDelta = System.currentTimeMillis() - timeStart;
		
		System.out.println("Total time: " + timeDelta + " ms.");
		
		defaultGroup = null;
		
		// Get and write out default group.
		try {
			defaultGroup = manager.getGroup(HostGroup.DEFAULT_GROUP_NAME);
		} catch (Exception e) {
			System.err.println("Unable to query default group 2.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}

		System.out.println("Default group:");
		System.out.println(defaultGroup);

		
		// now we will refresh host several times
		System.out.println("----------------------------------------------");
		System.out.println("Host refresh test");
		System.out.println("Will refresh localhost " + REFRESH_COUNT + " times.");
		System.out.println("Refresh waiting time is " + REFRESH_WAIT + " ms.");
		System.out.println();
		
		for (int i = 0; i < REFRESH_COUNT; ++i) {
			System.out.println("Refresh " + (i + 1));
			
			try {
				handle = manager.refreshHost(hostName);
			} catch (Exception e) {
				System.err.println("Refresh failed.");
				System.err.println("Error message: " + e.getMessage());
			}

			waitForStatus(manager, handle, REFRESH_WAIT);
		}
	}

	/**
	 * Active wait for change of the status for given handle.
	 * 
	 * @param manager Reference to the Host Manager.
	 * @param handle Handle of the operation.
	 * @param timeOut How long to wait.
	 * 
	 * @return Status with which method ended.
	 */
	private static HostOperationStatus waitForStatus(HostManagerInterface manager, OperationHandle handle, long timeOut) {

		long timeStart = System.currentTimeMillis();
		
		HostOperationStatus opStatus = null;
		
		do {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// nothing to do
			}
			
			// get status of add operation
			try {
				opStatus = manager.getOperationStatus(handle);
			} catch (Exception e) {
				System.err.println("Invalid operation handle.");
				System.err.println("Error message: " + e.getMessage());
				return null;
			}

			// write info about status
			System.out.println("Time: " + (System.currentTimeMillis() - timeStart)
			                 + ", Status: " + opStatus.getStatus()
			                 + ", Message: " + opStatus.getMessage());
			
			// operation failed -> exit
			if (opStatus.getStatus() == HostOperationStatus.Status.FAILED) {
				return opStatus;
			}
		} while (!(opStatus.getStatus() == HostOperationStatus.Status.SUCCESS)
				 && (System.currentTimeMillis() < timeStart + timeOut));
		
		// now remove status info since we do not need it anymore
		try {
			manager.removeOperationStatus(handle);
		} catch (Exception e) {
			System.err.println("Error removing status info.");
			System.err.println("Error message: " + e.getMessage());
		}

		return opStatus;
	}
	
	/**
	 * Empty ctor.
	 */
	private AddHost() {
	}
}
