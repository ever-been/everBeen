/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.softwarerepository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * String metadata attribute helper class.
 * 
 * @author David Majda
 */
public class StringAttributeHelper extends AttributeHelper<String> {
	/** Class instance (singleton pattern). */
	private static StringAttributeHelper instance;

	/**
	 * Allocates a new <code>StringAttributeHelper</code> object. Construcor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private StringAttributeHelper() {
		super();
	}
	
	/**
	 * @see cz.cuni.mff.been.softwarerepository.AttributeHelper#validateInXML(org.w3c.dom.Element)
	 */
	@Override
	public String validateInXML(Element element) {
		/* OK, I admit this is just an ugly hack. If we seem to test the package name,
		 * we match it against the constraining regular expression, otherwise
		 * we consider the element allways valid (if extractTextValueFromElement
		 * doesn't return null).
		 * 
		 * Clean solution would require defining a new metadata attribute type
		 * for package name (new class instead of String) with its helper
		 * (inherited from AttributeHelper), but that would add too much trouble 
		 * in other parts of the application.
		 * 
		 * I couldn't express enough how I hate the String class being final,
		 * because it it wasn't, the clean solution would be as easy as subclassing it.     
		 */
		String value = extractTextValueFromElement(element);
		if (value != null) {
			if (element.getNodeName().equals("name")) {
				return value.matches("^[a-z0-9]+(-[a-z0-9]+)*$")
					? null	
					: "Invalid value of element <" + element.getNodeName() + ">.";
			} else {
				return null;
			}
		} else {
			return "Invalid value of element <" + element.getNodeName() + ">.";
		}
	}
	
	/**
	 * @see cz.cuni.mff.been.softwarerepository.AttributeHelper#readValueFromElement(org.w3c.dom.Element)
	 */
	@Override
	public String readValueFromElement(Element element) {
		return extractTextValueFromElement(element);
	}
	
	@Override
	public Element writeValueToElement(Document document, String tagName, String value) {
		if (value != null) {
			Element result = document.createElement(tagName);
			result.appendChild(document.createTextNode(value));
			return result;
		} else {
			return null;
		}
	}

	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static StringAttributeHelper getInstance() {
		if (instance == null) {
			 instance = new StringAttributeHelper();
		}
		return instance;
	}
}
