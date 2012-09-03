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
 * Class which stores informations about software product installed on system (application, package...).
 *
 * @author Branislav Repcek
 */
public class Product extends PropertyTree 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {

	private static final long	serialVersionUID	= 2051048367584655976L;

	/**
	 * Encapsulation of constants with names of properties of Product object. 
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
		/**
		 * Product name.
		 */
		public static final String NAME = "name";
		
		/**
		 * Product vendor.
		 */
		public static final String VENDOR = "vendor";
		
		/**
		 * Product version.
		 */
		public static final String VERSION = "version";
	}
	
	/**
	 * name of product.
	 */
	private String name;
	
	/**
	 * Product vendor.
	 */
	private String vendor;
	
	/**
	 * Product version.
	 */
	private String version;
	
	/**
	 * Initialise product data.
	 * 
	 * @param name Name of product.
	 * @param vendor Vendor of product.
	 * @param version Version of product.
	 */
	public Product(String name, String vendor, String version) {
		
		super(HostInfoInterface.Objects.APPLICATION, null);
		
		this.name = name;
		this.vendor = vendor;
		this.version = version;
		
		try {
			putProperty(Properties.NAME, new ValueString(this.name));
			putProperty(Properties.VENDOR, new ValueString(this.vendor));
			putProperty(Properties.VERSION, new ValueVersion(this.version));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to the Product.";
		}
	}
	
	/**
	 * Read product info from XML file node.
	 * 
	 * @param prodNode <code>product</code> node from host XML file.
	 * @throws InputParseException if error occurred when parsing file.
	 */
	public Product(Node prodNode) throws InputParseException {
		
		super(HostInfoInterface.Objects.APPLICATION, null);
		
		parseXMLNode(prodNode);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {
		
		name = XMLHelper.getSubNodeValueByName("name", node);
		vendor = XMLHelper.getSubNodeValueByName("vendor", node);
		version = XMLHelper.getSubNodeValueByName("version", node);
		
		try {
			putProperty(Properties.NAME, new ValueString(name));
			putProperty(Properties.VENDOR, new ValueString(vendor));
			putProperty(Properties.VERSION, new ValueVersion(version));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to the Product.";
		}
	}
	
	/**
	 * Get product name.
	 * 
	 * @return Product name.
	 */
	public String getName() {
		
		return name;
	}
	
	/**
	 * Get product vendor.
	 * 
	 * @return Product vendor.
	 */
	public String getVendor() {
		
		return vendor;
	}
	
	/**
	 * Get product version.
	 * 
	 * @return Product version.
	 */
	public String getVersion() {
		
		return version;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element productElement = document.createElement("product");
		
		productElement.appendChild(XMLHelper.writeValueToElement(document, name, "name"));
		productElement.appendChild(XMLHelper.writeValueToElement(document, vendor, "vendor"));
		productElement.appendChild(XMLHelper.writeValueToElement(document, version, "version"));
		
		return productElement;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "Product{" + name + ", " + vendor + ", " + version + "}";
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "product";
	}
}
