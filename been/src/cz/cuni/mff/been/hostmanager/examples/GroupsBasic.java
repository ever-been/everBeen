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
import cz.cuni.mff.been.hostmanager.HostManagerService;

import cz.cuni.mff.been.hostmanager.database.HostGroup;

/**
 * This class represents simple application which manipulates groups in Host Manager.
 * Basic operation with groups are shown in this example.
 * 
 * @author Branislav Repcek
 */
public class GroupsBasic {

	/**
	 * Default path to the Host Manager - it is assumed that it runs on the localhost.
	 */
	public static final String MANAGER_URL_DEFAULT = "localhost:" + RMI.REGISTRY_PORT;

	/**
	 * RMI path to the HM interface on the host. 
	 */
	public static final String INTERFACE_URL = "/been/hostmanager/" + HostManagerService.REMOTE_INTERFACE_MAIN; 

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
		
		/* Connect to the Host Manager
		 */
		HostManagerInterface manager = null;
		
		try {
			manager = (HostManagerInterface) Naming.lookup("rmi://" + managerURL + INTERFACE_URL);
		} catch (Exception e) {
			System.err.println("Unable to connect to the Host Manager.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}

		/* Output all hosts in all groups.
		 */
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
				System.out.println("Unable to find group " + cur);
			}
		}
		
		System.out.println();
		
		/* How to modify group:
		 *  1. Retrieve group from the Host Manager
		 *  2. Change group (in this case, remove one host)
		 *  3. Update group in the database.
		 */
		do {
			/* Get group from HM.
			 */
			HostGroup compilers = null;
			
			try {
				compilers = manager.getGroup("compilers");
			} catch (Exception e) {
				System.err.println("Unable to find compilers group.");
				break;
			}
		
			/* Remove first host from the group.
			 */
			compilers.removeHost(compilers.iterator().next());
			
			/* Update group in the database.
			 */
			try {
				manager.updateGroup(compilers);
			} catch (Exception e) {
				System.err.println("Unable to update group.");
				System.err.println("Error message: " + e.getMessage());
				break;
			}			
				
			/* Write members of the group in the DB to see it has changed.
			 */
			System.out.println("\"compilers\" group after modification:");
			System.out.println("Metadata: " + compilers.getMetadata());
			System.out.println(compilers);
			System.out.println();				
		} while (false); // so we loop only once, this is "hack" so we can use break in the exception handlers
		
		// Rename "compilers" group to "dev"
		try {
			manager.renameGroup("compilers", "dev");
		} catch (Exception e) {
			System.err.println("Unable to rename group.");
			System.err.println("Error message: " + e.getMessage());
		}
		
		/* Now we try to modify default group.
		 * Only metadata will change in database.
		 */
		do {
			/* Get default group from HM.
			 */
			HostGroup allHosts = null;
			
			try {
				allHosts = manager.getGroup(HostGroup.DEFAULT_GROUP_NAME);
			} catch (Exception e) {
				System.err.println("Unable to query default group.");
				break;
			}
			
			System.out.println("\"" + HostGroup.DEFAULT_GROUP_NAME + "\" group before modification:");
			System.out.println("Metadata: " + allHosts.getMetadata());
			System.out.println("Default: " + allHosts.isDefaultGroup());
			System.out.println(allHosts);
			System.out.println();
			
			/* Remove first host from the group.
			 */
			allHosts.removeHost(allHosts.iterator().next());
			
			/* Change metadata.
			 */
			allHosts.setMetadata("This is modified.");
			
			/* Update group.
			 */
			try {
				manager.updateGroup(allHosts);
			} catch (Exception e) {
				System.err.println("Unable to update default group.");
				System.err.println("Error message: " + e.getMessage());
				break;
			}
			
			/* Write similar info as before.
			 */
			try {
				allHosts = manager.getGroup(HostGroup.DEFAULT_GROUP_NAME);
			} catch (Exception e) {
				System.err.println("Unable to query default group 2.");
				break;
			}

			System.out.println("\"" + HostGroup.DEFAULT_GROUP_NAME + "\" group after modification:");
			System.out.println("Metadata: " + allHosts.getMetadata());
			System.out.println(allHosts);
			System.out.println();
		} while (false); // again...
	}
	
	/**
	 * Empty ctor.
	 */
	private GroupsBasic() {
	}
}
