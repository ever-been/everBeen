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

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.HostManagerService;

import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.RSLRestriction;
import cz.cuni.mff.been.hostmanager.database.RestrictionInterface;

/**
 * Simple restriction test.
 *
 * @author Branislav Repcek
 */
public class RestrictionsTest {

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
	 * @throws Exception If some error occurred.
	 */
	public static void main(String []args) throws Exception {

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

		RestrictionInterface []restr = 
			new RSLRestriction[] {new RSLRestriction("name!=\"www.google.com\"")};
		
		HostInfoInterface []result = manager.queryHosts(restr);

		System.out.println("Number of hosts matched: " + result.length);
		System.out.println("Hosts:");
		for (HostInfoInterface h: result) {
			System.out.println("  " + h.getHostName());
		}
	}
	
	/**
	 * Empty ctor.
	 */
	private RestrictionsTest() {
	}
}
