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
import cz.cuni.mff.been.common.value.ValueVersion;
import cz.cuni.mff.been.hostmanager.InputParseException;



/**
 * 
 * Stores informations about operating systems of unknown type (not Linux, Windows or Solaris).
 * 
 * @author Branislav Repcek
 */
public class UnknownOperatingSystem extends OperatingSystem 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {

	private static final long	serialVersionUID	= -2465522025059221594L;

	/**
	 * Constants with names of properties of UnknownOperatingSystem.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {

		/** System version. */
		public static final String VERSION = "version";
	}
	
	/**
	 * Version string.
	 */
	private String osVersion; // os.version
	
	/**
	 * Properties.
	 */
	private PropertyTree obj;

	/**
	 * Create and initialise object with given info.
	 *  
	 * @param name OS name.
	 * @param vendor OS vendor.
	 * @param arch Target architecture.
	 * @param version OS version.
	 */
	public UnknownOperatingSystem(String name, String vendor, String arch, String version) {
	
		super(name, vendor, arch, "other");
		
		osVersion = version;
		
		obj = new PropertyTree(OperatingSystem.Objects.OTHER, this);
		
		try {
			obj.putProperty(Properties.VERSION, new ValueVersion(osVersion));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to UnknownOperatingSystem.";
		}
	}
	
	/**
	 * Initialise class with data from XML node.
	 * 
	 * @param osNode Node with data.
	 * 
	 * @throws InputParseException If there was an error while parsing data.
	 */
	public UnknownOperatingSystem(Node osNode) throws InputParseException {
		
		super("Unknown");
		
		obj = new PropertyTree(OperatingSystem.Objects.OTHER, this);
		
		parseXMLNode(osNode);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {
		
		parseBasicInfoNode(node);
		
		Node advancedInfo = XMLHelper.getSubNodeByName("advancedInfo", node);
		osVersion = XMLHelper.getSubNodeValueByName("version", advancedInfo);
		
		try {
			obj.putProperty(Properties.VERSION, new ValueVersion(osVersion));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to UnknownOperatingSystem.";
		}
	}
	
	/**
	 * Gets version of operating system.
	 *  
	 * @return Version identification string.
	 */
	public String getVersion() {
		
		return osVersion;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element osElement = document.createElement("operatingSystem");
		Element advancedInfoElement = document.createElement("advancedInfo");
		
		osElement.appendChild(exportBasicAsElement(document));
		advancedInfoElement.appendChild(XMLHelper.writeValueToElement(document, osVersion, "version"));
		osElement.appendChild(advancedInfoElement);
		
		return osElement;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "UnknownOS{" + getName() + ", " + osVersion + "}";
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "operatingSystem";
	}
}
