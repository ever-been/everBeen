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
import cz.cuni.mff.been.common.value.ValueBoolean;
import cz.cuni.mff.been.common.value.ValueDouble;
import cz.cuni.mff.been.common.value.ValueInteger;
import cz.cuni.mff.been.common.value.ValueList;
import cz.cuni.mff.been.common.value.ValueRange;
import cz.cuni.mff.been.common.value.ValueRegexp;
import cz.cuni.mff.been.common.value.ValueString;
import cz.cuni.mff.been.common.value.ValueType;
import cz.cuni.mff.been.common.value.ValueVersion;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.NameValuePair;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeReadInterface;

/**
 * This class represents simple application which connects to the Host Manager and retrieves list of all
 * properties and their values of the first host in the database.   
 *
 * @author Branislav Repcek
 */
public class UserProperties {

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
			System.out.println( property );
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

		PropertyTreeInterface userProps = hi.getUserPropertiesObject();
		
		System.out.println("User properties before modification:");
		writePropertyTree(userProps, 0);
		System.out.println("=======================================================");
		
		// now put new property: current time
		try {
			userProps.putProperty("boolean", new ValueBoolean(false));
			userProps.putProperty("time", new ValueInteger(System.currentTimeMillis()));
			userProps.putProperty("string", new ValueString("A gulocka urobila zblnk (bez diakritiky :-( )..."));
			userProps.putProperty("double", new ValueDouble(1.4142));
			userProps.putProperty("regexp", new ValueRegexp("(regular)+expression"));
			userProps.putProperty("version", new ValueVersion("7.3.1 rc8"));
			userProps.putProperty(
				"rangeInt",
				new ValueRange< ValueInteger >(
					new ValueInteger(7),
					null,
					false,
					false,
					ValueType.INTEGER
				)
			);
			userProps.putProperty(
				"listString",
				new ValueList< ValueString >(
					new ValueString[] {
						new ValueString("s1"),
						new ValueString("ss1"),
						new ValueString("lol <!--")
					},
					ValueType.STRING
				)
			);
		} catch (Exception e) {
			System.err.println("Unable to add new properties, message: " + e.getMessage());
			return;
		}
		
		try {
			manager.updateUserProperties(hi);
		} catch (Exception e) {
			System.err.println("Unable to modify properties, message: " + e.getMessage());
			return;
		}
		
		// now request data again from database
		String hostName = hi.getHostName();
		HostInfoInterface newHostData = null;
		
		try {
			newHostData = manager.getHostInfo(hostName);
			// write new properties
			writePropertyTree(newHostData.getUserPropertiesObject(), 0);
		} catch (Exception e) {
			System.err.println("Unable to get data from HM, message: " + e.getMessage());
		}
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
	private UserProperties() {
	}
}
