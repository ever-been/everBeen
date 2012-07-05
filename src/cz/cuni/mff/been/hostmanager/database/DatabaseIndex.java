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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.HostDatabaseException;
import cz.cuni.mff.been.hostmanager.HostManagerLogger;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.util.XMLHelper;

/**
 * This class represents index of the database. It stores list of all files in the database and relations between
 * various types of files and relations between hosts and files. All operations with the index are synchronised with
 * the index file on the disk so they may be slower.
 *
 * @author Branislav Repcek
 */
class DatabaseIndex implements Serializable, XMLSerializableInterface, ModifiableInterface {

	private static final long	serialVersionUID	= 1500976509734402886L;

	/**
	 * Name of the XML file node.
	 */
	public static final String XML_NODE_NAME = "index";
	
	/**
	 * Logger.
	 */
	private HostManagerLogger logger;
	
	/**
	 * Name of the index file.
	 */
	private String indexFile;
	
	/**
	 * Maps host name to the entry in index.
	 */
	private HashMap< String, HostIndexEntry > hosts;
	
	/**
	 * Maps group name to the group file.
	 */
	private HashMap< String, String > groups;
	
	/**
	 * Was instance of the class modified?
	 */
	private boolean modified;
	
	/**
	 * Load existing index file.
	 * 
	 * @param fileName Name of the file to load. This file must exist (that is, no empty file will be created).
	 * @param logger Logger which will be used to output messages.
	 * 
	 * @throws HostDatabaseException If database error occurred.
	 * @throws InputParseException Error parsing index file.
	 */
	public DatabaseIndex(String fileName, HostManagerLogger logger) throws HostDatabaseException, InputParseException {
		
		hosts = new HashMap< String, HostIndexEntry >();
				
		groups = new HashMap< String, String >();
		
		this.logger = logger;

		File input = new File(fileName);

		this.logger.logDebug("Index file: \"" + fileName + "\".");

		if (!input.exists()) {
			this.logger.logWarning("Index does not exist. Creating empty one.");
			
			modified = true;
			
			indexFile = fileName;
			
			try {
				save();
			} catch (HostDatabaseException e) {
				throw new HostDatabaseException("Unable to create new index.", e);
			}
		}
		this.logger.logInfo("Loading database index, file: \"" + fileName + "\".");
				
		DocumentBuilder builder = null;

		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new InputParseException(e.getMessage());
		}
		
		Document document = null;
		
		try {
			document = builder.parse(input);
		} catch (Exception e) {
			throw new InputParseException(e.getMessage());
		}
		
		Node indexNode = XMLHelper.getSubNodeByName("index", document);
		
		ArrayList< Node > hostNodes = 
			XMLHelper.getChildNodesByName("host", XMLHelper.getSubNodeByName("hosts", indexNode));

		// load entries for hosts
		HostIndexEntry	hie;
		
		for ( Node node : hostNodes ) {
			hie = new HostIndexEntry( node );
			hosts.put(hie.getHostName(), hie);
		}
		
		ArrayList< Node > groupNodes = 
			XMLHelper.getChildNodesByName("group", XMLHelper.getSubNodeByName("groups", indexNode));
		
		// load entries for groups
		GroupIndexEntry	gie;
		
		for ( Node node : groupNodes ) {			
			gie = new GroupIndexEntry( node );			
			groups.put(gie.getGroupName(), gie.getDataFileName());
		}
		
		this.logger.logInfo("Index file loaded successfuly. Hosts: " + hosts.size() + ", "
				+ "groups: " + groups.size() + ".");
		
		indexFile = fileName;
	}

	/*
	 * NOTE: Index cannot be created from the XML node. This method always throws exception. You
	 *       should always use constructor to create new index.
	 *
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	public void parseXMLNode(Node node) throws InputParseException {
		
		throw new UnsupportedOperationException("Index cannot be create from XML node."
				+ " Use constructor instead.");
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.ModifiableInterface#isModified()
	 */
	public boolean isModified() {
		
		if (!modified) {
			// if the index itself is not modified look whether some of the child nodes are modified
			for (HostIndexEntry hostEntry: hosts.values()) {
				if (hostEntry.isModified()) {
					return true;
				}
			}
		}

		return modified;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.ModifiableInterface#isModified(boolean)
	 */
	public boolean isModified(boolean reset) {
		
		boolean result = isModified();
		
		if (result && reset) {
			for (HostIndexEntry hostEntry: hosts.values()) {
				hostEntry.saved();
			}
		}
		
		return result;
	}
	
	/**
	 * Get name of the index file.
	 * 
	 * @return Name of the index file.
	 */
	public String getIndexFile() {
		
		return indexFile;
	}
	
	/**
	 * Save index before destroying object.
	 */
	@Override
	public void finalize() throws Throwable {
		
		try {
			if (indexFile != null) {
				modified = true;
				save();
			}
		} finally {
			super.finalize();
		}
	}
	
	/**
	 * Save index into the file. Index is saved into the same file from which it was loaded. 
	 * File is overwritten. If the data in index was not modified since last save, nothing is
	 * written to the disk.
	 * 
	 * @throws HostDatabaseException If an error occurred when writing data to the disk.
	 * @throws NullPointerException If name of index file is <code>null</code>
	 */
	public void save() throws HostDatabaseException {

		if (!isModified()) {
			return;
		}
		
		if (indexFile == null) {
			logger.logFatal("Name of index file is null!");
			throw new NullPointerException("Name of index file is null!");
		}

		try {
			XMLHelper.saveXMLSerializable(this, indexFile, true, "UTF-16");
		} catch (Exception e) {
			logger.logError("Unable to save index file.", e);
			throw new HostDatabaseException("Unable to save index file.", e);
		}
		
		modified = false;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(Document)
	 */
	public Element exportAsElement(Document document) {
		
		Element indexElement = document.createElement("index");
		
		Element hostsElement = document.createElement("hosts");
		Element groupsElement = document.createElement("groups");
		
		indexElement.appendChild(hostsElement);
		indexElement.appendChild(groupsElement);
		
		for (HostIndexEntry hostEntry: hosts.values()) {
			hostsElement.appendChild(hostEntry.exportAsElement(document));
		}
		
		for (Map.Entry< String, String > groupEntry: groups.entrySet()) {
			GroupIndexEntry gie = new GroupIndexEntry(groupEntry.getKey(), groupEntry.getValue());
			groupsElement.appendChild(gie.exportAsElement(document));
		}
		
		return indexElement;
	}
	
	/**
	 * Test if an entry for given host exists.
	 * 
	 * @param hostName Name of the host.
	 * 
	 * @return <tt>true</tt> is entry for given host is present in the index, <tt>false</tt> otherwise.
	 */
	public boolean hasHostEntry(String hostName) {
		
		return (hosts.get(hostName) != null); 
	}
	
	/**
	 * Get entry of host with specified name.
	 * 
	 * @param hostName Name of the host.
	 * 
	 * @return Entry of the given host.
	 * 
	 * @throws ValueNotFoundException If host has not been found in index.
	 */
	public HostIndexEntry getHostEntry(String hostName) throws ValueNotFoundException {
		
		HostIndexEntry result = hosts.get(hostName);
		
		if (result != null) {
			return result;
		} else {
			throw new ValueNotFoundException("Unable to find host \"" + hostName + "\" in index.");
		}
	}
	
	/**
	 * Remove entry of the specified host. You should save index after calling this method.
	 * 
	 * @param hostName Name of the host to remove from index.
	 * 
	 * @throws ValueNotFoundException If host to remove was not found in index.
	 */
	public void removeHostEntry(String hostName) throws ValueNotFoundException {
		
		if (hosts.containsKey(hostName)) {
			hosts.remove(hostName);
			
			modified = true;
		} else {
			throw new ValueNotFoundException("Unable to remove entry for \""
					+ hostName + "\" from index.");
		}
	}
	
	/**
	 * Get list of all host entries in index.
	 *  
	 * @return Array containing entries in the index. You should not rely on the order of the entries 
	 *         to remain same between calls (especially after insert/delete operations).
	 */
	public HostIndexEntry[] getHostEntryList() {
				
		return hosts.values().toArray(new HostIndexEntry[0]);
	}
	
	/**
	 * Add new entry for the host. You should save index after calling this method.
	 * 
	 * @param hostName Name of the host.
	 * @param dataFile File with host's data.
	 * @param loadFile File with load info for the host.
	 * @param loadMapFile Name of the load map file.
	 * @param date Date when the entry was made.
	 * 
	 * @throws InvalidArgumentException If host is already in the index.
	 */
	public void addHostEntry(String hostName, String dataFile, String loadFile, String loadMapFile, Date date) 
		throws InvalidArgumentException {

		if (hosts.containsKey(hostName)) {
			throw new InvalidArgumentException("Unable to add \"" + hostName + "\" to the index. "
					+ "Host is already in the index.");
		}
		
		HostIndexEntry ie = new HostIndexEntry(hostName, dataFile, loadFile, loadMapFile, date);
		
		hosts.put(hostName, ie);
		
		modified = true;
	}
	
	/**
	 * Test if entry for given group is present in the index.
	 * 
	 * @param groupName Name of the group.
	 * 
	 * @return <tt>true</tt> if entry for given group is present in the index, <tt>false</tt> otherwise.
	 */
	public boolean hasGroupEntry(String groupName) {
		
		return (groups.get(groupName) != null);
	}
	
	/**
	 * Get entry of the given group.
	 * 
	 * @param groupName Name of the group.
	 *  
	 * @return Entry of the requested group.
	 * 
	 * @throws ValueNotFoundException If entry for the group has not been found.
	 */
	public GroupIndexEntry getGroupEntry(String groupName) throws ValueNotFoundException {

		String f = groups.get(groupName);
		
		if (f != null) {
			return new GroupIndexEntry(groupName, f);
		} else {
			throw new ValueNotFoundException("Group \"" + groupName + "\" was not found in index.");
		}
	}
	
	/**
	 * Add entry for new group. You should save index after performing this operation.
	 * 
	 * @param groupName Name of the group.
	 * @param groupFile File with group's data.
	 * 
	 * @throws InvalidArgumentException If group with specified name is already in the index.
	 */
	public void addGroupEntry(String groupName, String groupFile) throws InvalidArgumentException {
		
		if (groups.containsKey(groupName)) {
			throw new InvalidArgumentException("Group \"" + groupName + "\" is already in the index.");
		} else {
			groups.put(groupName, groupFile);
			modified = true;
		}
	}
	
	/**
	 * Get list of all group entries in the index.
	 * 
	 * @return Array containing all group entries in the index.
	 */
	public GroupIndexEntry[] getGroupEntryList() {
		
		GroupIndexEntry[] grp = new GroupIndexEntry[groups.size()];
		Set< Map.Entry< String, String> > entries = groups.entrySet();
		
		int i = 0;
		for ( Map.Entry< String, String > current : entries ) {
			grp[i] = new GroupIndexEntry(current.getKey(), current.getValue());
			++i;
		}
		
		return grp;
	}
	
	/**
	 * Remove entry of the given group. Synchronises data with the disk.
	 * 
	 * @param groupName Name of the group.
	 * 
	 *  @throws ValueNotFoundException If specified group was not found index.
	 */
	public void removeGroupEntry(String groupName) throws ValueNotFoundException {
		
		if (groups.containsKey(groupName)) {
			
			groups.remove(groupName);
			
			modified = true;
		} else {
			throw new ValueNotFoundException("Unable to remove non-existant group entry for \""
					+ groupName + "\" from index.");
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	public String getXMLNodeName() {
		
		return XML_NODE_NAME;
	}
}
