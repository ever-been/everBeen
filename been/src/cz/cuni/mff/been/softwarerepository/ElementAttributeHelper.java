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
 * Element metadata attribute helper class.
 * 
 * @author David Majda
 */
public class ElementAttributeHelper extends AttributeHelper<Element> {
	/** Class instance (singleton pattern). */
	private static ElementAttributeHelper instance;

	/**
	 * Allocates a new <code>ElementAttributeHelper</code> object. Construcor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private ElementAttributeHelper() {
		super();
	}
	
	/**
	 * @see cz.cuni.mff.been.softwarerepository.AttributeHelper#validateInXML(org.w3c.dom.Element)
	 */
	@Override
	public String validateInXML(Element element) {
		return extractTextValueFromElement(element) == null
			? "Invalid value of element <" + element.getNodeName() + ">."
			: null;
	}
	
	/**
	 * @see cz.cuni.mff.been.softwarerepository.AttributeHelper#readValueFromElement(org.w3c.dom.Element)
	 */
	@Override
	public Element readValueFromElement(Element element) {
		return element;
	}
	
	@Override
	public Element writeValueToElement(Document document, String tagName, Element value) {
		return value;
	}

	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static ElementAttributeHelper getInstance() {
		if (instance == null) {
			 instance = new ElementAttributeHelper();
		}
		return instance;
	}
}
