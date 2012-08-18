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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.common.value.ValueString;
import cz.cuni.mff.been.hostmanager.InputParseException;



/**
 * Base class for operating system informations. It stores data common to all operating systems.
 * OS specific features are stored in derived classes.
 * 
 * @see cz.cuni.mff.been.hostmanager.database.WindowsOperatingSystem
 * @see cz.cuni.mff.been.hostmanager.database.LinuxOperatingSystem
 * @see cz.cuni.mff.been.hostmanager.database.SolarisOperatingSystem
 * @see cz.cuni.mff.been.hostmanager.database.UnknownOperatingSystem 
 * 
 * @author Branislav Repcek
 */
public abstract class OperatingSystem extends PropertyTree 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {

	private static final long	serialVersionUID	= 2220004193453470724L;

	/**
	 * Encapsulation of names of properties of OperatingSystem object.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
		/**
		 * Name of the OS.
		 */
		public static final String NAME = "name";
		
		/**
		 * Vendor name.
		 */
		public static final String VENDOR = "vendor";
		
		/**
		 * CPU architecture OS is running on.
		 */
		public static final String ARCHITECTURE = "arch";
		
		/**
		 * OS family (Windows, Linux...).
		 */
		public static final String FAMILY = "family";
	}
	
	/**
	 * Child object names. Only one of those objects will be present at any given time.
	 *
	 * @author Branislav Repcek
	 */
	public static class Objects {

		/**
		 * Windows info.
		 */
		public static final String WINDOWS = "windows";
		
		/**
		 * Solaris info.
		 */
		public static final String SOLARIS = "solaris";
		
		/**
		 * Linux info.
		 */
		public static final String LINUX = "linux";
		
		/**
		 * Other OS info.
		 */
		public static final String OTHER = "other";
	}
	
	/**
	 * Name of OS (eg. Windows XP, Gentoo Linux etc.)
	 */
	protected String name;
	
	/**
	 * Operating system vendor (Microsoft, Sun...)
	 */
	protected String vendor;
	
	/**
	 * Architecture of computer OS is running on (eg. x86, sparc, IA64 etc.).
	 */
	protected String architecture;
	
	/**
	 * Operating system family (Linux, Windows, Solaris, other).
	 */
	protected String family;

	/**
	 * Initialise info about common OS features.
	 * 
	 * @param name Name of OS.
	 * @param vendor OS vendor string.
	 * @param arch Architecture of computer.
	 * @param family Family of operating system (Linux, Windows, Solaris, other).
	 */
	public OperatingSystem(String name, String vendor, String arch, String family) {
		
		super(HostInfoInterface.Objects.OPERATING_SYSTEM, null);
		
		this.name = name;
		this.vendor = vendor;
		architecture = arch;
		this.family = family;
		
		try {
			putProperty(Properties.NAME, new ValueString(name));
			putProperty(Properties.VENDOR, new ValueString(vendor));
			putProperty(Properties.ARCHITECTURE, new ValueString(architecture));
			putProperty(Properties.FAMILY, new ValueString(family));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to the OperatingSystem.";
		}
	}

	/**
	 * Create empty operating system.
	 * 
	 * @param family Operating system family (Windows, Linux, Solaris, Other).
	 */
	public OperatingSystem(String family) {
		
		super(HostInfoInterface.Objects.OPERATING_SYSTEM, null);
		
		this.family = family;
		
		try {
			putProperty(Properties.FAMILY, new ValueString(family));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to the OperatingSystem.";
		}
	}

	/**
	 * Parse data stored in XML file node (only data from <code>basicInfo</code> node are parsed).
	 * 
	 * @param osNode Node containing OS data.
	 * 
	 * @throws InputParseException If there was an error while parsing data.
	 */
	protected void parseBasicInfoNode(Node osNode) throws InputParseException {

		Node basicInfoNode = XMLHelper.getSubNodeByName("basicInfo", osNode);
		
		name = XMLHelper.getSubNodeValueByName("name", basicInfoNode);
		vendor = XMLHelper.getSubNodeValueByName("vendor", basicInfoNode);
		architecture = XMLHelper.getSubNodeValueByName("arch", basicInfoNode);

		try {
			putProperty(Properties.NAME, new ValueString(name));
			putProperty(Properties.VENDOR, new ValueString(vendor));
			putProperty(Properties.ARCHITECTURE, new ValueString(architecture));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to the OperatingSystem.";
		}
	}

	/**
	 * Get name of operating system.
	 * 
	 * @return OS name.
	 */
	public String getName() {
		
		return name;
	}
	
	/**
	 * Get vendor name.
	 * 
	 * @return Vendor name.
	 */
	public String getVendor() {
		
		return vendor;
	}
	
	/**
	 * Get architecture of computer OS is running on.
	 * 
	 * @return String with architecture description.
	 */
	public String getArchitecture() {
		
		return architecture;
	}
	
	/**
	 * Get family of the OS.
	 * 
	 * @return OS family.
	 */
	public String getFamily() {
		
		return family;
	}
	
	/**
	 * Export basic info about the OS as XML element.
	 * 
	 * @param document Document into which element will be written.
	 * 
	 * @return Element containing basic info about the OS.
	 */
	protected Element exportBasicAsElement(Document document) {
		
		Element basicInfoElement = document.createElement("basicInfo");
		
		basicInfoElement.appendChild(XMLHelper.writeValueToElement(document, name, "name"));
		basicInfoElement.appendChild(XMLHelper.writeValueToElement(document, vendor, "vendor"));
		basicInfoElement.appendChild(XMLHelper.writeValueToElement(document, architecture, "arch"));
		
		return basicInfoElement;
	}
}
