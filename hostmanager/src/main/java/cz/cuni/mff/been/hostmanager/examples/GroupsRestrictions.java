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
import cz.cuni.mff.been.common.value.ValueInteger;
import cz.cuni.mff.been.common.value.ValueRange;
import cz.cuni.mff.been.common.value.ValueRegexp;
import cz.cuni.mff.been.common.value.ValueString;
import cz.cuni.mff.been.common.value.ValueType;
import cz.cuni.mff.been.common.value.ValueVersion;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import static cz.cuni.mff.been.services.Names.HOST_MANAGER_REMOTE_INTERFACE_MAIN;

import cz.cuni.mff.been.hostmanager.database.AlternativeRestriction;
import cz.cuni.mff.been.hostmanager.database.DiskDrive;
import cz.cuni.mff.been.hostmanager.database.DiskPartition;
import cz.cuni.mff.been.hostmanager.database.HostGroup;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.Memory;
import cz.cuni.mff.been.hostmanager.database.NameValuePair;
import cz.cuni.mff.been.hostmanager.database.ObjectRestriction;
import cz.cuni.mff.been.hostmanager.database.Processor;
import cz.cuni.mff.been.hostmanager.database.Product;
import cz.cuni.mff.been.hostmanager.database.RestrictionInterface;


/**
 * This class represents simple application which creates group based on set of conditions.
 * The group called "compilers" is created. This group will contain only hosts on which
 * some kind of compiler is installed (MSVC .NET+, gcc 2.8+). 
 * This group is then added to the database. 
 *
 * @author Branislav Repcek
 */
public class GroupsRestrictions {

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
			System.err.println("Unable to connect to the Host Manager.");
			System.err.println("Error message: " + e.getMessage());
			return;
		}

		/* Create restriction on the processor: we want compilation host to have CPU faster than 1600 MHz.
		 */
		NameValuePair cpu = 
			new NameValuePair(Processor.Properties.SPEED, 
			                  new ValueRange< ValueInteger >(new ValueInteger(1600), null, ValueType.INTEGER));
		ObjectRestriction cpuRestr = 
			new ObjectRestriction(HostInfoInterface.Objects.PROCESSOR + "(?)", cpu);

		/* Create restriction on the memory of the host - we want it to be bigger than 512 MB.
		 */
		NameValuePair memory = 
			new NameValuePair(Memory.Properties.PHYSICAL_MEMORY_SIZE, 
			                  new ValueRange< ValueInteger >(new ValueInteger(536870912L), null, ValueType.INTEGER));
		ObjectRestriction memRestr = 
			new ObjectRestriction(HostInfoInterface.Objects.MEMORY, memory);

		/* Create restriction on the free space left - we want at least one partition to have more than
		 * 1 GB free. 
		 */
		NameValuePair disk = 
			new NameValuePair(DiskPartition.Properties.FREE_SPACE, 
			                  new ValueRange< ValueInteger >(new ValueInteger(10737418240L), null, ValueType.INTEGER));
		ObjectRestriction diskRestr = 
			new ObjectRestriction(HostInfoInterface.Objects.DRIVE + "(?)."
			                    + DiskDrive.Objects.PARTITION + "(?)", disk);

		/* Create restriction on the Microsoft Visual Studio compiler - we want at least version 7.1 (that
		 * is at least MSVS .NET).
		 */
		NameValuePair []msvc = 
			{
				new NameValuePair(Product.Properties.NAME, 
				                  new ValueRegexp(".*Visual Studio.*")),
				new NameValuePair(Product.Properties.VENDOR, 
				                  new ValueString(".*Microsoft.*")),
				new NameValuePair(Product.Properties.VERSION, 
		                          new ValueRange< ValueVersion >(new ValueVersion("7.1"), null, ValueType.VERSION))
			};
		ObjectRestriction msvcRestr = 
			new ObjectRestriction(HostInfoInterface.Objects.APPLICATION + "(?)", msvc);

		/* Create restriction on the gcc compiler - we want at least version 2.8.
		 */
		NameValuePair []gcc = 
			{
				new NameValuePair(Product.Properties.NAME, 
				                  new ValueString("gcc")),
				new NameValuePair(Product.Properties.VERSION, 
				                  new ValueRange< ValueVersion >(new ValueVersion("2.8"), null, ValueType.VERSION))
			};
		ObjectRestriction gccRestr = 
			new ObjectRestriction(HostInfoInterface.Objects.APPLICATION + "(?)", gcc);

		/* Now create alternative restriction which will accept either MSVS or gcc (or both).
		 */
		AlternativeRestriction someCompiler = 
			new AlternativeRestriction(msvcRestr, gccRestr);
		
		/* Build array of all restrictions. Host needs to pass all of them to be included in the resulting
		 * group.  
		 */
		RestrictionInterface []restrictions = 
			{
				cpuRestr,
				memRestr,
				diskRestr,
				someCompiler
			};
		
		/* Create group based on our restrictions.
		 */
		do {
			HostGroup group = null;
			
			try {
				group = manager.createGroup(restrictions, "compilers");
			} catch (Exception e) {
				System.err.println("Unable to create compilers group.");
				System.err.println("Error message: " + e.getMessage());
				break;
			}
	
			group.setDescription("Compilers");
			
			System.out.println("Number of groups in database: " + manager.getGroupCount());
			System.out.println("Hosts in new group:");
			System.out.println(group);
			System.out.println();
	
			/* Output hosts in our group.
			 */
			System.out.println("Adding group...");

			/* Add group to the database.
			 */
			try {
				manager.addGroup(group);
			} catch (Exception e) {
				System.err.println("Unable to add compilers group to the database.");
				System.err.println("Error message: " + e.getMessage());
				break;
			}
			
			System.out.println("Number of groups in database: " + manager.getGroupCount());
		} while (false);
	}
	
	/**
	 * Empty ctor.
	 */
	private GroupsRestrictions() {
	}
}
