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

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ArrayList metadata attribute helper class.
 *
 * @author David Majda
 */
public class ArrayListAttributeHelper extends AttributeHelper<ArrayList< ? > > {
	/** Class instance (singleton pattern). */
	private static ArrayListAttributeHelper instance;

	/**
	 * Allocates a new <code>ArrayListAttributeHelper</code> object. Construcor is
	 * private so only instance in <code>instance</code> field can be constructed
	 * (singleton pattern).
	 */
	private ArrayListAttributeHelper() {
		super();
	}

	/**
	 * 
	 * 
	 * XML element representation of the ArrayList attribute is valid
	 * iff following conditions are all met:
	 * 
	 * <ol>
	 *   <li>element name is in plural (ends with "s")</li>
	 *   <li>if element contains some child nodes, those of them, which are also
	 *   elements, have name of the base element in singular (without trailing
	 *   "s") and contain only text</li>
	 * </ol>
	 * 
	 * @see cz.cuni.mff.been.softwarerepository.AttributeHelper#validateInXML(org.w3c.dom.Element)
	 */
	@Override
	public String validateInXML(Element element) {
		/* Verify element name. */
		String elementName = element.getNodeName();
		if (elementName.charAt(elementName.length() - 1) != 's') {
			return "Invalid element name: <" + elementName + ">.";
		}
		
		/* Go through the list of children. */
		NodeList children = element.getChildNodes();
		String childName = elementName.substring(0, elementName.length() - 1);
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				
				/* If the child is element, verify its name and content. */
				Element childElement = (Element) children.item(i);
				if (!childElement.getNodeName().equals(childName)) {
					return "Invalid element name: <" + childElement.getNodeName() + ">.";
				}
				if (extractTextValueFromElement(childElement) == null) {
					return "Invalid value of element <" + elementName + ">.";
				}
			}
		}
		
		/* If all tests passed OK, return true. */
		return null;
	}

	/**
	 * @see cz.cuni.mff.been.softwarerepository.AttributeHelper#readValueFromElement(org.w3c.dom.Element)
	 */
	@Override
	public ArrayList< ? > readValueFromElement(Element element) {
		ArrayList< String > result = new ArrayList< String >();
		
		/* Go through the list of children and extract textual information
		 * from the subelements. 
		 */
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				result.add(extractTextValueFromElement((Element) children.item(i)));
			}
		}
		
		return result;
	}

	@Override
	public Element writeValueToElement(Document document, String tagName, ArrayList< ? > value) {
		assert tagName.charAt(tagName.length()) == 's': "Element name is not in plural";
		
		if (value != null) {
			Element result = document.createElement(tagName);
			String innerTagName = tagName.substring(0, tagName.length() - 1);
			for (Object item: value) {
				Element innerElement = document.createElement(innerTagName);
			  innerElement.appendChild(document.createTextNode(item.toString()));
			  result.appendChild(innerElement);
			}
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
	public static ArrayListAttributeHelper getInstance() {
		if (instance == null) {
			 instance = new ArrayListAttributeHelper();
		}
		return instance;
	}
}
