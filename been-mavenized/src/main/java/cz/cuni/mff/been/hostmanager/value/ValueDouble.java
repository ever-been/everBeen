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

import cz.cuni.mff.been.hostmanager.InputParseException;

import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;

import cz.cuni.mff.been.hostmanager.util.XMLHelper;

/**
 * Class representing floating-point numbers in the range of the built-in java double.
 *
 * @author Branislav Repcek
 */
public class ValueDouble 
	implements Serializable, XMLSerializableInterface, ValueBasicInterface< ValueDouble > {

	private static final long	serialVersionUID	= 2468434524450984466L;

	/**
	 * Value stored in the class.
	 */
	private double value;
	
	/**
	 * Unit of measure.
	 */
	private String unit;
	
	/**
	 * Create new instance with given value. Unit is set to <tt>null</tt>.
	 * 
	 * @param value New value.
	 */
	public ValueDouble(long value) {
		
		this.value = value;
	}
	
	/**
	 * Create new instance with given value and unit.
	 * 
	 * @param value New value.
	 * @param unit Unit of measure for the value.
	 */
	public ValueDouble(long value, String unit) {
		
		this.value = value;
		this.unit = unit;
	}
	
	/**
	 * Create new instance with given value.
	 * 
	 * @param value New value.
	 */
	public ValueDouble(double value) {
		
		this.value = value;
	}
	
	/**
	 * Create new instance with given value.
	 * 
	 * @param value New value.
	 * @param unit Unit of measure for given value.
	 */
	public ValueDouble(double value, String unit) {
		
		this.value = value;
		this.unit = unit;
	}

	/**
	 * Create new <code>ValueDouble</code> object from <code>ValueInteger</code>.
	 * 
	 * @param v <code>ValueInteger</code> which will be used to initialize this.
	 */
	public ValueDouble(ValueInteger v) {
		
		this.value = v.doubleValue();
		this.unit = v.getUnit();
	}
	
	/**
	 * Create new instance from data in XML node.
	 * 
	 * @param node Node with data.
	 * 
	 * @throws InputParseException If there was and error while parsing XML node data.
	 */
	public ValueDouble(Node node) throws InputParseException {
		
		parseXMLNode(node);
		
		unit = null;
	}
	
	/**
	 * Create new instance from data in XML node and set unit to given one.
	 * 
	 * @param node Node with data.
	 * @param unit Unit.
	 * 
	 * @throws InputParseException If there was and error while parsing XML node data.
	 */
	public ValueDouble(Node node, String unit) throws InputParseException {
		
		parseXMLNode(node);
		
		this.unit = unit;
	}

	/**
	 * Create new instance with zero value.
	 */
	public ValueDouble() {
		
		value = 0.0;
		unit = null;
	}

	/*
	 * @see java.lang.Object#toString
	 */
	@Override
	public String toString() {

		return String.valueOf(value);
	}

	/*
	 * @see java.lang.Comparable#compareTo
	 */
	public int compareTo(ValueDouble d) {
		
		return (int) (value - d.value);
	}
	
	/*
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof ValueDouble) {
			return equals((ValueDouble) o);
		} else { 
			return false;
		}
	}

	/**
	 * Test for equality.
	 * 
	 * @param d Number to test against.
	 * 
	 * @return <code>true</code> if both numbers have same value, <code>false</code> otherwise.
	 */
	public boolean equals(ValueDouble d) {

		return value == d.value;
	}

	/**
	 * Test for equality. This test may not be accurate for floating-point numbers.
	 * 
	 * @param d Number to test against.
	 * 
	 * @return <code>true</code> if both numbers have same value, <code>false</code> otherwise.
	 */
	public boolean equals(double d) {
		
		return value == d;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		return (int) value;
	}
	
	/**
	 * Test for equality with epsilon.
	 * 
	 * @param d Number to test against.
	 * @param epsilon Maximum distance numbers are allowed to be apart to pass the test.
	 * 
	 * @return <code>true</code> if argument lies in the open interval (this-epsilon, this+epsilon),
	 *         <code>false</code> otherwise.
	 */
	public boolean equalsEpsilon(ValueDouble d, double epsilon) {
		
		return Math.abs(value - d.value) < epsilon;
	}
	
	/**
	 * Test for equality with epsilon.
	 * 
	 * @param d Number to test against.
	 * @param epsilon Maximum distance numbers are allowed to be apart to pass the test.
	 * 
	 * @return <code>true</code> if argument lies in the open interval (this-epsilon, this+epsilon),
	 *         <code>false</code> otherwise.
	 */
	public boolean equalsEpsilon(double d, double epsilon) {
		
		return Math.abs(value - d) < epsilon;
	}

	/**
	 * Return value of this object as long.
	 * 
	 * @return long containing value of this object.
	 */
	public long longValue() {
		
		return (long) value;
	}
	
	/**
	 * Return value of this object as double.
	 * 
	 * @return double containing value of this object.
	 */
	public double doubleValue() {
		
		return value;
	}
	
	/**
	 * Return value of this object as int.
	 * 
	 * @return int containing value of this object.
	 */
	public int intValue() {
		
		return (int) value;
	}
	
	/**
	 * Return value of this object as float.
	 * 
	 * @return float containing value of this object.
	 */
	public float floatValue() {
		
		return (float) value;
	}

	/**
	 * @param value New value to be stored in this object.
	 */
	public void setValue(double value) {
		
		this.value = value;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#lessThan
	 */
	public boolean lessThan(Object o) {
		
		if (o instanceof ValueDouble) {
			return lessThan((ValueDouble) o);
		} else if (o instanceof ValueInteger) {
			return lessThan(new ValueDouble((ValueInteger) o));
		} else {
			return false;
		}
	}
	
	/**
	 * Test whether this object is less than given object.
	 * 
	 * @param d Object to test.
	 * @return <code>true</code> if current object is less than <code>d</code>, <code>false</code>
	 *         otherwise. 
	 */
	public boolean lessThan(ValueDouble d) {
		
		return value < d.value;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#greaterThan
	 */
	public boolean greaterThan(Object o) {
		
		if (o instanceof ValueDouble) {
			return greaterThan((ValueDouble) o);
		} else if (o instanceof ValueInteger) {
			return greaterThan(new ValueDouble((ValueInteger) o));
		} else {
			return false;
		}
	}
	
	/**
	 * Test whether this object is greater than given object.
	 * 
	 * @param d Object to test.
	 * @return <code>true</code> if current object is greater than <code>d</code>, <code>false</code>
	 *         otherwise. 
	 */
	public boolean greaterThan(ValueDouble d) {
		
		return value > d.value;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 */
	public String getXMLNodeName() {
		
		return "double";
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement
	 */
	public Element exportAsElement(Document doc) {
		
		Element element = doc.createElement(getXMLNodeName());
		
		element.setAttribute("value", toString());
		
		return element;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 */
	public void parseXMLNode(Node node) throws InputParseException {

		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException("Node does not contain double data. Node name is \""
					+ node.getNodeName() + "\".");
		}

		value = Double.valueOf(XMLHelper.getAttributeValueByName("value", node)).doubleValue();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#getUnit()
	 */
	public String getUnit() {
		
		return unit;
	}

	@Override
	public ValueType getType() {
		
		return ValueType.DOUBLE;
	}

	@Override
	public ValueType getElementType() {
		
		return getType();
	}
}
