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

import java.text.SimpleDateFormat;

import java.util.Date;

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import static cz.cuni.mff.been.services.Names.HOST_MANAGER_REMOTE_INTERFACE_MAIN;


import cz.cuni.mff.been.hostmanager.database.HostGroup;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;

/**
 * Enumerate properties of the host in database.
 * 
 * @author Branislav Repcek
 */
public class Enumerate {
	
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
		
		/* Connect to the Host Manager
		 */
		HostManagerInterface manager = null;
		
		try {
			manager = (HostManagerInterface) Naming.lookup("rmi://" + managerURL + INTERFACE_URL);
		} catch (Exception e) {
			System.err.println("Error connecting to the Host Manager.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}

		System.out.println("**** Hosts ****");
		System.out.println(makeTable3("Host name", "Date & Time", "History entries", 30, 30));
		
		String []hosts = manager.getHostNames();
		
		for (String host: hosts) {
			HostInfoInterface hi = null;
			Date[]   history = null;
			
			try {
				hi = manager.getHostInfo(host);
				history = manager.getHostHistoryDates(host);
			} catch (Exception e) {
				System.err.println("Unable to query history data.");
				System.err.println("Error message: " + e.getMessage());
				continue;
			}
			
			if (history.length > 0) {
				SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm.ss");
				
				boolean first = true;
				for (Date d: history) {
					System.out.println(makeTable3(first ? hi.getHostName() : "", 
					                              first ? hi.getCheckDate() + " " + hi.getCheckTime() : "",
					                              formater.format(d),
					                              30,
					                              30));
					first = false;
				}
			} else {
				System.out.println(makeTable3(hi.getHostName(),
				                              hi.getCheckDate() + " " + hi.getCheckTime(),
				                              "(none)",
				                              30,
				                              30));
			}
		}
		
		System.out.println();
		System.out.println("**** Groups ****");
		System.out.println(makeTable2("Group name", "Hosts", 30));

		String []groups = manager.getGroupNames();
		
		for (String group: groups) {
			
			HostGroup grp = null;
			
			try {
				grp = manager.getGroup(group);
			} catch (Exception e) {
				System.err.println("Unable to query group.");
				System.err.println("Error message: " + e.getMessage());
				continue;
			}

			boolean first = true;
			for (String host: grp) {
				
				System.out.println(makeTable2(first ? grp.getName() : "", host, 30));
				first = false;
			}
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
	 * Make line from table with three columns.
	 * 
	 * @param col1 Text in the first column.
	 * @param col2 Text in the second column.
	 * @param col3 Text in the third column.
	 * @param c1width Width of the first column in characters.
	 * @param c2width Width of the second column in characters.
	 * 
	 * @return Line from the table containing given texts for all three columns.
	 */
	private static String makeTable3(String col1, String col2, String col3, int c1width, int c2width) {
		
		return col1 + fillStr(" ", c1width - col1.length())
		       + col2 + fillStr(" ", c2width - col2.length())
		       + col3;
	}
	
	/**
	 * Empty ctor.
	 */
	private Enumerate() {
	}
}
