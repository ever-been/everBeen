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
import cz.cuni.mff.been.common.value.ValueInteger;
import cz.cuni.mff.been.common.value.ValueString;
import cz.cuni.mff.been.common.value.ValueVersion;
import cz.cuni.mff.been.hostmanager.InputParseException;



/**
 * 
 * Class which stores info about Windows operating systems. It does not contain any detection.
 * 
 * @author Branislav Repcek
 *
 */
public class WindowsOperatingSystem extends OperatingSystem 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {
	
	private static final long	serialVersionUID	= -4571531394234905577L;

	/**
	 * Encapsulation of constants with names of properties of WindowsOperatingSystem.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
		/** 
		 * Version of Windows 
		 */
		public static final String VERSION = "version";
		
		/**
		 * Build identification string.
		 */
		public static final String BUILD_TYPE = "build";
		
		/**
		 * Service pack version.
		 */
		public static final String SERVICE_PACK_VERSION = "sp";
		
		/**
		 * Windows installation directory.
		 */
		public static final String WINDOWS_DIRECTORY = "windir";
		
		/**
		 * Windows system directory.
		 */
		public static final String SYSTEM_DIRECTORY = "sysdir";
		
		/**
		 * Default encryption level.
		 */
		public static final String ENCRYPTION_LEVEL = "encryption";
	}
	
	/**
	 * Version string of Windows (eg. 5.1.2600) 
	 */
	private String version;
	
	/**
	 * Type of build of MS systems (determines processor type, most OSes are Uniprocessor)
	 */
	private String buildType;

	/**
	 * Version of service pack in format major.minor (can be 0.0 when no SP is installed).
	 */
	private String spVersion;
	
	/**
	 * Main directory of Windows (usually c:\Windows)
	 */
	private String windowsDirectory;
	
	/**
	 * System directory (eg. c:\Windows\System32)
	 */
	private String systemDirectory;
	
	/**
	 * Size of default encryption key in Windows. It depends on service pack an os version.
	 */
	private int encryptionLevel;
	
	/**
	 * Sub-object which contains properties.
	 */
	private PropertyTree obj;
	
	/**
	 * Create WindowsOperatingSystem class with given parameters.
	 * 
	 * @param name Operating system name.
	 * @param vendor Operating system vendor.
	 * @param version Version string.
	 * @param build Build type string.
	 * @param servicePack Version of service pack.
	 * @param winDir Path to Windows directory. 
	 * @param systemDir Path to system directory.
	 * @param encryption Encryption level in bits.
	 * @param arch Computer architecture description.
	 */
	public WindowsOperatingSystem(String name, String vendor, String version, String build,  
			                      String servicePack, String winDir, String systemDir, 
			                      int encryption, String arch) {
		
		super(name, vendor, arch, "Windows");
		
		this.version = version;
		this.buildType = build;
		this.spVersion = servicePack;
		this.windowsDirectory = winDir;
		this.systemDirectory = systemDir;
		this.encryptionLevel = encryption;
		
		obj = new PropertyTree(OperatingSystem.Objects.WINDOWS, this);
				
		try {
			obj.putProperty(Properties.VERSION, new ValueVersion(this.version));
			obj.putProperty(Properties.BUILD_TYPE, new ValueString(this.buildType));
			obj.putProperty(Properties.SERVICE_PACK_VERSION, new ValueVersion(this.spVersion));
			obj.putProperty(Properties.WINDOWS_DIRECTORY, new ValueString(this.windowsDirectory));
			obj.putProperty(Properties.SYSTEM_DIRECTORY, new ValueString(this.systemDirectory));
			obj.putProperty(Properties.ENCRYPTION_LEVEL, new ValueInteger(this.encryptionLevel, "b"));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to WindowsOperatingSystem.";
		}
	}
	
	/**
	 * Read OS data from Node from host XML file.
	 * 
	 * @param osNode <code>operatingSystem</code> node from XML file.
	 * @throws InputParseException if error occurred when parsing file.
	 */
	public WindowsOperatingSystem(Node osNode) throws InputParseException {
		
		super("Windows");

		obj = new PropertyTree(OperatingSystem.Objects.WINDOWS, this);

		parseXMLNode(osNode);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {

		parseBasicInfoNode(node);
		
		Node advancedInfoNode = XMLHelper.getSubNodeByName("advancedInfo", node);
		
		version = XMLHelper.getSubNodeValueByName("version", advancedInfoNode);
		buildType = XMLHelper.getSubNodeValueByName("buildType", advancedInfoNode);
		spVersion = XMLHelper.getSubNodeValueByName("servicePackVersion", advancedInfoNode);
		windowsDirectory = XMLHelper.getSubNodeValueByName("windowsDirectory", advancedInfoNode);
		systemDirectory = XMLHelper.getSubNodeValueByName("systemDirectory", advancedInfoNode);
		encryptionLevel = Integer.valueOf(XMLHelper.getSubNodeValueByName("encryptionLevel", advancedInfoNode)).intValue();

		try {
			obj.putProperty(Properties.VERSION, new ValueVersion(version));
			obj.putProperty(Properties.BUILD_TYPE, new ValueString(buildType));
			obj.putProperty(Properties.SERVICE_PACK_VERSION, new ValueVersion(spVersion));
			obj.putProperty(Properties.WINDOWS_DIRECTORY, new ValueString(windowsDirectory));
			obj.putProperty(Properties.SYSTEM_DIRECTORY, new ValueString(systemDirectory));
			obj.putProperty(Properties.ENCRYPTION_LEVEL, new ValueInteger(encryptionLevel, "b"));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to WindowsOperatingSystem.";
		}
	}
	
	/**
	 * Get version string of Windows.
	 * 
	 * @return Version.
	 */
	public String getVersion() {
		
		return version;
	}

	/**
	 * Get string containing build type of Windows.
	 * 
	 * @return String with build type.
	 */
	public String getBuildType() {
		
		return buildType;
	}

	/**
	 * Get version of service pack.
	 * 
	 * @return String with service pack version in format major.minor. t can be 0.0 when no service
	 *         pack is installed.
	 */
	public String getServicePackVersion() {
		
		return spVersion;
	}

	/**
	 * Get path to Windows directory.
	 * 
	 * @return String containing path of windows directory. 
	 */
	public String getWindowsDirectory() {
		
		return windowsDirectory;
	}

	/**
	 * Get path of system directory.
	 * 
	 * @return Path of system directory (usually system32 sub-directory in windows directory).
	 */
	public String getSystemDirectory() {
		
		return systemDirectory;
	}

	/**
	 * Get default level of encryption.
	 * 
	 * @return Length of encryption key in bits.
	 */
	public int getEncryptionLevel() {
		
		return encryptionLevel;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element osElement = document.createElement("operatingSystem");
		
		osElement.appendChild(exportBasicAsElement(document));

		Element advancedElement = document.createElement("advancedInfo");
		osElement.appendChild(advancedElement);
		
		advancedElement.appendChild(XMLHelper.writeValueToElement(document, version, "version"));
		advancedElement.appendChild(XMLHelper.writeValueToElement(document, buildType, "buildType"));
		advancedElement.appendChild(XMLHelper.writeValueToElement(document, spVersion, "servicePackVersion"));
		advancedElement.appendChild(XMLHelper.writeValueToElement(document, windowsDirectory, "windowsDirectory"));
		advancedElement.appendChild(XMLHelper.writeValueToElement(document, systemDirectory, "systemDirectory"));
		advancedElement.appendChild(XMLHelper.writeValueToElement(document, encryptionLevel, "encryptionLevel"));
		
		return osElement;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "Windows{" + getName() + ", " + version + ", sp=" + getServicePackVersion() + "}";
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "operatingSystem";
	}
}
