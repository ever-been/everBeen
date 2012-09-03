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

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import static cz.cuni.mff.been.services.Names.HOST_MANAGER_REMOTE_INTERFACE_MAIN;
import cz.cuni.mff.been.hostmanager.HostQueryCallbackInterface;

import cz.cuni.mff.been.hostmanager.database.DiskDrive;
import cz.cuni.mff.been.hostmanager.database.HostGroup;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;

/**
 * Class which will filter out host which do not have sufficient total disk space.
 *
 * @author Branislav Repcek
 */
class HostFilter implements HostQueryCallbackInterface {

	private static final long	serialVersionUID	= 8931687949644411933L;

	/**
	 * Create HostFilter class which will accept only host with total disk space size greater than given value.

	   @param minSize Minimum size of disk space on host. Host with smaller disk space will not be accepted.
	 */
	public HostFilter(long minSize) {

		this.minSize = minSize;
	}

	/**
	 * Test whether host has sufficient disk size.
	 */
	public boolean match(HostInfoInterface hi) throws Exception {

		long totalSize = 0;

		// Scan all drives
		for (int i = 0; i < hi.getDriveCount(); ++i) {

			DiskDrive drive = hi.getDiskDrive(i);

			totalSize += drive.getSize();
		}

		return (totalSize >= minSize);
	}

	private long minSize;
}

/**
 * This class models simple application which connects to the Host Manager and creates groups using
 * class implementing HostQueryCallbackInterface.
 * In this example we will create two groups based on size of total disk space available on hosts.
 * First group will contain only hosts with disk space greater than 200 GB, second group will contain
 * only hosts with disk space greater than 500 GB.  
 *
 * @author Branislav Repcek
 */
public class GroupsQueryInterface {

	/**
	 * Default path to the Host Manager - it is assumed that it runs on the localhost.
	 */
	public static final String MANAGER_URL_DEFAULT = "localhost:" + RMI.REGISTRY_PORT;

	/**
	 * RMI path to the HM interface on the host. 
	 */
	public static final String INTERFACE_URL = "/been/hostmanager/" + HOST_MANAGER_REMOTE_INTERFACE_MAIN;
		
	/**
	 * Entry point of application.
	 * 
	 * @param args Command line arguments. Only one argument is supported - URL to the host on which HM
	 *        is running.
	 *        
	 * @throws RemoteException If RMI error occurred.
	 */
	public static void main(String []args) throws RemoteException {

		String managerURL = MANAGER_URL_DEFAULT;

		/* set path to the manager from the commandline.
		 */
		if (args.length > 0) {
			managerURL = args[0];
		}

		/* Connect to Host Manager
		 */
		HostManagerInterface manager = null;
		
		try {
			manager = (HostManagerInterface) Naming.lookup("rmi://" + managerURL + INTERFACE_URL);
		} catch (Exception e) {
			System.err.println("Unable to connect to the Host Manager.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}

		System.out.println("Creating groups...");
		
		/* Create group for hosts with more than 200 GB disk space.
		 */
		HostGroup group200 = null;
		
		try {
			group200 = manager.createGroup(new HostFilter(214748364800L), "group_200");
		} catch (Exception e) {
			System.err.println("Unable to create group_200.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		/* Create group for hosts with more than 500 GB disk space.
		 */
		HostGroup group500 = null;
		
		try {
			group500 = manager.createGroup(new HostFilter(536870912000L), "group_500");
		} catch (Exception e) {
			System.err.println("Unable to create group_500.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}


		System.out.println("Adding groups...");
		
		try {
			manager.addGroup(group200);
			System.out.println("group_200 added successfully.");
		} catch (Exception e) {
			System.err.println("Unable to add group_200 to the database.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}

		try {
			manager.addGroup(group500);
			System.out.println("group_500 added successfully.");
		} catch (Exception e) {
			System.err.println("Unable to add group_500 to the database.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		System.out.println();
		System.out.println("Number of groups in database: " + manager.getGroupCount());
		System.out.println();
		
		/* Walk through all of the groups in database and write members to the output.
		 */
		String []groups = manager.getGroupNames();
		
		for (String cur: groups) {
		
			try {
				System.out.println(manager.getGroup(cur));
			} catch (Exception e) {
				System.err.println("Error querying group.");
				System.err.println("Error message: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Empty ctor.
	 */
	private GroupsQueryInterface() {
	}
}
