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

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.InputParseException;

import cz.cuni.mff.been.hostmanager.util.XMLHelper;

import cz.cuni.mff.been.hostmanager.value.ValueCommonInterface;

/**
 * This class represents condition on given object and set of its properties. Object is identified 
 * by its name. Conditions on properties are specified using <code>NameValuePair</code> objects.
 * Object can be specified by its exact name (like "processor(0)" or "memory") or you can use "(?)" 
 * wildcard instead of numbers (this is similar to how existential quantifier works) - in such case 
 * all objects of the same type will be searched for match. Condition is then satisfied if at least 
 * one match is found.
 *
 * @author Branislav Repcek
 */
public class ObjectRestriction 
	implements Serializable, RestrictionInterface, XMLSerializableInterface {
	
	private static final long	serialVersionUID	= -4525625007701766228L;

	/**
	 * Name of the XML file node.
	 */
	public static final String XML_NODE_NAME = "restriction";
	
	/**
	 * Path to object.
	 */
	private String objectPath;
	
	/**
	 * List of properties and conditions on them.
	 */
	private NameValuePair []properties;
	
	/**
	 * Create simple restriction for object and one property.
	 * 
	 * @param objPath Path to object.
	 * @param propertyName Name of object's property.
	 * @param propertyValue Value which property must match.
	 * 
	 * @throws IllegalArgumentException If some parameter is <code>null</code>
	 */
	public ObjectRestriction(String objPath, String propertyName, ValueCommonInterface propertyValue) 
		throws IllegalArgumentException {
		
		if ((objPath == null) || (propertyName == null) || (propertyValue == null)) {
			throw new IllegalArgumentException("null parameters are not allowed for ObjectRestriction.");
		}

		objectPath = objPath;
		properties = new NameValuePair[1];
		properties[0] = new NameValuePair(propertyName, propertyValue);
	}
	
	/**
	 * Create simple restriction for object and one property.
	 * 
	 * @param objPath Path to the object.
	 * @param nvp <code>NameValuePair</code> object which contains name of property and condition.
	 * 
	 * @throws IllegalArgumentException If <code>null</code> parameter was passed to function.
	 */
	public ObjectRestriction(String objPath, NameValuePair nvp) throws IllegalArgumentException {

		if (objPath == null) {
			throw new IllegalArgumentException("Invalid object path (null).");
		}
		
		if (nvp == null) {
			throw new IllegalArgumentException("Invalid NameValuePair (null).");
		}

		objectPath = objPath;
		properties = new NameValuePair[1];
		properties[0] = nvp;
	}
	
	/**
	 * Create restriction for object and two properties (this is just convenience method).
	 * 
	 * @param objPath Path to the object.
	 * @param nvp1 <code>NameValuePair</code> object which contains name of property and condition.
	 * @param nvp2 <code>NameValuePair</code> object which contains name of property and condition.
	 * 
	 * @throws IllegalArgumentException If <code>null</code> parameter was passed to function.
	 */
	public ObjectRestriction(String objPath, NameValuePair nvp1, NameValuePair nvp2)
		throws IllegalArgumentException {
		
		if (objPath == null) {
			throw new IllegalArgumentException("Invalid object path (null).");
		}

		if (nvp1 == null) {
			throw new IllegalArgumentException("Invalid argument 2 ( null NameValuePair).");
		}

		if (nvp2 == null) {
			throw new IllegalArgumentException("Invalid argument 3 ( null NameValuePair).");
		}
		
		objectPath = objPath;
		properties = new NameValuePair[2];
		properties[0] = nvp1;
		properties[1] = nvp2;
	}

	/**
	 * Create restriction for object and three properties (this is just convenience method).
	 * 
	 * @param objPath Path to the object.
	 * @param nvp1 <code>NameValuePair</code> object which contains name of property and condition.
	 * @param nvp2 <code>NameValuePair</code> object which contains name of property and condition.
	 * @param nvp3 <code>NameValuePair</code> object which contains name of property and condition.
	 * 
	 * @throws IllegalArgumentException If <code>null</code> parameter was passed to function.
	 */
	public ObjectRestriction(String objPath, NameValuePair nvp1, NameValuePair nvp2, NameValuePair nvp3)
		throws IllegalArgumentException {
	
		if (objPath == null) {
			throw new IllegalArgumentException("Invalid object path (null).");
		}
	
		if (nvp1 == null) {
			throw new IllegalArgumentException("Invalid argument 2 ( null NameValuePair).");
		}
	
		if (nvp2 == null) {
			throw new IllegalArgumentException("Invalid argument 3 ( null NameValuePair).");
		}
	
		if (nvp3 == null) {
			throw new IllegalArgumentException("Invalid argument 4 ( null NameValuePair).");
		}
		
		objectPath = objPath;
		properties = new NameValuePair[3];
		properties[0] = nvp1;
		properties[1] = nvp2;
		properties[2] = nvp3;
	}
	
	/**
	 * Create restriction based on object path and list of its properties.
	 * 
	 * @param objPath Path to object.
	 * @param props Array of <code>NameValuePair</code> objects which specify conditions on respective properties.
	 * 
	 * @throws IllegalArgumentException If invalid parameters are passed to constructor.
	 */
	public ObjectRestriction(String objPath, NameValuePair []props) 
		throws IllegalArgumentException {
		
		if ((objPath == null) || (props == null)) {
			throw new IllegalArgumentException("null parameters are not allowed for ObjectRestriction.");
		}
		
		if (props.length == 0) {
			throw new IllegalArgumentException("Property array is empty.");
		}
		
		objectPath = objPath;
		properties = props;
	}
	
	/**
	 * Create new instance from data in XML file node.
	 * 
	 * @param node Node with data.
	 * 
	 * @throws InputParseException If there was an error while parsing input node.
	 */
	public ObjectRestriction(Node node) throws InputParseException {
		
		parseXMLNode(node);
	}
	
	/**
	 * Get object path.
	 * 
	 * @return String with object path.
	 */
	public String getObjectPath() {
		
		return objectPath;
	}
	
	/**
	 * Get list of properties.
	 * 
	 * @return Array of <code>NameValuePair</code> objects with property names and values.
	 */
	public NameValuePair []getProperties() {
		
		return properties;
	}
	
	/**
	 * Create string with descriptions of all conditions.
	 * 
	 * @return String representing restriction. String is multi-line.
	 */
	@Override
	public String toString() {
		
		String result = "Restriction {\n\t" + objectPath + "\n";
		
		for (int i = 0; i < properties.length; ++i) {
			result += "\t  ." + properties[i].toString() + "\n";
		}
		
		result += "}";
		
		return result;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 */
	public void parseXMLNode(Node node) throws InputParseException {

		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException("Node does not contain restriction data. Node name is \""
					+ node.getNodeName() + "\".");
		}

		objectPath = XMLHelper.getAttributeValueByName("object", node);
		
		ArrayList< Node > nvpNodes = XMLHelper.getChildNodesByName(new NameValuePair().getXMLNodeName(), node);
		
		properties = new NameValuePair[nvpNodes.size()];
		
		int i = 0;
		for (Node current: nvpNodes) {
			properties[i] = new NameValuePair(current);
			i += 1;
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement
	 */
	public Element exportAsElement(Document doc) {
		
		/* Resulting node looks like this:
		 * 
		 * <restriction object="<object-name>">
		 *    <namevalue #1/>
		 *    <namevalue #2/>
		 *          .
		 *          .
		 *          .
		 *    <namevalue #n/>
		 * </restriction>
		 * 
		 * where <object-name> is name of the object in restriction and each of the <namevalue #i>
		 * is serialised NameValuePair containing property name and value.
		 */
		
		Element element = doc.createElement(getXMLNodeName());
		
		element.setAttribute("object", objectPath);
		
		for (NameValuePair current: properties) {
			element.appendChild(current.exportAsElement(doc));
		}
		
		return element;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 */
	public String getXMLNodeName() {
		
		return XML_NODE_NAME;
	}
	
	/*
	 * @see java.lang.Object#hashCode
	 */
	@Override
	public int hashCode() {
		
		int result = objectPath.hashCode();
		
		for (NameValuePair nvp: properties) {
			result += nvp.hashCode();
		}
		
		return result;
	}
	
	/*
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof ObjectRestriction) {
			return equals((ObjectRestriction) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Compare two ObjectRestriction objects. Objects are considered equal if they specify restriction
	 * on the same property object and if they contain same conditions for same properties of given
	 * object (order does not matter).
	 * 
	 * @param o Object to compare to this.
	 * 
	 * @return <code>true</code> if both objects are equal, <code>false</code> otherwise.
	 */
	public boolean equals(ObjectRestriction o) {
		
		if (properties.length != o.properties.length) {
			return false;
		}
		
		if (!objectPath.equals(o.objectPath)) {
			return false;
		}
		
		/*HashSet< NameValuePair > right = new HashSet< NameValuePair >();
		HashSet< NameValuePair > left = new HashSet< NameValuePair >();
		
		for (int i = 0; i < properties.length; ++i) {
			right.add(o.properties[i]);
			left.add(properties[i]);
		}
		
		return result = left.containsAll(right);*/


		/* Well, this surely is not the best algorithm out there (it is O(n^2)) but at least it works
		 * (which is quite an improvement considering commented one).
		 */
		for (NameValuePair current: properties) {
			if (!isInArray(current, o.properties)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Test if given element is in array. It uses simple linear search and therefore array does not
	 * need to be sorted (this also makes this method not so suitable for sorted arrays).
	 * 
	 * @param <T> Type of the element to look for.
	 * 
	 * @param what Element which we are searching for.
	 * @param where Array in which we search.
	 * 
	 * @return <code>true</code> if given element is in array, <code>false</code> otherwise.
	 */
	private static < T > boolean isInArray(T what, T []where) {
		
		for (T current: where) {
			if (current.equals(what)) {
				return true;
			}
		}
		
		return false;
	}
}
