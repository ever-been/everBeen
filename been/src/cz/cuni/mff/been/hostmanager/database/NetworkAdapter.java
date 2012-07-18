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

/**
 * Class which stores informations about network adapter (on Windows) or about network 
 * interface (on Linux and Solaris).
 * It does not contain any detection routines.
 *
 * @author Branislav Repcek
 */
public class NetworkAdapter extends PropertyTree 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {

	private static final long	serialVersionUID	= -1032771693107719780L;

	/**
	 * Encapsulates constants with names of properties of NetworkAdapter object.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
		/**
		 * Adapter name.
		 */
		public static final String NAME = "name";
		
		/**
		 * Vendor name.
		 */
		public static final String VENDOR = "vendor";
		
		/**
		 * Adapter type.
		 */
		public static final String TYPE = "type";
		
		/**
		 * MAC address.
		 */
		public static final String MAC_ADDRESS = "mac";
	}
	
	/**
	 * Adapter/Interface name.
	 */
	private String name;
	
	/**
	 * Adapter vendor. (Does not apply to interfaces).
	 */
	private String vendor;
	
	/**
	 * Type of adapter/interface (eg. Ethernet, Loopback, etc.).
	 */
	private String type;
	
	/**
	 * MAC Address of adapter/interface (if applicable, some windows virtual adapters do not have MAC).
	 * It consists of 6 hexadecimal 8-bit numbers (bytes) delimited by colon character, e.g.: "50:50:54:50:30:3F".
	 */
	private String macAddress;
	
	/**
	 * Initialise class with data.
	 * 
	 * @param name Name of network adapter or interface.
	 * @param vendor Vendor of adapter (does not apply to interfaces, set it to empty string).
	 * @param type Type of adapter/interface (e.g. network protocol etc.)
	 * @param adapterMAC MAC address of device if it has one.
	 */
	public NetworkAdapter(String name, String vendor, String type, String adapterMAC) {
		
		super(HostInfoInterface.Objects.ADAPTER, null);

		this.name = name;
		this.vendor = vendor;
		this.type = type;
		this.macAddress = adapterMAC;
		
		try {
			putProperty(Properties.NAME, new ValueString(this.name));
			putProperty(Properties.VENDOR, new ValueString(this.vendor));
			putProperty(Properties.TYPE, new ValueString(this.type));
			putProperty(Properties.MAC_ADDRESS, new ValueString(this.macAddress));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to NetworkAdapter.";
		}
	}

	/**
	 * Read info about adapter from XML file node.
	 * 
	 * @param adapterNode <code>networkAdapter</code> node from XML file.
	 * @throws InputParseException if error occurred when parsing node data.
	 */
	public NetworkAdapter(Node adapterNode) throws InputParseException {
		
		super(HostInfoInterface.Objects.ADAPTER, null);

		parseXMLNode(adapterNode);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {

		name = XMLHelper.getSubNodeValueByName("name", node);
		vendor = XMLHelper.getSubNodeValueByName("vendor", node);
		type = XMLHelper.getSubNodeValueByName("adapterType", node);
		macAddress = XMLHelper.getSubNodeValueByName("macAddress", node);
		
		try {
			putProperty(Properties.NAME, new ValueString(name));
			putProperty(Properties.VENDOR, new ValueString(vendor));
			putProperty(Properties.TYPE, new ValueString(type));
			putProperty(Properties.MAC_ADDRESS, new ValueString(macAddress));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to NetworkAdapter.";
		}
	}
	
	/**
	 * Get name of adapter or interface.
	 * 
	 * @return Adapter/interface name.
	 */
	public String getName() {
		
		return name;
	}
	
	/**
	 * Get vendor of adapter.
	 * 
	 * @return Adapter vendor.
	 */
	public String getVendor() {
		
		return vendor;
	}
	
	/**
	 * Get type of adapter/interface (eg. protocol).
	 * 
	 * @return String with description of adapter's type.
	 */
	public String getType() {
		
		return type;
	}
	
	/**
	 * Get MAC address of adapter/interface.
	 * 
	 * @return MAC address or "unknown" when device does not have MAC. 
	 */
	public String getMacAddress() {
		
		return macAddress;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element networkAdapterElement = document.createElement("networkAdapter");
		
		networkAdapterElement.appendChild(XMLHelper.writeValueToElement(document, name, "name"));
		networkAdapterElement.appendChild(XMLHelper.writeValueToElement(document, vendor, "vendor"));
		networkAdapterElement.appendChild(XMLHelper.writeValueToElement(document, type, "adapterType"));
		networkAdapterElement.appendChild(XMLHelper.writeValueToElement(document, macAddress, "macAddress"));
		
		return networkAdapterElement;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return name;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "networkAdapter";
	}
}
