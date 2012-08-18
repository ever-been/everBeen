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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.w3c.dom.Node;

import cz.cuni.mff.been.common.value.ValueCommonInterface;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;

/**
 * This interface provides methods which you can use to access data about host's hardware and 
 * software configuration as determined by detectors.
 *
 * @author Branislav Repcek
 */
public interface HostInfoInterface 
extends Serializable, PropertyTreeReadInterface {

	/**
	 * This class encapsulates all constants for properties of one host.
	 * 
	 * @author Branislav Repcek
	 */
	public static class Properties {

		/**
		 * Property name for host name.
		 */
		public static final String HOST_NAME = "name";

		/**
		 * Property name for detector identification string.
		 */
		public static final String DETECTOR = "detector";

		/**
		 * Property name for the date of the latest check/refresh. 
		 */
		public static final String CHECK_DATE = "checkdate";

		/**
		 * Property name for the time of the latest check/refresh. 
		 */
		public static final String CHECK_TIME = "checktime";

		/**
		 * Property name for number of drives installed on host.
		 */
		public static final String DRIVES = "drives";

		/**
		 * Property name for number of processors available on host.
		 */
		public static final String PROCESSORS = "processors";

		/**
		 * Property name for number of network adapters available on host. 
		 */
		public static final String ADAPTERS = "adapters";

		/**
		 * Property name for number of application installed on host.
		 */
		public static final String APPLICATIONS = "applications";

		/**
		 * Member of property.
		 */
		public static final String MEMBER_OF = "memberof";

		/**
		 * Number of aliases.
		 */
		public static final String ALIASES = "aliases";
		
		/** Default number of load units when no memory information is available. */
		public static final String DEFAULT_LOAD_UNITS = "defaultLoadUnits";
		
		/** Actual number of load units currently assigned to the host. */
		public static final String LOAD_UNITS = "loadUnits";
	}

	/**
	 * Encapsulation of constants which represent names of objects.
	 * 
	 * @author Branislav Repcek
	 */
	public static class Objects {

		/**
		 * Name of object containing one installed application.
		 */
		public static final String APPLICATION = "application";

		/**
		 * Name of object with information about one drive.
		 */
		public static final String DRIVE = "drive";

		/**
		 * Name of object with information about one network adapter.
		 */
		public static final String ADAPTER = "adapter";

		/**
		 * Name of object with information about java virtual machine and runtime on host.
		 */
		public static final String JAVA = "java";

		/**
		 * Name of object with information about one logical processor.
		 */
		public static final String PROCESSOR = "processor";

		/**
		 * Name of object with information about operating system.
		 */
		public static final String OPERATING_SYSTEM = "os";

		/**
		 * Name of object with information about host's memory.
		 */
		public static final String MEMORY = "memory";

		/**
		 * Name of the object with info about drive BEEN is installed on.
		 */
		public static final String BEEN_DISK = "beendisk";

		/**
		 * Object which contain all user-defined properties.
		 */
		public static final String USER_OBJECT = "user";

		/**
		 * Object which contains software alias.
		 */
		public static final String SOFTWARE_ALIAS = "alias";
	}

	/**
	 * Encapsulates identification strings for various detectors. 
	 *
	 * @author Branislav Repcek
	 */
	public static enum Detectors {

		/**
		 * Generic detector (for OSes not directly supported).
		 */
		GENERIC( "hwdet_generic" ) {
			@Override
			public OperatingSystem makeOSFromNode( Node osNode ) throws InputParseException {
				return new UnknownOperatingSystem( osNode );
			}
		},

		/**
		 * Linux detector.
		 */
		LINUX( "hwdet3_linux" ) {
			@Override
			public OperatingSystem makeOSFromNode( Node osNode ) throws InputParseException {
				return new LinuxOperatingSystem( osNode );
			}
		},

		/**
		 * Solaris detector.
		 */
		SOLARIS( "hwdet_solaris" ) {
			@Override
			public OperatingSystem makeOSFromNode( Node osNode ) throws InputParseException {
				return new SolarisOperatingSystem( osNode );
			}
		},

		/**
		 * Windows detector.
		 */
		WINDOWS( "hwdet3_windows" ) {
			@Override
			public OperatingSystem makeOSFromNode( Node osNode ) throws InputParseException {
				return new WindowsOperatingSystem( osNode );
			}			
		};



		private final String								name;

		private static final TreeMap< String, Detectors >	reverseMap;

		private Detectors( String name ) {
			this.name = name;
		}

		static {	// This runs AFTER the enum items had been initialized.
			reverseMap = new TreeMap< String, Detectors >();

			for ( Detectors detector : Detectors.values() ) {
				reverseMap.put( detector.toString(), detector );
			}
		}

		public static Detectors fromString( String name ) {
			return reverseMap.get( name );
		}

		@Override
		public String toString() {
			return name;
		}

		public abstract OperatingSystem makeOSFromNode( Node osNode ) throws InputParseException;
	}

	/**
	 * Get name of host.
	 * 
	 * @return String with host name.
	 */
	String getHostName();

	/**
	 * Get date of last check of host's setup.
	 * 
	 * @return String containing date of check in format year/month/day.
	 */
	String getCheckDate();

	/**
	 * Get time of last check of host,
	 * 
	 * @return String with check time in format hours:minutes.seconds (24 hour time format).
	 */
	String getCheckTime();

	/**
	 * Get identification string of detector used to create data file with host's setup.
	 * Each detector has it's own string containing detector version and platform.
	 * 
	 * @return Detector's ID string.
	 */
	String getDetectorIDString();

	/**
	 * Get number of drives installed on host. This will NOT return number of partitions, but number
	 * of actual physical drives (it can be 0, when no supported drive types are installed on the host) including
	 * CD/DVD drives or tape drives.
	 * 
	 * @return Number of drives installed on host.
	 */
	int getDriveCount();

	/**
	 * Get information about drive with given index.
	 * 
	 * @param index Index of drive to get info about (from 0 to drive count-1).
	 * @return Class with drive parameters. For more info look into DiskDrive.java.
	 * 
	 * @throws IndexOutOfBoundsException When invalid index was passed (negative or too big)
	 */
	DiskDrive getDiskDrive(int index) throws IndexOutOfBoundsException;

	/**
	 * Get number of processors installed on host.
	 * 
	 * @return Number of processors. This value should always be >=1 (or we have had an error during
	 *         HW detection, file parsing or special pc without cpu :))
	 */	
	int getProcessorCount();

	/**
	 * Get data about processor with given index.
	 * 
	 * @param index Index of processor in CPU list (from 0 to number of CPUs-1). 
	 * @return Class containing info about CPU. For more info look into Processor.java.
	 * 
	 * @throws IndexOutOfBoundsException Invalid CPU index.
	 */	
	Processor getProcessor(int index) throws IndexOutOfBoundsException;

	/**
	 * Get number of network adapters/interfaces present on host.
	 * 
	 * @return Number of adapters/interfaces.
	 */
	int getNetworkAdapterCount();

	/**
	 * Get info about given network adapter. On windows this returns info about hardware/software
	 * adapter, on Linux it returns info about network interfaces present on system.
	 * 
	 * @param index Index of adapter/interface in list.
	 * @return Class containing requested info.
	 * 
	 * @throws IndexOutOfBoundsException When bad index has been used (too big or negative).
	 */
	NetworkAdapter getNetworkAdapter(int index) throws IndexOutOfBoundsException;

	/**
	 * Get info about memory subsystem.
	 * 
	 * @return Memory information.
	 */
	Memory getMemory();

	/**
	 * Get operating system informations.
	 * 
	 * @return Info about OS running on host.
	 */
	OperatingSystem getOperatingSystem();

	/**
	 * Get number of software products installed on system.
	 * 
	 * @return Number of products,
	 */
	int getProductCount();

	/**
	 * Get info about product on given position in list.
	 * 
	 * @param index Position of product in list. First product has index 0. 
	 * @return Info about requested product.
	 * @throws IndexOutOfBoundsException When bad index has been used.
	 */
	Product getProduct(int index) throws IndexOutOfBoundsException;

	/**
	 * Get info about Java implementation on host.
	 * 
	 * @return Java info.
	 */
	JavaInfo getJavaInfo();

	/**
	 * Get info about BEEN disk.
	 * 
	 * @return Info about BEEN disk.
	 */
	BeenDisk getBeenDisk();

	/**
	 * Get number of aliases on the host.
	 * 
	 * @return Number of software aliases on the host.
	 */
	int getSoftwareAliasCount();

	/**
	 * Get alias at the given index.
	 * 
	 * @param index Index of the alias.
	 * 
	 * @return Alias at specified index.
	 * 
	 * @throws IndexOutOfBoundsException If index is invalid
	 */
	SoftwareAlias getSoftwareAlias(int index) throws IndexOutOfBoundsException;

	/**
	 * Test if alias with given name is present.
	 * 
	 * @param aliasName Name of the alias to find. Note that this is case-sensitive search.
	 * 
	 * @return <code>true</code> if alias with given name has been found, <code>false</code> otherwise.
	 */
	boolean hasSoftwareAlias(String aliasName);

	/**
	 * Get list of all aliases on the host.
	 * 
	 * @return Array containing all software aliases present on the host.
	 */
	SoftwareAlias[] getSoftwareAliasList();

	/**
	 * Get list of aliases with given name.
	 * 
	 * @param name Name of the alias to search for.
	 * 
	 * @return List containing all aliases with given name. If no alias has been found, empty list
	 *         is returned.
	 */
	List< SoftwareAlias > getSoftwareAliasByName(String name);

	/**
	 * Get date and time of the last host check.
	 * 
	 * @return Date and time data in this HostInfo were collected by the detector.
	 */
	Date getCheckDateTime();

	/**
	 * Get value of user-defined property.
	 * 
	 * @param name Name of the property.
	 * @return Value of the property.
	 * 
	 * @throws ValueNotFoundException If property with given name does not exist.
	 * @throws InvalidArgumentException If property name has invalid syntax,
	 */
	ValueCommonInterface getUserPropertyValue(String name) 
	throws ValueNotFoundException, InvalidArgumentException;

	/**
	 * Test if given property already exists.
	 * 
	 * @param name Name of the property to test.
	 * 
	 * @return <code>true</code> if property with given name exists, <code>false</code> otherwise.
	 * @throws InvalidArgumentException If property name has invalid syntax.
	 */
	boolean hasUserProperty(String name) throws InvalidArgumentException;

	/**
	 * Add new user-defined property.
	 * 
	 * @param name Name of the property to add.
	 * @param value Value of the property.
	 * 
	 * @throws InvalidArgumentException If property name has invalid syntax or if given property
	 *         already exists.
	 */
	void addUserProperty(String name, ValueCommonInterface value) 
	throws InvalidArgumentException;

	/**
	 * Add new user-defined property.
	 * 
	 * @param property <code>NameValuePair</code> containing name and value of the property to add.
	 *
	 * @throws InvalidArgumentException If property name has invalid syntax or if given property
	 *         already exists.
	 */
	void addUserProperty(NameValuePair property) throws InvalidArgumentException;

	/**
	 * Add new user-defined property or change value of already defined property.
	 *  
	 * @param name Name of the property to add or set.
	 * @param value New value of the property.
	 * 
	 * @return Old value of the property if property already existed or <code>null</code> if
	 *         property was just created.
	 *
	 * @throws InvalidArgumentException If name of property has invalid syntax.
	 */
	ValueCommonInterface putUserProperty(String name, ValueCommonInterface value)
	throws InvalidArgumentException;

	/**
	 * Add new user-defined property or change value of already defined property.
	 *  
	 * @param property <code>NameValuePair</code> containing name and value of the property.
	 * 
	 * @return Old value of the property if property already existed or <code>null</code> if
	 *         property was just created.
	 *
	 * @throws InvalidArgumentException If name of property has invalid syntax.
	 */
	ValueCommonInterface putUserProperty(NameValuePair property) throws InvalidArgumentException;

	/**
	 * Remove user-defined property.
	 * 
	 * @param name Name of the property to remove.
	 * 
	 * @throws InvalidArgumentException If property name has invalid syntax.
	 * @throws ValueNotFoundException If property with given name has not been found.
	 */
	void removeUserProperty(String name) throws InvalidArgumentException, ValueNotFoundException;

	/**
	 * Change value of existing user-defined property.
	 * 
	 * @param name Name of the property to change. Property must already exist.
	 * @param value New value of the property.
	 * 
	 * @return Old value of the property.
	 * 
	 * @throws InvalidArgumentException If name of the property has invalid syntax.
	 * @throws ValueNotFoundException If property with given name does not exist.
	 */
	ValueCommonInterface setUserProperty(String name, ValueCommonInterface value)
	throws InvalidArgumentException, ValueNotFoundException;

	/**
	 * Change value of existing user-defined property.
	 * 
	 * @param property <code>NameValuePair</code> containing name of already existing property and
	 *        new value to which value of the property should be set.
	 * 
	 * @return Old value of the property.
	 * 
	 * @throws InvalidArgumentException If name of the property has invalid syntax.
	 * @throws ValueNotFoundException If property with given name does not exist.
	 */
	ValueCommonInterface setUserProperty(NameValuePair property)
	throws InvalidArgumentException, ValueNotFoundException;

	/**
	 * Set object which contains user-defined properties to given object. This can be used to create
	 * more complicated hierarchies of user-defined properties that with simplified API.
	 * 
	 * @param props Object which contains user-defined properties.
	 */
	void setUserPropertiesObject(PropertyTreeReadInterface props);

	/**
	 * Get object which contains user-defined properties.
	 * 
	 * @return Object which contains all user-defined properties.
	 */
	PropertyTreeInterface getUserPropertiesObject();

	/**
	 * Get iterator over the set of the user-defined properties.
	 * 
	 * @return Iterator of the set of the user-defined properties. Iterator is read-only (does not
	 *         support <code>remove</code> method).
	 */
	@Deprecated
	Iterator< NameValuePair > getUserPropertiesIterator();

	/**
	 * Get array which contains user-defined properties.
	 * 
	 * @return array which contains all user-defined properties
	 */
	NameValuePair[] getUserProperties();
}
