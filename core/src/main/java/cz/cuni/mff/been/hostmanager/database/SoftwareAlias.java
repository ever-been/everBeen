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
 * Storage class for one software alias.
 *
 * @author Branislav Repcek
 */
public class SoftwareAlias extends PropertyTree
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {

	private static final long	serialVersionUID	= -1919495781024255036L;

	/**
	 * XML node name.
	 */
	public static final String ALIAS_XML_NODE_NAME = "alias";
	
	/**
	 * Name of the alias.
	 */
	private String aliasName;
	
	/**
	 * Name of the product this alias represents.
	 */
	private String productName;
	
	/**
	 * Version of the product this alias represents.
	 */
	private String productVersion;
	
	/**
	 * Vendor of the product this alias represents.
	 */
	private String productVendor;
	
	/**
	 * Names of local properties.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {

		/** Alias name */
		public static final String ALIAS_NAME = "alias";
		
		/** Product name */
		public static final String PRODUCT_NAME = "name";
		
		/** Product version */
		public static final String PRODUCT_VERSION = "version";
		
		/** Product vendor */
		public static final String PRODUCT_VENDOR = "vendor";
	}
	
	/**
	 * Create new <code>SoftwareAlias</code>.
	 * 
	 * @param aliasName Name of the alias.
	 * @param productName Name of the product this alias represents.
	 * @param productVersion Version of the product this alias represents.
	 * @param productVendor Vendor of the product this alias represents.
	 */
	public SoftwareAlias(String aliasName, String productName, String productVersion, String productVendor) {
		
		super(HostInfoInterface.Objects.SOFTWARE_ALIAS, null);
		
		this.aliasName = aliasName;
		this.productName = productName;
		this.productVersion = productVersion;
		this.productVendor = productVendor;
		
		try {
			putProperty(Properties.ALIAS_NAME, new ValueString(this.aliasName));
			putProperty(Properties.PRODUCT_NAME, new ValueString(this.productName));
			putProperty(Properties.PRODUCT_VENDOR, new ValueString(this.productVendor));
			putProperty(Properties.PRODUCT_VERSION, new ValueVersion(this.productVersion));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to the Software Alias.";
		}
	}
	
	/**
	 * Create new <code>SoftwareAlias</code> from XML file node.
	 * 
	 * @param node Node containing alias data.
	 * 
	 * @throws InputParseException If there was an error parsing input.
	 */
	public SoftwareAlias(Node node) throws InputParseException {
		
		super(HostInfoInterface.Objects.SOFTWARE_ALIAS, null);
		
		parseXMLNode(node);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {

		Element element = document.createElement(getXMLNodeName());
		
		element.appendChild(XMLHelper.writeValueToElement(document, aliasName, "alias"));
		element.appendChild(XMLHelper.writeValueToElement(document, productName, "name"));
		element.appendChild(XMLHelper.writeValueToElement(document, productVendor, "vendor"));
		element.appendChild(XMLHelper.writeValueToElement(document, productVersion, "version"));
		
		return element;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return ALIAS_XML_NODE_NAME;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {
		
		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException("Node does not contain Software Alias data.");
		}
		
		aliasName = XMLHelper.getSubNodeValueByName("alias", node);
		productName = XMLHelper.getSubNodeValueByName("name", node);
		productVersion = XMLHelper.getSubNodeValueByName("version", node);
		productVendor = XMLHelper.getSubNodeValueByName("vendor", node);
		
		try {
			putProperty(Properties.ALIAS_NAME, new ValueString(this.aliasName));
			putProperty(Properties.PRODUCT_NAME, new ValueString(this.productName));
			putProperty(Properties.PRODUCT_VENDOR, new ValueString(this.productVendor));
			putProperty(Properties.PRODUCT_VERSION, new ValueVersion(this.productVersion));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to the Software Alias.";
		}
	}

	/**
	 * @return Name of the alias.
	 */
	public String getAliasName() {
		return aliasName;
	}

	/**
	 * @return Name of the product.
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @return Product's vendor.
	 */
	public String getProductVendor() {
		return productVendor;
	}

	/**
	 * @return Version of the product.
	 */
	public String getProductVersion() {
		return productVersion;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTree#hashCode()
	 */
	@Override
	public int hashCode() {
		
		return productVendor.hashCode() + 31 * productVersion.hashCode()
		       + 967 * productName.hashCode() + 29789 * aliasName.hashCode();
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTree#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof SoftwareAlias) {
			SoftwareAlias p = (SoftwareAlias) o;
			
			return aliasName.equals(p.aliasName) && productVersion.equals(p.productVersion)
			       && productVendor.equals(p.productVendor) && productName.equals(p.productName);
		} else {
			return false;
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTree#toString()
	 */
	@Override
	public String toString() {
		
		return "Alias{" + aliasName + "|" + productName + ", " + productVendor + ", " + productVersion + "}";
	}
}
