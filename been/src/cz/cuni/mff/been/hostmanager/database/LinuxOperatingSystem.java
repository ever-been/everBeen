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

import cz.cuni.mff.been.hostmanager.InputParseException;

import cz.cuni.mff.been.hostmanager.util.XMLHelper;

import cz.cuni.mff.been.hostmanager.value.ValueString;
import cz.cuni.mff.been.hostmanager.value.ValueVersion;

/**
 * Class which store info about Linux operating system. It does not contain any detection routines.
 *
 * @author Branislav Repcek
 */
public class LinuxOperatingSystem extends OperatingSystem 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {
	
	private static final long	serialVersionUID	= 6570218378674417407L;

	/**
	 * Encapsulates names of properties of LinuxOperatingSystem object.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
		/**
		 * Distribution property name.
		 */
		public static final String DISTRIBUTION = "distribution";
		
		/**
		 * Version of the distribution.
		 */
		public static final String DISTRIBUTION_VERSION = "distversion";
		
		/**
		 * Kernel release name.
		 */
		public static final String OS_RELEASE = "osrelease";
		
		/**
		 * Kernel version string (usually build date).
		 */
		public static final String OS_VERSION = "osversion";
		
		/**
		 * Kernel version property name.
		 */
		public static final String KERNEL_VERSION = "kernelversion";
	}
	
	/**
	 * Kernel release (like 2.6.9-gentoo-r6)
	 */
	private String osRelease;
	
	/**
	 * Kernel version date (on Gentoo it is date of kernel compilation).
	 */
	private String osVersion;
	
	/**
	 * Distribution name.
	 */
	private String distribution;
	
	/**
	 * Distribution version.
	 */
	private String distributionVersion;
	
	/**
	 * Kernel version.
	 */
	private String kernelVersion;
	
	/**
	 * Sub-object which contains properties.
	 */
	private PropertyTree obj;

	/**
	 * Create LinuxOperatingSystem class with given parameters.
	 * 
	 * @param name Name of operating system.
	 * @param vendor Vendor name.
	 * @param distribution Distribution name.
	 * @param distributionVersion Distribution version.
	 * @param release OS release string.
	 * @param version OS version string.
	 * @param machine Target machine architecture.
	 * @param kernelVersion Version or the kernel.
	 */
	public LinuxOperatingSystem(String name, String vendor, String distribution, String distributionVersion, 
			String release, String version, String machine, String kernelVersion) {
		
		super(name, vendor, machine, "Linux");

		this.distribution = distribution;
		this.distributionVersion = distributionVersion;
		this.osRelease = release;
		this.osVersion = version;
		this.kernelVersion = kernelVersion;
		
		obj = new PropertyTree(OperatingSystem.Objects.LINUX, this);
		
		try {
			obj.putProperty(Properties.DISTRIBUTION, new ValueString(this.distribution));
			obj.putProperty(Properties.DISTRIBUTION_VERSION, new ValueVersion(this.distributionVersion));
			obj.putProperty(Properties.KERNEL_VERSION, new ValueString(this.kernelVersion));
			obj.putProperty(Properties.OS_RELEASE, new ValueString(this.osRelease));
			obj.putProperty(Properties.OS_VERSION, new ValueString(this.osVersion));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to LinuxOperatingSystem."; 
		}
	}
	
	/**
	 * Read OS data from Node from host XML file.
	 * 
	 * @param osNode <code>operatingSystem</code> node from XML file.
	 * 
	 * @throws InputParseException If error occurred when parsing file.
	 */	
	public LinuxOperatingSystem(Node osNode) throws InputParseException {
		
		super("Linux");

		obj = new PropertyTree(OperatingSystem.Objects.LINUX, this);

		parseXMLNode(osNode);
	}
	
	/*
	 * NOTE: this will only parse AdvancedInfo node.
	 * 
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {
		
		parseBasicInfoNode(node);
		
		Node advancedInfoNode = XMLHelper.getSubNodeByName("advancedInfo", node);

		distribution = XMLHelper.getSubNodeValueByName("distribution", advancedInfoNode);
		distributionVersion = XMLHelper.getSubNodeValueByName("distroVersion", advancedInfoNode);
		osVersion = XMLHelper.getSubNodeValueByName("version", advancedInfoNode);
		osRelease = XMLHelper.getSubNodeValueByName("release", advancedInfoNode);
		kernelVersion = XMLHelper.getSubNodeValueByName("kernelVersion", advancedInfoNode);

		try {
			obj.putProperty(Properties.DISTRIBUTION, new ValueString(this.distribution));
			obj.putProperty(Properties.DISTRIBUTION_VERSION, new ValueVersion(this.distributionVersion));
			obj.putProperty(Properties.KERNEL_VERSION, new ValueString(this.kernelVersion));
			obj.putProperty(Properties.OS_RELEASE, new ValueString(this.osRelease));
			obj.putProperty(Properties.OS_VERSION, new ValueString(this.osVersion));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to LinuxOperatingSystem."; 
		}
	}

	/**
	 * @return Name of the distribution.
	 */
	public String getDistributionName() {
		
		return distribution;
	}

	/**
	 * @return Distribution version.
	 */
	public String getDistributionVersion() {
		
		return distributionVersion;
	}
	
	/**
	 * Get version of kernel.
	 * 
	 * @return String with kernel version.
	 */
	public String getKernelVersion() {
		
		return kernelVersion;
	}
	
	/**
	 * @return OS release.
	 */
	public String getOSRelease() {
		
		return osRelease;
	}

	/**
	 * @return OS version.
	 */
	public String getOSVersion() {
		
		return osVersion;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element osElement = document.createElement("operatingSystem");
		
		osElement.appendChild(exportBasicAsElement(document));
		
		Element advancedInfoElement = document.createElement("advancedInfo");
		
		advancedInfoElement.appendChild(XMLHelper.writeValueToElement(document, distribution, "distribution"));
		advancedInfoElement.appendChild(XMLHelper.writeValueToElement(document, distributionVersion, "distroVersion"));
		advancedInfoElement.appendChild(XMLHelper.writeValueToElement(document, kernelVersion, "kernelVersion"));
		advancedInfoElement.appendChild(XMLHelper.writeValueToElement(document, osRelease, "release"));
		advancedInfoElement.appendChild(XMLHelper.writeValueToElement(document, osVersion, "version"));
		
		osElement.appendChild(advancedInfoElement);
		
		return osElement;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "Linux{" + getName() + "}";
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "operatingSystem";
	}
}
