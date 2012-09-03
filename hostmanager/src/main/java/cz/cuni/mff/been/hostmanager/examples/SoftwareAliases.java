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
import cz.cuni.mff.been.common.value.ValueRegexp;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import static cz.cuni.mff.been.services.Names.HOST_MANAGER_REMOTE_INTERFACE_MAIN;

import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.ObjectRestriction;
import cz.cuni.mff.been.hostmanager.database.SoftwareAlias;
import cz.cuni.mff.been.hostmanager.database.SoftwareAliasDefinition;


/**
 * Example showing usage of the Software Aliases API. In this example you can see how to retrieve
 * list of aliases for given host and how to change alias definitions and rebuild database.
 *
 * @author Branislav Repcek
 */
public class SoftwareAliases {

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
	 * @param args Command line arguments. Only one argument is supported - URL to the host on which
	 *        Host Manager is running.
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
		
		// we need some data
		if (manager.getHostCount() == 0) {
			System.out.println("Host database is empty. Please add some hosts to the database.");
			return;
		}
		
		// request data about first host in database
		HostInfoInterface host = null;
		
		try {
			host = manager.getHostInfo(manager.getHostNames()[0]);
		} catch (Exception e) {
			System.err.println("Error querying host data for host " + manager.getHostNames()[0]);
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		System.out.println("Host name: " + host.getHostName());
		System.out.println("Number of alias definitions: " + manager.getAliasDefinitionCount());		
		System.out.println("Number of aliases on host: " + host.getSoftwareAliasCount());
		System.out.println();
		
		// output table with all aliases
		System.out.println(makeTable2("** Alias name **", "** Application match **", 30));
		System.out.println(fillStr("-", 80));
		
		for (int i = 0; i < host.getSoftwareAliasCount(); ++i) {
			SoftwareAlias current = host.getSoftwareAlias(i);
			
			System.out.println(makeTable2(current.getAliasName(), current.getProductName(), 30));
			System.out.println(makeTable2("", current.getProductVendor(), 30));
			System.out.println(makeTable2("", current.getProductVersion(), 30));
		}

		// now add new alias definition
		// we will create alias that will match anything from Sun (so we will get at leas one 
		// hit on Java for every computer)
		SoftwareAliasDefinition sun = new SoftwareAliasDefinition(
				"Sun product", // name of the alias
				"${name}",     // name of application that will be displayed
				"Made by sun (${vendor}", // this will be displayed as a vendor's name
				"${version}",  // version
				null,          // operating system restriction - null means we don't care about OS
				new ObjectRestriction("", "vendor", new ValueRegexp(".*Sun Microsystems.*")));
				               // app restriction - match any app that contains Sun Microsystems
				               // in its vendor name
		
		System.out.println("\n\nAdding new definition...");
		// now add definition
		try {
			manager.addAliasDefinition(sun);
		} catch (Exception e) {
			System.err.println("Error adding new definition.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		System.out.println("Rebuilding database...");
		long start = System.currentTimeMillis(); // we will measure how long it takes
		
		// and rebuild database, you have to call this every time you modify definition list
		// it is not called automatically because it may take a while to finish
		try {
			manager.rebuildAliasTableForAllHosts();
		} catch (Exception e) {
			System.err.println("Error rebuilding database.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		long end = System.currentTimeMillis();
		System.out.println("Database update took " + (end - start) + " ms");
		System.out.println("\n");
		
		// request new data for our host
		HostInfoInterface host2 = null;
		
		try {
			host2 = manager.getHostInfo(host.getHostName());
		} catch (Exception e) {
			System.err.println("Unable to get data for host " + host.getHostName());
			System.err.println("Error message: " + e.getMessage());
			return;
		}
			
		// output aliases after modifications
		System.out.println("Number of alias definitions: " + manager.getAliasDefinitionCount());
		System.out.println("Number of aliases after modifications: " + host2.getSoftwareAliasCount());
		System.out.println();
		System.out.println(makeTable2("** Alias name **", "** Application match **", 30));
		System.out.println(fillStr("-", 80));
		
		for (int i = 0; i < host2.getSoftwareAliasCount(); ++i) {
			SoftwareAlias current = host2.getSoftwareAlias(i);
			
			System.out.println(makeTable2(current.getAliasName(), current.getProductName(), 30));
			System.out.println(makeTable2("", current.getProductVendor(), 30));
			System.out.println(makeTable2("", current.getProductVersion(), 30));
		}
	}
	
	/**
	 * Create string from multiple copies of another string.
	 * 
	 * @param s String which will be copied into result.
	 * @param reps Number of copies of specified string in result.
	 * 
	 * @return String containing given number of copies of given string.
	 */
	private static String fillStr(String s, int reps) {
		
		String res = new String();
		
		for (int i = 0; i < reps; ++i) {
			
			res += s;
		}
		
		return res;
	}
	
	/**
	 * Make line from table with two columns.
	 * 
	 * @param col1 Text in the first column.
	 * @param col2 Text in the second column.
	 * @param c1width Width of the first column in characters.
	 * 
	 * @return Line from the table with given texts in the first and second columns.
	 */
	private static String makeTable2(String col1, String col2, int c1width) {
		
		return col1 + fillStr(" ", c1width - col1.length()) + col2;
	}
	
	/**
	 * Empty ctor.
	 */
	private SoftwareAliases() {
	}
}
