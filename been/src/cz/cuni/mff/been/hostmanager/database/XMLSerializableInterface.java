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

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.InputParseException;

/**
 * Interface which adds support for XML export.
 *
 * @author Branislav Repcek
 */
public interface XMLSerializableInterface {

	/**
	 * Export data as XML element node.
	 * 
	 * @param document Document into which data should be exported.
	 * 
	 * @return Element node which contains data from the class implementing this interface.
	 */
	Element exportAsElement(Document document);
	
	/**
	 * Read data stored in XML file node.
	 * 
	 * @param node Node containing serialised form of the class.
	 * 
	 * @throws InputParseException If there was an error while parsing input node.
	 */
	void parseXMLNode(Node node) throws InputParseException;
	
	/**
	 * Get name of the node to which object implementing this interface is serialised.
	 * 
	 * @return Name of the node to which object is serialised.
	 */
	String getXMLNodeName();
}
