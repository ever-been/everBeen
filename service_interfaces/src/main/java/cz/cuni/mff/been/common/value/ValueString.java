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
 * Class which represents strings.
 * 
 * @author Branislav Repcek
 * 
 */
public class ValueString implements ValueBasicInterface<ValueString>,
		Serializable, XMLSerializableInterface {

	private static final long serialVersionUID = 4017528991264060045L;

	/**
	 * Value of this object.
	 */
	private String value;

	/*
	 * Flag which specifies whether case-sensitive methods are used when
	 * comparing strings. Default value is false (that is, strings are not
	 * case-sensitive).
	 */
	private boolean caseSense;

	/**
	 * Allocate empty <code>ValueString</code> value.
	 */
	public ValueString() {

		caseSense = false;
		value = "";
	}

	/**
	 * Create new <code>ValueString</code> with given value.
	 * 
	 * @param newValue
	 *            Value.
	 */
	public ValueString(String newValue) {

		caseSense = false;
		value = newValue;
	}

	/**
	 * Read data from XML node.
	 * 
	 * @param node
	 *            Node with data.
	 * 
	 * @throws InputParseException
	 *             If there was an error while parsing node.
	 */
	public ValueString(Node node) throws InputParseException {

		parseXMLNode(node);
	}

	/**
	 * Create new ValueString object.
	 * 
	 * @param newValue
	 *            Value.
	 * @param caseSensitive
	 *            If <code>true</code>, string will be case-sensitive.
	 */
	public ValueString(String newValue, boolean caseSensitive) {

		caseSense = caseSensitive;
		value = newValue;
	}

	/*
	 * @see java.lang.Object#toString
	 */
	@Override
	public String toString() {

		return value;
	}

	/*
	 * @see java.lang.Comparable#compareTo
	 */
	@Override
	public int compareTo(ValueString vb) {

		if (caseSense && vb.caseSense) {
			return value.compareTo(vb.value);
		} else {
			return value.compareToIgnoreCase(vb.value);
		}
	}

	/*
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof ValueString) {
			return equals((ValueString) o);
		} else {
			return false;
		}
	}

	/**
	 * Test specified <code>ValueString</code> with this value for equality. If
	 * at least one of strings to compare is case insensitive, comparison is
	 * also case insensitive.
	 * 
	 * @param vc
	 *            Value to compare this to.
	 * @return <code>true</code> if values are equal, <code>false</code>
	 *         otherwise.
	 */
	public boolean equals(ValueString vc) {

		if (caseSense && vc.caseSense) {
			return value.equals(vc.value);
		} else {
			return value.equalsIgnoreCase(vc.value);
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#greaterThan
	 */
	@Override
	public boolean greaterThan(Object o) {

		if (o instanceof ValueString) {
			return greaterThan((ValueString) o);
		} else {
			return false;
		}
	}

	/**
	 * Test if current value is greater than specified one.
	 * 
	 * @param v
	 *            Value to compare to.
	 * 
	 * @return <code>true</code> if this instance has greater value than given
	 *         value, <code>false</code> otherwise.
	 */
	public boolean greaterThan(ValueString v) {

		if (caseSense && v.caseSense) {
			return value.compareTo(v.value) > 0;
		} else {
			return value.compareToIgnoreCase(v.value) > 0;
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#lessThan
	 */
	@Override
	public boolean lessThan(Object o) {

		if (o instanceof ValueString) {
			return lessThan((ValueString) o);
		} else {
			return false;
		}
	}

	/**
	 * Test if current value is less than specified one.
	 * 
	 * @param v
	 *            Value to compare to.
	 * 
	 * @return <code>true</code> if this instance has lower value than given
	 *         value, <code>false</code> otherwise.
	 */
	public boolean lessThan(ValueString v) {

		if (caseSense && v.caseSense) {
			return value.compareTo(v.value) < 0;
		} else {
			return value.compareToIgnoreCase(v.value) < 0;
		}
	}

	/*
	 * @see java.lang.Object#hashCode
	 */
	@Override
	public int hashCode() {

		return value.hashCode();
	}

	/**
	 * Test whether string uses case-sensitive comparison methods.
	 * 
	 * @return true if case-sensitive comparison methods are used, false
	 *         otherwise.
	 */
	boolean caseSensitive() {

		return caseSense;
	}

	/**
	 * Enable/disable case-sensitivity.
	 * 
	 * @param newCS
	 *            If true, case-sensitive comparison methods will be used when
	 *            comparing this string, if false, string is case-insensitive.
	 */
	void setCaseSensitivity(boolean newCS) {

		caseSense = newCS;
	}

	/*
	 * @see
	 * cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {

		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException(
					"Node does not contain string data. Node name is \""
							+ node.getNodeName() + "\".");
		}

		value = XMLHelper.getAttributeValueByName("value", node);
		caseSense = XMLHelper.getAttributeValueByName("caseSensitive", node)
				.equals("yes");
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#
	 * exportAsElement
	 */
	@Override
	public Element exportAsElement(Document document) {

		/*
		 * Resulting node
		 * 
		 * <string value="xxxxxx" caseSensitive="<cs>"/>
		 * 
		 * where <cs> is either "yes" or "no" depending on the case sensitivity
		 * of the current string.
		 */

		Element element = document.createElement(getXMLNodeName());

		element.setAttribute("value", value);
		element.setAttribute("caseSensitive", caseSense ? "yes" : "no");

		return element;
	}

	/*
	 * @see
	 * cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 */
	@Override
	public String getXMLNodeName() {

		return "string";
	}

	/**
	 * @return String value.
	 */
	public String getValue() {

		return value;
	}

	/**
	 * @param s
	 *            New string value.
	 */
	public void setValue(String s) {

		value = s;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#getUnit()
	 */
	@Override
	public String getUnit() {

		return null;
	}

	@Override
	public ValueType getType() {

		return ValueType.STRING;
	}

	@Override
	public ValueType getElementType() {

		return getType();
	}
}
