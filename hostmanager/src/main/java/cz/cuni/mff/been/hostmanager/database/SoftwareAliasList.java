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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.HostDatabaseException;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;


/**
 * This class stores all alias definitions.
 *
 * @author Branislav Repcek
 */
/*public */class SoftwareAliasList implements Serializable, Iterable< SoftwareAliasDefinition > {

	private static final long	serialVersionUID	= -5343077307286580092L;

	/**
	 * Name of the root node in the XML file.
	 */
	private static final String ROOT_NODE_NAME = "aliasList";
	
	/**
	 * List of alias definitions.
	 */
	private ArrayList< SoftwareAliasDefinition > aliases;
	
	/**
	 * Create empty alias definition list.
	 */
	public SoftwareAliasList() {
		
		aliases = new ArrayList< SoftwareAliasDefinition >();
	}
	
	/**
	 * Load alias definitions from given file.
	 * 
	 * @param fileName Name of the input file.
	 * 
	 * @throws FileNotFoundException If file does not exist.
	 * @throws InputParseException If there was an error parsing data.
	 */
	public SoftwareAliasList(String fileName) throws FileNotFoundException, InputParseException {

		aliases = new ArrayList< SoftwareAliasDefinition >();
		
 		File input = new File(fileName);
		
		if (!input.exists()) {
			throw new FileNotFoundException("File \"" + fileName + "\" was not found.");
		}
		
		DocumentBuilder builder = null;
		
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (Exception e) {
			throw new InputParseException("Unable to create document builder.", e);
		}
		
		Document document = null;
		
		try {
			document = builder.parse(input);
		} catch (Exception e) {
			throw new InputParseException(e);
		}
		
		Node root = XMLHelper.getSubNodeByName(ROOT_NODE_NAME, document);
		ArrayList< Node > nodes = XMLHelper.getChildNodesByName(SoftwareAliasDefinition.XML_NODE_NAME, root);
		
		for (Node n: nodes) {
			aliases.add(new SoftwareAliasDefinition(n));
		}
	}
	
	/**
	 * Save alias definitions to the file.
	 * 
	 * @param outputFileName Name of the output file.
	 * 
	 * @throws HostDatabaseException If there was an error writing data to the file.
	 */
	public void save(String outputFileName) throws HostDatabaseException {
		
		Document document = null;
		
		try {
			document = XMLHelper.createDocument();
		} catch (Exception e) {
			throw new HostDatabaseException("Error creating new XML document.", e);
		}
		
		Node root = document.createElement(ROOT_NODE_NAME);
		
		for (SoftwareAliasDefinition s: aliases) {
			root.appendChild(s.exportAsElement(document));
		}
		
		document.appendChild(root);
		
		try {
			XMLHelper.saveDocument(document, outputFileName, true, "UTF-8");
		} catch (Exception e) {
			throw new HostDatabaseException("Unable to save alias definitions.", e);
		}
	}
	
	/**
	 * Add new definition to the list.
	 * 
	 * @param alias New alias definition.
	 */
	public void add(SoftwareAliasDefinition alias) {
		
		aliases.add(alias);
	}
	
	/**
	 * Get number of alias definitions currently in list.
	 * 
	 * @return Number of alias definitions.
	 */
	public int getAliasCount() {
		
		return aliases.size();
	}
	
	/**
	 * Get definition at given position.
	 * 
	 * @param index Index of the definition.
	 * @return Requested alias definition.
	 * 
	 * @throws ValueNotFoundException If index is out of bounds.
	 */
	SoftwareAliasDefinition get(int index) throws ValueNotFoundException {
		
		try {
			return aliases.get(index);
		} catch (IndexOutOfBoundsException e) {
			throw new ValueNotFoundException("Index out of bounds.", e);
		}
	}
	
	/**
	 * Remove alias at given position.
	 * 
	 * @param index Index of alias to remove.
	 * @return The removed software alias definition instance.
	 * @throws ValueNotFoundException If index is out of bounds.
	 */
	public SoftwareAliasDefinition remove(int index) throws ValueNotFoundException {
		
		try {
			return aliases.remove(index);
		} catch (IndexOutOfBoundsException e) {
			throw new ValueNotFoundException("Index is out of bounds.", e);
		}
	}
	
	/**
	 * Remove all alias definitions.
	 */
	public void removeAll() {
		
		aliases.clear();
	}
	
	/**
	 * Get iterator over the set of alias definitions.
	 * 
	 * @return Iterator over the set of alias definitions.
	 */
	public Iterator< SoftwareAliasDefinition > iterator() {
		
		return aliases.iterator();
	}
	
	/**
	 * Added by Andrej Podzimek as a temporary fix to issue 314406.
	 * 
	 * @return An array with the current snapshot of alias definitions.
	 */
	public SoftwareAliasDefinition[] toArray() {
		return aliases.toArray( new SoftwareAliasDefinition[ aliases.size() ] );
	} 
}
