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

package cz.cuni.mff.been.hostmanager.value;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.util.XMLHelper;

import cz.cuni.mff.been.hostmanager.InputParseException;

import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;

/**
 * Class which represents boolean value.
 *
 * @author Branislav Repcek
 */
public class ValueBoolean 
	implements ValueBasicInterface< ValueBoolean >, Serializable, XMLSerializableInterface {
	
	private static final long	serialVersionUID	= 8531197229782451455L;

	/**
	 * Value of this object.
	 */
	private boolean value;	
	
	/**
	 * Allocate empty <code>ValueBoolean</code> value.
	 */
	public ValueBoolean() {
		
		value = false;
	}
	
	/**
	 * Initialise instance from the node in XML file.
	 * 
	 * @param node Node with data.
	 * 
	 * @throws InputParseException If there was an error while parsing input.
	 */
	public ValueBoolean(Node node) throws InputParseException {
		
		parseXMLNode(node);
	}
	
	/** 
	 * Create new <code>ValueBoolean</code> value.
	 * 
	 * @param newValue Value.
	 */
	public ValueBoolean(boolean newValue) {
		
		value = newValue;
	}

	/*
	 * @see java.lang.Object#toString
	 */
	@Override
	public String toString() {
		
		if (value) {
			return "true";
		} else {
			return "false";
		}
	}
	
	/*
	 * @see java.lang.Comparable#compareTo
	 */
	public int compareTo(ValueBoolean vb) {

		if (value && !vb.value) {
			return 1;
		} else if (!value && vb.value) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Compare specified <code>ValueBoolean</code> with this value for equality.
	 * 
	 * @param vc Value to compare this to.
	 * @return <code>true</code> if values are equal, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object vc) {
		
		if (vc instanceof ValueBoolean) {
			return equals((ValueBoolean) vc);
		} else {
			return false;
		}
	}
	
	/**
	 * Test two values for equality.
	 * 
	 * @param v Value to test with.
	 * @return <code>true</code> if values are equal, <code>false</code> otherwise.
	 */
	public boolean equals(ValueBoolean v) {
		
		return value == v.value;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#greaterThan
	 */
	public boolean greaterThan(Object o) {
		
		if (o instanceof ValueBoolean) {
			return greaterThan((ValueBoolean) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Test whether this object is greater than given object.
	 * 
	 * @param b Object to test.
	 * @return <code>true</code> if current object is less than <code>b</code>, <code>false</code>
	 *         otherwise. Note that <code>true</code> is greater than <code>false</code>.
	 */
	public boolean greaterThan(ValueBoolean b) {
		
		return (value && !b.value);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#lessThan
	 */
	public boolean lessThan(Object o) {
		
		if (o instanceof ValueBoolean) {
			return lessThan((ValueBoolean) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Test whether this object is less than given object.
	 * 
	 * @param b Object to test.
	 * @return <code>true</code> if current object is less than <code>vb</code>, <code>false</code>
	 *         otherwise. Note that <code>false</code> is less than <code>true</code>.
	 */
	public boolean lessThan(ValueBoolean b) {
		
		return (!value && b.value);
	}

	/*
	 * @see java.lang.Object#hashCode
	 */
	@Override
	public int hashCode() {

		return value ? 1231 : 1237;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 */
	public void parseXMLNode(Node node) throws InputParseException {
		
		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException("Node does not contain boolean data. Node name is \""
					+ node.getNodeName() + "\".");
		}

		String v = XMLHelper.getAttributeValueByName("value", node);
		
		if (v.equalsIgnoreCase("true")) {
			value = true;
		} else if (v.equalsIgnoreCase("false")) {
			value = false;
		} else {
			throw new InputParseException("Invalid data in boolean value.");
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement
	 */
	public Element exportAsElement(Document document) {
		
		/*
		 * Resulting node:
		 * 
		 * <boolean value="<value>"/>
		 * 
		 * where <value> is "true" or "false"
		 * 
		 */

		Element element = document.createElement(getXMLNodeName());
		
		element.setAttribute("value", value ? "true" : "false");
		
		return element;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 */
	public String getXMLNodeName() {
		
		return "boolean";
	}
	
	/**
	 * @return Value stored in this object.
	 */
	public boolean getValue() {
		
		return value;
	}

	/**
	 * @param value New value that will be stored in this object.
	 */
	public void setValue(boolean value) {
		
		this.value = value;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#getUnit()
	 */
	public String getUnit() {
		
		return null;
	}

	@Override
	public ValueType getType() {
		
		return ValueType.BOOLEAN;
	}

	@Override
	public ValueType getElementType() {
		
		return getType();
	}
}
