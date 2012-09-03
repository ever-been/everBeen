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

import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.InputParseException;

import java.io.Serializable;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * This class stores details about the Solaris operating system running on the host.
 * 
 * @author Branislav Repcek
 */
public class SolarisOperatingSystem extends OperatingSystem 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {
	
	private static final long	serialVersionUID	= -3541802878775550605L;

	/**
	 * Encapsulation of constants with names of properties of SolarisOperatingSystem.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
	}
	
	/**
	 * Sub-objects which contains properties.
	 */
	@SuppressWarnings("unused")
	private PropertyTree obj;
	
	/**
	 * Create and initialise object.
	 * 
	 * @param name Operating system name.
	 * @param vendor Operating system vendor.
	 * @param arch Architecture of computer.
	 */
	public SolarisOperatingSystem(String name, String vendor, String arch) {
		
		super(name, vendor, arch, "Solaris");
		
		obj = new PropertyTree(OperatingSystem.Objects.SOLARIS, this);
	}
	
	/**
	 * Read OS data from Node from host XML file.
	 * 
	 * @param osNode <code>operatingSystem</code> node from XML file.
	 * @throws InputParseException if error occurred when parsing file.
	 */	
	public SolarisOperatingSystem(Node osNode) throws InputParseException {
		
		super("Solaris");
		
		obj = new PropertyTree(OperatingSystem.Objects.SOLARIS, this);
		
		parseXMLNode(osNode);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {

		Element osElement = document.createElement("operatingSystem");
		
		osElement.appendChild(exportBasicAsElement(document));
		
		Element advancedInfoElement = document.createElement("advancedInfo");
		
		osElement.appendChild(advancedInfoElement);
		
		return osElement;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {
		
		parseBasicInfoNode(node);
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "Solaris{}";
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "operatingSystem";
	}
}
