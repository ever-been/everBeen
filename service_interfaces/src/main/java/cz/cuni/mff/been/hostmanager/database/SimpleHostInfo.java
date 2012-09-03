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

package cz.cuni.mff.been.hostmanager.database;

import java.io.Serializable;

import cz.cuni.mff.been.common.value.ValueList;
import cz.cuni.mff.been.common.value.ValueString;
import cz.cuni.mff.been.common.value.ValueType;

/**
 * This class represents basic informations about host. It contains only info needed to identify host in network.
 * This class should be used when there is no need for complete <code>HostInfo</code> structure (which can be 
 * several KB in size) or when there is need to save network bandwidth.
 *
 * @author Branislav Repcek
 *
 */
public class SimpleHostInfo implements Cloneable, Serializable {
	
	private static final long	serialVersionUID	= 2298632910883969655L;

	/**
	 * Name of host.
	 */
	private String hostName;
	
	/**
	 * Operating system identification string (contains full os name and version).
	 */
	private String operatingSystem;
	
	/**
	 * List of names of groups the host is member of. It will always contain at least default group.
	 */
	private String[] groups;
	
	/**
	 * Size of physical memory installed on host in bytes.
	 */
	private long memory;
	
	/** String containing identification of processor(s) installed on host. For single CPU it has format
	 *  "[cpu name]", for multi-processor systems it is in format "[# of CPUs] x [cpu name]".
	 */
	private String cpuIDString;
	
	/**
	 * Create empty class.
	 *
	 */
	public SimpleHostInfo() {
		
		hostName = "";
		operatingSystem = "";
		groups = null;
		memory = 0;
		cpuIDString = "";
	}
	
	/**
	 * Create new SimpleHostInfo class and initialise members.
	 * 
	 * @param name Name of host.
	 * @param osName Operating system identification string.
	 * @param groupList List of groups host is member of.
	 * @param memSize Size of physical memory in bytes.
	 * @param cpuString CPU identification string.
	 */
	public SimpleHostInfo(String name, String osName, String[] groupList, long memSize, String cpuString) {
		
		hostName = name;
		operatingSystem = osName;
		groups = groupList.clone();
		memory = memSize;
		cpuIDString = cpuString;
	}
	
	/**
	 * Build <code>SimpleHostInfo</code> from data provided by the <code>HostInfo</code> class.
	 * 
	 * @param hi Instance of <code>HostInfo</code> class which contains data to be used in initialisation.
	 */
	public SimpleHostInfo(HostInfoInterface hi) {
		
		cpuIDString = "";
		String after = "";
		
		try {
			if (hi.getProcessorCount() != 1) {
				cpuIDString = String.valueOf(hi.getProcessorCount()) + " x ";
			} else {
				after = ", " + String.valueOf(hi.getProcessor(0).getSpeed()) + " MHz";
			}
			
			cpuIDString += hi.getProcessor(0).getModelName() + after;
		} catch (IndexOutOfBoundsException e) {
			cpuIDString = "(unknown)";
		}

		operatingSystem = hi.getOperatingSystem().getName();
		memory = hi.getMemory().getPhysicalMemorySize();
		hostName = hi.getHostName();

		ValueList< ? > groupNames = null;
		
		try {
			groupNames = (ValueList< ? >) hi.getPropertyValue(HostInfoInterface.Properties.MEMBER_OF);
		} catch (Exception e) {
			groupNames = new ValueList< ValueString >(ValueType.STRING);
		}
		
		groups = new String[groupNames.length()];
		
		for (int i = 0; i < groupNames.length(); ++i) {
			groups[i] = new String(groupNames.get(i).toString());
		}
	}
	
	/**
	 * Get name of host.
	 * 
	 * @return Host name.
	 */
	public String getHostName() {
		
		return hostName;
	}
	
	/**
	 * Get operating system installed on host;
	 * 
	 * @return Operating system installed on host.
	 */
	public String getOperatingSystem() {
		
		return operatingSystem;
	}
	
	/**
	 * Get list of groups this host is member of.
	 * 
	 * @return List of names of groups host belongs to. It always contains at least one group - __ROOT.
	 */
	public String[] getGroupList() {
		
		return groups;
	}
	
	/**
	 * Get size of physical memory installed on computer.
	 * 
	 * @return Size of physical memory in bytes.
	 */
	public long getMemorySize() {
		
		return memory;
	}
	
	/**
	 * Get identification string of processor(s) on host.
	 * 
	 * @return Processor identification string.
	 */
	public String getCPUIDString() {
		
		return cpuIDString;
	}
}
