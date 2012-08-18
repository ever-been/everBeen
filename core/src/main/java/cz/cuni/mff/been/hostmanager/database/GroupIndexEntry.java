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

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.InputParseException;


/**
 * One entry in the list of groups in Database Index.
 *
 * @author Branislav Repcek
 */
public class GroupIndexEntry implements Serializable, XMLSerializableInterface {

	private static final long	serialVersionUID	= -269950123098378954L;

	/**
	 * Name of the group.
	 */
	private String groupName;
	
	/**
	 * Name of the file with group data.
	 */
	private String groupFile;
	
	/**
	 * Create new GroupIndexEntry object.
	 * 
	 * @param name Name of the group.
	 * @param file name of the file with group's data.
	 */
	public GroupIndexEntry(String name, String file) {
		
		groupName = name;
		groupFile = file;
	}
	
	/**
	 * Read data from XML node.
	 * 
	 * @param node Node to read from.
	 * 
	 * @throws InputParseException Error parsing node data.
	 */
	public GroupIndexEntry(Node node) throws InputParseException {

		parseXMLNode(node);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	public void parseXMLNode(Node node) throws InputParseException {

		/* One group index node looks like this:

		   <group name="Universe" file=""/>
		 */

		groupName = XMLHelper.getAttributeValueByName("name", node);
		groupFile = XMLHelper.getAttributeValueByName("file", node);
	}
	
	/**
	 * Get name of the group.
	 * 
	 * @return Name of the group.
	 */
	public String getGroupName() {
		
		return groupName;
	}
	
	/**
	 * Get name of the file with group data.
	 * 
	 * @return Name of the group's data file.
	 */
	public String getDataFileName() {
		
		return groupFile;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	public Element exportAsElement(Document document) {
		
		Element entry = document.createElement("group");
		
		entry.setAttribute("name", groupName);
		entry.setAttribute("file", groupFile);
		
		return entry;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "\"" + groupName + "\"=" + groupFile;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	public String getXMLNodeName() {
		
		return "group";
	}
}
