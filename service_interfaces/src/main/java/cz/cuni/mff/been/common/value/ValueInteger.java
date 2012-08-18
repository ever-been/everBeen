/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Branislav Repcek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package cz.cuni.mff.been.common.value;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;

/**
 * Class representing integer number. It can store number in range of
 * <code>Long</code>
 * 
 * @author Branislav Repcek
 */
public class ValueInteger implements ValueBasicInterface<ValueInteger>,
		Serializable, XMLSerializableInterface {

	private static final long serialVersionUID = -9178170456370084266L;

	/**
	 * Value of this object.
	 */
	private long value;

	/**
	 * Unit of measure.
	 */
	private final String unit;

	/**
	 * Create new <code>ValueInteger</code> from XML node.
	 * 
	 * @param node
	 *            Node containing data.
	 * 
	 * @throws InputParseException
	 *             If there was an error parsing node data.
	 */
	public ValueInteger(Node node) throws InputParseException {

		parseXMLNode(node);
		unit = null;
	}

	/**
	 * Create new <code>ValueInteger</code> from XML node and set unit to given
	 * one.
	 * 
	 * @param node
	 *            Node containing data.
	 * @param unit
	 *            Unit.
	 * 
	 * @throws InputParseException
	 *             If there was an error parsing node data.
	 */
	public ValueInteger(Node node, String unit) throws InputParseException {

		parseXMLNode(node);
		this.unit = unit;
	}

	/**
	 * Allocate zero <code>ValueInteger</code> value.
	 */
	public ValueInteger() {

		value = 0;
		unit = null;
	}

	/**
	 * Create new <code>ValueInteger</code> object from <code>ValueDouble</code>
	 * object.
	 * 
	 * @param v
	 *            <code>ValueDouble</code> which will be used to initialise
	 *            this.
	 */
	public ValueInteger(ValueDouble v) {

		value = v.intValue();
		unit = v.getUnit();
	}

	/**
	 * Create new <code>ValueInteger</code> value,
	 * 
	 * @param newValue
	 *            New value.
	 */
	public ValueInteger(long newValue) {

		value = newValue;
		unit = null;
	}

	/**
	 * Create new <code>ValueInteger</code> value,
	 * 
	 * @param newValue
	 *            New value.
	 * @param unit
	 *            Unit.
	 */
	public ValueInteger(long newValue, String unit) {

		value = newValue;
		this.unit = unit;
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
	@Override
	public int compareTo(ValueInteger vb) {

		return (int) (value - vb.value);
	}

	/*
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object vc) {

		if (vc instanceof ValueInteger) {
			return equals((ValueInteger) vc);
		} else {
			return false;
		}
	}

	/**
	 * Test whether other <code>ValueInteger</code> is equal to this.
	 * 
	 * @param v
	 *            Value to compare to.
	 * @return <code>true</code> if values are equal, <code>false</code>
	 *         otherwise.
	 */
	public boolean equals(ValueInteger v) {

		return v.value == value;
	}

	/**
	 * Test for equality.
	 * 
	 * @param l
	 *            Number to test against.
	 * 
	 * @return <code>true</code> if both numbers have same value,
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(long l) {

		return value == l;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#greaterThan
	 */
	@Override
	public boolean greaterThan(Object o) {

		if (o instanceof ValueInteger) {
			return greaterThan((ValueInteger) o);
		} else if (o instanceof ValueDouble) {
			return greaterThan(new ValueInteger((ValueDouble) o));
		} else {
			return false;
		}
	}

	/**
	 * Test whether this object is greater than given object.
	 * 
	 * @param vb
	 *            Object to test.
	 * @return <code>true</code> if current object is greater than
	 *         <code>vb</code>, <code>false</code> otherwise.
	 */
	public boolean greaterThan(ValueInteger vb) {

		return value > vb.value;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#lessThan
	 */
	@Override
	public boolean lessThan(Object o) {

		if (o instanceof ValueInteger) {
			return lessThan((ValueInteger) o);
		} else if (o instanceof ValueDouble) {
			return lessThan(new ValueInteger((ValueDouble) o));
		} else {
			return false;
		}
	}

	/**
	 * Test whether this object is less than given object.
	 * 
	 * @param vb
	 *            Object to test.
	 * @return <code>true</code> if current object is less than <code>vb</code>,
	 *         <code>false</code> otherwise.
	 */
	public boolean lessThan(ValueInteger vb) {

		return value < vb.value;
	}

	/*
	 * @see java.lang.Object#hashCode
	 */
	@Override
	public int hashCode() {

		return (new Long(value)).hashCode();
	}

	/*
	 * @see
	 * cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {

		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException(
					"Node does not contain integer data. Node name is \""
							+ node.getNodeName() + "\".");
		}

		String attr = XMLHelper.getAttributeValueByName("value", node);

		try {
			value = Long.valueOf(attr).longValue();
		} catch (NumberFormatException e) {
			throw new InputParseException("Invalid number \"" + attr + "\".", e);
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#
	 * exportAsElement
	 */
	@Override
	public Element exportAsElement(Document document) {

		/*
		 * Resulting node:
		 * 
		 * <integer value="123456"/>
		 */
		Element element = document.createElement(getXMLNodeName());

		element.setAttribute("value", String.valueOf(value));

		return element;
	}

	/*
	 * @see
	 * cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 */
	@Override
	public String getXMLNodeName() {

		return "integer";
	}

	/**
	 * Return value of this object as long.
	 * 
	 * @return long containing value of this object.
	 */
	public long longValue() {

		return value;
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

		return value;
	}

	/**
	 * @param value
	 *            New value to be stored in object.
	 */
	public void setValue(long value) {

		this.value = value;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#getUnit()
	 */
	@Override
	public String getUnit() {

		return unit;
	}

	@Override
	public ValueType getType() {

		return ValueType.INTEGER;
	}

	@Override
	public ValueType getElementType() {

		return getType();
	}
}
