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

/**
 * Shows manipulation with configuration history of the host in database.
 *
 * @author Branislav Repcek
 */
public class History {
	
	/**
	 * Default path to the Host Manager - it is assumed that it runs on the localhost.
	 */
	private static final String MANAGER_URL_DEFAULT = "localhost:" + RMI.REGISTRY_PORT;
	
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
			manager = (HostManagerInterface) Naming.lookup("rmi://" + managerURL + HostManagerInterface.URL);
		} catch (Exception e) {
			System.err.println("Error connecting to the Host Manager.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
	
		String []hosts = manager.getHostNames();
		
		if (hosts.length == 0) {
			System.out.println("No hosts in database.");
			return;
		}
		
		System.out.println(makeTable2("Host Name", "History Entries", 60));
		for (String current: hosts) {
			try {
				System.out.println(makeTable2(current, 
				                              String.valueOf(manager.getHostHistoryDates(current).length), 
				                              60));
			} catch (Exception e) {
				System.err.println("Unable to query history for host.");
				System.err.println("Error message: " + e.getMessage());
			}
		}
		
		// we will work with the history entries of the first host
		String currentHost = hosts[0];
		
		System.out.println();
		System.out.println("Entries for host: " + currentHost);
		
		SimpleDateFormat format = new SimpleDateFormat(HostManagerInterface.DEFAULT_DATE_TIME_FORMAT);
		
		Date []dates = null;
		
		try {
			dates = manager.getHostHistoryDates(currentHost);
		} catch (Exception e) {
			System.err.println("Unable to query history dates.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		// write out dates of all entries for our host
		for (Date current: dates) {
			System.out.println(format.format(current));
		}
		
		// remove oldest entry
		Date oldest = dates[dates.length - 1];
		System.out.println("Removing last entry: " + format.format(oldest));
		
		try {
			manager.removeHostHistoryEntry(currentHost, oldest);
		} catch (Exception e) {
			System.err.println("Unable to remove history entry.");
			System.err.println("Error message: " + e.getMessage());
		}

		System.out.println();
		System.out.println("Entries for host after removal.");
		System.out.println();
		
		// write dates of entries again, so we can see that the entry has been successfully removed
		Date []datesAfterRemoval = null;
		
		try {
			datesAfterRemoval = manager.getHostHistoryDates(currentHost);
		} catch (Exception e) {
			System.err.println("Unable to query history dates 2.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		for (Date current: datesAfterRemoval) {
			System.out.println(format.format(current));
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
	private History() {
	}
}
