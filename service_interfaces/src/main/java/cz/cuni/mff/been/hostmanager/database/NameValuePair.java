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

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.common.value.ValueCommonInterface;

import cz.cuni.mff.been.hostmanager.InputParseException;



/**
 * Class which provides user with means to specify condition for given property of object. 
 * For list of properties see sources for other HostInfo classes.
 *
 * @see cz.cuni.mff.been.hostmanager.database.HostInfo
 * @see cz.cuni.mff.been.hostmanager.database.DiskDrive
 * @see cz.cuni.mff.been.hostmanager.database.DiskPartition
 * @see cz.cuni.mff.been.hostmanager.database.JavaInfo
 * @see cz.cuni.mff.been.hostmanager.database.Memory
 * @see cz.cuni.mff.been.hostmanager.database.OperatingSystem
 * @see cz.cuni.mff.been.hostmanager.database.LinuxOperatingSystem
 * @see cz.cuni.mff.been.hostmanager.database.WindowsOperatingSystem
 * @see cz.cuni.mff.been.hostmanager.database.SolarisOperatingSystem
 * @see cz.cuni.mff.been.hostmanager.database.UnknownOperatingSystem
 * @see cz.cuni.mff.been.hostmanager.database.NetworkAdapter
 * @see cz.cuni.mff.been.hostmanager.database.Memory
 * @see cz.cuni.mff.been.hostmanager.database.Processor
 * @see cz.cuni.mff.been.hostmanager.database.Product
 * 
 * @author Branislav Repcek
 */
public class NameValuePair extends Pair< String, ValueCommonInterface > 
	implements Serializable, XMLSerializableInterface {
	
	private static final long	serialVersionUID	= 3152331978099497754L;

	/**
	 * Create empty <code>NameValuePair</code>.
	 */
	public NameValuePair() {

		super("(none)", null);
	}
	
	/**
	 * Create new <code>NameValuePair</code> with given parameters.
	 * 
	 * @param newName Name of characteristic.
	 * @param newValue Condition.
	 * 
	 * @throws IllegalArgumentException null parameters or empty name.
	 */
	public NameValuePair(String newName, ValueCommonInterface newValue) {
		
		super(newName, newValue);
		
		if ((newName == null) || (newValue == null)) {
			
			throw new IllegalArgumentException("null parameters are not allowed in NameValuePair.");
		}
		
		if (newName.length() == 0) {
			
			throw new IllegalArgumentException("Empty name is not allowed in NameValuePair.");
		}
	}
	
	/**
	 * Create new instance from data contained in XML node.
	 * 
	 * @param node Node with data.
	 * 
	 * @throws InputParseException If there was an error while parsing node data.
	 */
	public NameValuePair(Node node) throws InputParseException {
		
		super("", null);
		
		parseXMLNode(node);
	}
	
	/**
	 * Get name of characteristic.
	 * 
	 * @return Characteristic name string.
	 */
	public String getName() {
		
		return getKey();
	}
	
	/**
	 * Create textual representation of property and value of condition.
	 * 
	 * @return String containing property and value.
	 */
	@Override
	public String toString() {
		
		return getKey() + "=" + getValue().toString();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 */
	public void parseXMLNode(Node node) throws InputParseException {
		
		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException("Node does not contain NameValuePair data. Node name is \""
					+ node.getNodeName() + "\".");
		}

		String name = XMLHelper.getAttributeValueByName("name", node);
		String valueType = XMLHelper.getAttributeValueByName("valueType", node);
		ValueCommonInterface value = null;

		try {
			value = (ValueCommonInterface) Class.forName(valueType).newInstance();
		} catch (Exception e) {
			throw new InputParseException("Unable to create new instance of value type.", e);
		}

		value.parseXMLNode(XMLHelper.getSubNodeByName(value.getXMLNodeName(), node));

		setKey(name);
		setValue(value);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement
	 */
	public Element exportAsElement(Document doc) {
		
		/* Node looks like this:
		 * 
		 * <namevalue name="<name>" valueType="<type>">
		 *    <data-node>
		 * </namevalue>
		 * 
		 * where <name> is string with name of the property, <type> is canonical name of the 
		 * property value type and <data-node> is serialised form of the value (for more info
		 * see serialisation routines for all Value* types).
		 */
		
		Element element = doc.createElement(getXMLNodeName());
		
		element.setAttribute("name", getKey());
		element.setAttribute("valueType", getValue().getClass().getCanonicalName());
		
		element.appendChild(getValue().exportAsElement(doc));
		
		return element;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 */
	public String getXMLNodeName() {
		
		return XML_NODE_NAME;
	}
	
	/**
	 * Name of XML node.
	 */
	public static final String XML_NODE_NAME = "namevalue";
	
	/*
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof NameValuePair) {
			return equals((NameValuePair) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Compare two NameValuePair objects. Objects are considered equal if they have same name and
	 * same value.
	 * 
	 * @param o Object to compare to this.
	 * 
	 * @return <code>true</code> if objects are equal, <code>false</code> otherwise.
	 */
	public boolean equals(NameValuePair o) {
		
		return getName().equals(o.getName()) && getValue().equals(o.getValue());
	}

	/*
	 * @see java.lang.Object#hashCode
	 */
	@Override
	public int hashCode() {
		
		return getName().hashCode() + getValue().hashCode();
	}
}
