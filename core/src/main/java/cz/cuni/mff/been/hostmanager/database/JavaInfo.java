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
import cz.cuni.mff.been.common.value.ValueVersion;
import cz.cuni.mff.been.hostmanager.InputParseException;



/**
 * Stores data about Java implementation on host. Does not contain any detection.
 * 
 * @author Branislav Repcek
 */
public class JavaInfo extends PropertyTree 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {
	
	private static final long	serialVersionUID	= -7385522252439791353L;

	/**
	 * Encapsulation of names of properties of java object.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
		/**
		 * Version of Java.
		 */
		public static final String JAVA_VERSION = "version";
		
		/**
		 * Java vendor.
		 */
		public static final String JAVA_VENDOR = "vendor";
		
		/**
		 * Runtime name.
		 */
		public static final String RUNTIME_NAME = "runtime";
		
		/**
		 * Runtime version.
		 */
		public static final String RUNTIME_VERSION = "runtimever";
		
		/**
		 * Virtual machine version.
		 */
		public static final String VM_VERSION = "vmversion";
		
		/**
		 * Virtual machine vendor.
		 */
		public static final String VM_VENDOR = "vmvendor";
		
		/**
		 * Supported Java specification version.
		 */
		public static final String SPECIFICATION_VERSION = "specification";
	}
	
	/**
	 * Version of Java.
	 */
	private String javaVersion;
	
	/**
	 * Java vendor name.
	 */
	private String javaVendor;
	
	/**
	 * Java runtime name string.
	 */
	private String runtimeName;
	
	/**
	 * Java runtime version.
	 */
	private String runtimeVersion;
	
	/**
	 * Java virtual machine version. 
	 */
	private String vmVersion;
	
	/**
	 * Java virtual machine vendor name.
	 */
	private String vmVendor;
	
	/**
	 * Java specification version.
	 */
	private String specification;
		
	/**
	 * Create new JavaInfo object.
	 * 
	 * @param javaVersion Version of Java.
	 * @param javaVendor Vendor of Java.
	 * @param runtimeName Runtime name.
	 * @param runtimeVersion Runtime version.
	 * @param vmVersion Virtual machine version.
	 * @param vmVendor Virtual machine vendor.
	 * @param specification Java specification version.
	 */
	public JavaInfo(String javaVersion, String javaVendor, String runtimeName, String runtimeVersion,
	                String vmVersion, String vmVendor, String specification) {
		
		super(HostInfoInterface.Objects.JAVA, null);

		this.javaVersion = javaVersion;
		this.javaVendor = javaVendor;
		this.runtimeName = runtimeName;
		this.runtimeVersion = runtimeVersion;
		this.vmVersion = vmVersion;
		this.vmVendor = vmVendor;
		this.specification = specification;
		
		try {
			putProperty(Properties.JAVA_VERSION, new ValueVersion(this.javaVersion));
			putProperty(Properties.JAVA_VENDOR, new ValueString(this.javaVendor));
			putProperty(Properties.RUNTIME_NAME, new ValueString(this.runtimeName));
			putProperty(Properties.RUNTIME_VERSION, new ValueVersion(this.runtimeVersion));
			putProperty(Properties.VM_VERSION, new ValueVersion(this.vmVersion));
			putProperty(Properties.VM_VENDOR, new ValueString(this.vmVendor));
			putProperty(Properties.SPECIFICATION_VERSION, new ValueVersion(this.specification));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add basic properties to JavaInfo."; 
		}
	}
	
	/**
	 * Create new JavaInfo object from XML node.
	 * 
	 * @param javaNode Node with info about Java.
	 * 
	 * @throws InputParseException If error occured during file parsing.
	 */
	public JavaInfo(Node javaNode) throws InputParseException {
		
		super(HostInfoInterface.Objects.JAVA, null);

		parseXMLNode(javaNode);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {

		javaVersion = XMLHelper.getSubNodeValueByName("version", node);
		javaVendor = XMLHelper.getSubNodeValueByName("vendor", node);
		runtimeName = XMLHelper.getSubNodeValueByName("runtimeName", node);
		runtimeVersion = XMLHelper.getSubNodeValueByName("runtimeVersion", node);
		vmVersion = XMLHelper.getSubNodeValueByName("vmVersion", node);
		vmVendor = XMLHelper.getSubNodeValueByName("vmVendor", node);
		specification = XMLHelper.getSubNodeValueByName("specification", node);

		try {
			putProperty(Properties.JAVA_VERSION, new ValueVersion(javaVersion));
			putProperty(Properties.JAVA_VENDOR, new ValueString(javaVendor));
			putProperty(Properties.RUNTIME_NAME, new ValueString(runtimeName));
			putProperty(Properties.RUNTIME_VERSION, new ValueVersion(runtimeVersion));
			putProperty(Properties.VM_VERSION, new ValueVersion(vmVersion));
			putProperty(Properties.VM_VENDOR, new ValueString(vmVendor));
			putProperty(Properties.SPECIFICATION_VERSION, new ValueVersion(specification));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add basic properties to JavaInfo."; 
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element javaElement = document.createElement("javaInfo");
		
		javaElement.appendChild(XMLHelper.writeValueToElement(document, javaVersion, "version"));
		javaElement.appendChild(XMLHelper.writeValueToElement(document, javaVendor, "vendor"));
		javaElement.appendChild(XMLHelper.writeValueToElement(document, runtimeName, "runtimeName"));
		javaElement.appendChild(XMLHelper.writeValueToElement(document, vmVersion, "vmVersion"));
		javaElement.appendChild(XMLHelper.writeValueToElement(document, vmVendor, "vmVendor"));
		javaElement.appendChild(XMLHelper.writeValueToElement(document, runtimeVersion, "runtimeVersion"));
		javaElement.appendChild(XMLHelper.writeValueToElement(document, specification, "specification"));
		
		return javaElement;
	}
	
	/**
	 * Get runtime name.
	 * 
	 * @return Runtime name string (eg. Java(TM) 2 Runtime Environment, Standard Edition).
	 */
	public String getRuntimeName() {
		
		return runtimeName;
	}
	
	/**
	 * Get version of runtime.
	 * 
	 * @return Runtime version string.
	 */
	public String getRuntimeVersion() {
		
		return runtimeVersion;
	}
	
	/**
	 * Get java virtual machine version.
	 * 
	 * @return JVM version (e.g. 1.4.2_06-b03).
	 */
	public String getVMVersion() {
		
		return vmVersion;
	}
	
	/**
	 * Get java virtual machine vendor name.
	 * 
	 * @return JVM vendor name (e.g. Sun Microsystems Inc. etc.).
	 */
	public String getVMVendor() {
		
		return vmVendor;
	}
	
	/**
	 * Get version of Java.
	 * 
	 * @return Java version string.
	 */
	public String getJavaVersion() {
		
		return javaVersion;
	}
	
	/**
	 * Get Java vendor.
	 * 
	 * @return Vendor string.
	 */
	public String getJavaVendor() {
		
		return javaVendor;
	}
	
	/**
	 * Get specification version supported by Java compiler.
	 * 
	 * @return Specification version string.
	 */
	public String getSpecificationVersion() {
		
		return specification;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "Java{" + javaVersion + "}";
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "javaInfo";
	}
}
