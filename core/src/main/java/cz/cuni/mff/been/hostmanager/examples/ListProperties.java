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
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.NameValuePair;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeReadInterface;
import cz.cuni.mff.been.hostmanager.value.ValueBasicInterface;

/**
 * This class represents simple application which connects to the Host Manager and retrieves list of all
 * properties and their values of the first host in the database.   
 *
 * @author Branislav Repcek
 */
public class ListProperties {

	/**
	 * Default path to the Host Manager - it is assumed that it runs on the localhost.
	 */
	public static final String MANAGER_URL_DEFAULT = "localhost:" + RMI.REGISTRY_PORT;
	
	/**
	 * Write properties to the stdout.
	 * 
	 * @param o PropertyTreeReadInterface to write out.
	 * @param depth Indentation depth.
	 */
	private static void writePropertyTree(PropertyTreeReadInterface o, int depth) {
		
		indent(depth);
		System.out.println(o.getName(true));
		
		for ( NameValuePair property : o.getProperties() ) {
			indent( depth + 1 );
			
			if ( property.getValue() instanceof ValueBasicInterface< ? > ) {
				String unit = ( (ValueBasicInterface< ? >) property.getValue() ).getUnit();
				System.out.println( property + ( unit == null ? "" : " " + unit ) );
			} else {
				System.out.println( property );
			}
		}
		
		for ( PropertyTreeReadInterface object : o.getObjects() ) {			
			writePropertyTree( object, depth + 1 );
		}		
	}
	
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
		
		/* set path to the manager from the command line.
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

		/* Get data about the first host.
		 */
		HostInfoInterface hi = null;
		
		try {
			hi = manager.getHostInfo(manager.getHostNames()[0]);
		} catch (Exception e) {
			System.err.println("Error querying host data.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}

		writePropertyTree(hi, 0);
	}
	
	/**
	 * Indent line.
	 * 
	 * @param d Amount to indent.
	 */
	private static void indent(int d) {

		for (int i = 0; i < d; ++i) {
			System.out.print("  ");
		}
	}
	
	/**
	 * Empty ctor.
	 */
	private ListProperties() {
	}
}
