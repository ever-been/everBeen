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
import cz.cuni.mff.been.hostmanager.database.PropertyDescription;
import cz.cuni.mff.been.hostmanager.database.PropertyDescriptionTable;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeReadInterface;

/**
 * This example shows how to use descriptions API to request descriptions and other data about
 * properties and objects in the host database.
 * 
 * @author Branislav Repcek
 */
public class PropertyDescriptions {
	
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
		
		HostInfoInterface hi = null;
		
		try {
			hi = manager.getHostInfo(manager.getHostNames()[0]);
		} catch (Exception e) {
			System.err.println("Error querying host data.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}

		PropertyTreeReadInterface osObject = hi.getOperatingSystem();
		PropertyTreeReadInterface cpuObject = hi.getProcessor(0);
		
		/*
		 * There are two ways of querying description data of the property:
		 *   1. Request Host Manager for description using property name.
		 *   2. Request all descriptions at once and query descriptions from the class returned.
		 * 
		 * Both ways use only property name to query its description.
		 * 
		 * Each way is designed to be used under different circumstances. First method is good if you 
		 * query descriptions only occasionally, since it requires calls over RMI which can be quite 
		 * slow.
		 * Second method is useful when you need to query for descriptions more often. You will 
		 * receive copy of table containing all descriptions and you can request descriptions from 
		 * that table. Since descriptions cannot be modified, it is sufficient to query description 
		 * table once (e.g. at startup of your application). 
		 */
		
		/*
		 * First method - direct query to the Host Manager
		 */
		System.out.println("** Method 1 **");
		System.out.println();
		
		writeProperties1(hi, manager);
		writeProperties1(osObject, manager);
		writeProperties1(cpuObject, manager);
		
		System.out.println("\n");
		
		/*
		 * Second method - request description table first and use it later to get description data
		 */
		PropertyDescriptionTable table = null;

		System.out.println("** Method 2 **");			
		System.out.println();
		
		try {
			// get table from the HM
			table = manager.getPropertyDescriptionTable();
		} catch (Exception e) {
			System.err.println("Unable to obtain description table.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}

		writeProperties2(hi, table);
		writeProperties2(osObject, table);
		writeProperties2(cpuObject, table);
	}
	
	/**
	 * Write properties of the object and their descriptions to the stdout. First method (direct
	 * query to the HM) is used.
	 * 
	 * @param o PropertyTreeReadInterface to write out.
	 * @param manager Reference to the Host Manager.
	 */
	private static void writeProperties1(PropertyTreeReadInterface o, HostManagerInterface manager) {
		
		String objectPath = o.getName(true);
		
		System.out.println(objectPath);
		// Query and output object's description.
		PropertyDescription objectDescription = null;
		
		try {
			objectDescription = manager.getPropertyDescription(objectPath);
		} catch (Exception e) {
			System.err.println("Unable to query description data for \"" + objectPath + "\".");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		System.out.println("  -> description: " + objectDescription.getDescription());
		
		for ( NameValuePair property : o.getProperties() ) {
			String propertyPath = objectPath + "." + property.getName();
			PropertyDescription description = null;
			
			try {
				// Get description directly from the HM.
				description = manager.getPropertyDescription(propertyPath);
			} catch (Exception e) {
				System.err.println("Unable to query description data for \"" + propertyPath + "\".");
				System.err.println("Error message: " + e.getMessage());
				return;
			}
			
			System.out.println("  " + property.getName() + "=" + property.getValue());
			System.out.println("    -> description: " + description.getDescription());
			System.out.println("    -> type: " + description.getType());
			
			String unit = description.getUnit();
			
			System.out.println("    -> unit: " + (unit == null ? "(not applicable)" : unit));
		}
	}
	
	/**
	 * Write properties of the object and their descriptions to the stdout. Second method is used.
	 * 
	 * @param o PropertyTreeReadInterface to write out.
	 * @param table Description table.
	 */
	private static void writeProperties2(PropertyTreeReadInterface o, PropertyDescriptionTable table) {

		String objectPath = o.getName(true);
		
		System.out.println(objectPath);
		// Query description of object using full path. Objects have only description and no unit
		// (they have type, but it's always "object").
		PropertyDescription objectDescription = null;
		
		try {
			objectDescription = table.getDescription(objectPath);
		} catch (Exception e) {
			System.err.println("Unable to query description data for \"" + objectPath + "\".");
			System.err.println("Error message: " + e.getMessage());
			return;
		}
		
		System.out.println("  -> description: " + objectDescription.getDescription());
		
		for ( NameValuePair property : o.getProperties() ) {
			String propertyPath = objectPath + "." + property.getName();
			PropertyDescription description = null;
			
			try {
				// Get description from the table.
				description = table.getDescription(propertyPath);
			} catch (Exception e) {
				System.err.println("Unable to query description data for \"" + propertyPath + "\".");
				System.err.println("Error message: " + e.getMessage());
				return;
			}
			
			System.out.println("  " + property.getName() + "=" + property.getValue());
			System.out.println("    -> description: " + description.getDescription());
			System.out.println("    -> type: " + description.getType());
			
			String unit = description.getUnit();
			
			System.out.println("    -> unit: " + (unit == null ? "(not applicable)" : unit));
		}
	}
	
	/**
	 * Empty ctor.
	 */
	private PropertyDescriptions() {
	}
}
