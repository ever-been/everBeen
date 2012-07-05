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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.InputParseException;

import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;

import cz.cuni.mff.been.hostmanager.util.XMLHelper;

/**
 * This class represents regular expression.
 *
 * @author Branislav Repcek
 *
 */
public class ValueRegexp 
	implements ValueBasicInterface< ValueRegexp >, Serializable, XMLSerializableInterface {
	
	private static final long	serialVersionUID	= 1515080941314428462L;

	/**
	 * Regular expression string.
	 */
	private String value;
	
	/**
	 * Case sensitivity flag.
	 */
	private boolean caseSensitive;
	
	/**
	 * Pattern.
	 */
	private Pattern pattern;
	
	/**
	 * Allocate empty <code>ValueRegexp</code> value.
	 */
	public ValueRegexp() {
		
		value = "";
		caseSensitive = false;
		pattern = null;
	}
	
	/**
	 * Allocate new <code>ValueRegexp</code> value.
	 * 
	 * @param newValue Regular expression string. For more info on format @see 
	 *        <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html">java.util.regex.Pattern</a>
	 * @param caseSense If <code>true</code> regular expression will be case sensitive.
	 * @throws PatternSyntaxException If syntax of <code>newValue</code> is invalid.
	 */
	public ValueRegexp(String newValue, boolean caseSense) throws PatternSyntaxException {
	
		caseSensitive = caseSense;
		value = newValue;
		
		if (caseSensitive) {
			pattern = Pattern.compile(newValue);
		} else {
			pattern = Pattern.compile(newValue, Pattern.CASE_INSENSITIVE);
		}
	}
	
	/**
	 * Read data from the XML file node.
	 * 
	 * @param node Node with data to read.
	 * 
	 * @throws InputParseException If there was an error while parsing input.
	 */
	public ValueRegexp(Node node) throws InputParseException {
		
		parseXMLNode(node);
	}
	
	/**
	 * Allocate new <code>ValueRegexp</code> value. This method is same as <code>ValueRegexp(String, boolean)</code>, 
	 * except it always creates case insensitive regular expression.
	 * 
	 * @param newValue Regular expression string.
	 * @throws PatternSyntaxException If syntax of <code>newValue</code> is invalid.
	 */
	public ValueRegexp(String newValue) throws PatternSyntaxException {
	
		caseSensitive = false;
		value = newValue;
		
		pattern = Pattern.compile(newValue, Pattern.CASE_INSENSITIVE);
	}
	
	/**
	 * Alloate a new {@code ValueRegexp} with the supplied pattern.
	 * 
	 * @param pattern The pattern this regexp will match.
	 */
	public ValueRegexp(Pattern pattern) {
		this.caseSensitive = 0 == ( pattern.flags() & Pattern.CASE_INSENSITIVE );
		this.value = pattern.toString();
		this.pattern = pattern;
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
	public int compareTo(ValueRegexp vb) {
	
		return value.compareTo(vb.value);
	}

	/*
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof ValueRegexp) {
			return equals((ValueRegexp) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Test specified <code>ValueRegexp</code> and this value for equality.
	 * 
	 * @param vc Value to compare this to.
	 * @return <code>true</code> if values are equal, <code>false</code> otherwise.
	 */
	public boolean equals(ValueRegexp vc) {
		
		return value.equals(vc.value);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#greaterThan
	 */
	public boolean greaterThan(Object o) {
		
		if (o instanceof ValueRegexp) {
			return greaterThan((ValueRegexp) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Test if current value is greater than specified one.
	 * 
	 * @param v Value to compare to.
	 * 
	 * @return <code>true</code> if this instance has greater value than given value, 
	 *         <code>false</code> otherwise.
	 */
	public boolean greaterThan(ValueRegexp v) {
		
		return value.compareTo(v.value) > 0;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#lessThan
	 */
	public boolean lessThan(Object o) {
		
		if (o instanceof ValueRegexp) {
			return lessThan((ValueRegexp) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Test if current value is less than specified one.
	 * 
	 * @param v Value to compare to.
	 * 
	 * @return <code>true</code> if this instance has lower value than given value, 
	 *         <code>false</code> otherwise.
	 */
	public boolean lessThan(ValueRegexp v) {
		
		return value.compareTo(v.value) < 0;
	}

	/*
	 * @see java.lang.Object#hashCode
	 */
	@Override
	public int hashCode() {
		
		return value.hashCode();
	}
	
	/**
	 * Test given string against current regular expression.
	 * 
	 * @param str String to test.
	 * @return <code>true</code> if string matches current regular expression, <code>false</code> otherwise.
	 */
	public boolean match(String str) {
		
		Matcher matcher = pattern.matcher(str);
		
		return matcher.matches();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 */
	public void parseXMLNode(Node node) throws InputParseException {

		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException("Node does not contain regexp data. Node name is \""
					+ node.getNodeName() + "\".");
		}

		value = XMLHelper.getAttributeValueByName("value", node);
		caseSensitive = XMLHelper.getAttributeValueByName("caseSense", node).equals("yes");
		
		try {
			if (caseSensitive) {
				pattern = Pattern.compile(value);
			} else {
				pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
			}
		} catch (Exception e) {
			throw new InputParseException("Regular expression is invalid.", e);
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement
	 */
	public Element exportAsElement(Document document) {
		
		/* Resulting node
		 * 
		 * <regexp caseSense="<case-sense>" value="xxxxx"/>
		 * 
		 * where <case-sense> is either "yes" or "no" depending on the case sensitivity of the regexp.
		 */
		
		Element element = document.createElement(getXMLNodeName());
		
		element.setAttribute("value", value);
		element.setAttribute("caseSense", caseSensitive ? "yes" : "no");
		
		return element;
	}

	/**
	 * Test if regular expression is case sensitive.
	 * 
	 * @return <code>true</code> if regular expression if case sensitive, <code>false</code> otherwise.
	 */
	public boolean isCaseSensitive() {

		return caseSensitive;
	}

	/**
	 * Set case sensitivity for the regular expression.
	 * 
	 * @param caseSensitive If set to <code>true</code> expression will be case sensitive, otherwise
	 *        is is case insensitive.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		
		try {
			if (caseSensitive) {
				pattern = Pattern.compile(value);
			} else {
				pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
			}
		} catch (Exception e) {
			// this should never happen since we already compiled given expression before
			assert false : "Invalid regexp.";
		}
		
		this.caseSensitive = caseSensitive;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 */
	public String getXMLNodeName() {
		
		return "regexp";
	}
	
	/**
	 * @return Regular expression string.
	 */
	public String getValue() {
		
		return value;
	}
	
	/**
	 * Pattern getter.
	 * 
	 * @return Regular expression pattern.
	 */
	public Pattern getPattern() {
		return pattern;
	}
	
	/**
	 * @param regexp New regular expression.
	 * 
	 * @throws PatternSyntaxException If given regular expression is invalid.
	 */
	public void setValue(String regexp) throws PatternSyntaxException {
		
		value = regexp;
		
		if (caseSensitive) {
			pattern = Pattern.compile(value);
		} else {
			pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#getUnit()
	 */
	public String getUnit() {
		
		return null;
	}

	@Override
	public ValueType getType() {
		
		return ValueType.REGEXP;
	}

	@Override
	public ValueType getElementType() {
		
		return getType();
	}
}
