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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Base class for "attribute class helpers". These helpers contain methods,
 * that should be included in classes of the attributes, but it wasn't
 * possible, because many of these classes are declared final.   
 * 
 * @author David Majda
 */
public abstract class AttributeHelper<T> {
	/**
	 * Validates attrbitue value in the XML element.
	 * 
	 * @param element XML element with (possible) attribute value
	 * @return <code>null</code> if element contains valid value of given
	 *          attribute type;
	 *          <code>String</code> with human-readable error message  otherwise
	 */
	public abstract String validateInXML(Element element);
	
	/**
	 * Extracts the attribute value form the XML element. It assumes the element
	 * contains valid value and exact behavior in case it doesn't is undefined.
	 * 
	 * @param element XML element
	 * @return extracted attribute value; the caller should cast
	 *          it to the appropriate type
	 */
	public abstract T readValueFromElement(Element element);
	
	/**
	 * Writes the attribute value to the XML element.
	 * 
	 * @param document document where the resulting element belongs
	 * @param tagName name of the element's tag
	 * @param value value to wrtie, can be <code>null</code>
	 * @return Element with written value
	 */
	public abstract Element writeValueToElement(Document document, String tagName, T value);
		
	/**
	 * Extracts text value form the XML element.
	 * 
	 * @param element XML element
	 * @return element's text value if the element contains exactly one child that
	 *          is a text node; <code>null</code> otherwise
	 */
	protected String extractTextValueFromElement(Element element) {
		NodeList children = element.getChildNodes();
		if (children.getLength() == 0) {
			return "";   
		}
		if (children.getLength() > 1) {
			return null;   
		}
		Node textContent = children.item(0);
		if (textContent.getNodeType() != Element.TEXT_NODE) {
			return null;   
		}
		return textContent.getNodeValue();
	}
}
