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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.InputSource;

import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;


/**
 * This class represents group of hosts. Groups are based only on user choice, hosts in one group do not need to
 * have nothing in common, however user can create groups automatically based on host configuration.
 * One host can belong to more than one group, but every host will belong to at least one group.
 * Host Manager creates default group which contains all known hosts - this group cannot be modified by user. <br>
 * <br>
 * This class does not perform any relevance check on host names or group names - Host Manager is responsible for 
 * passing correct arguments to <code>HostGroup</code> and maintaining unique group names.
 *
 * @author Branislav Repcek
 *
 */
public class HostGroup implements Serializable, Iterable< String >, XMLSerializableInterface {
	
	private static final long	serialVersionUID	= -4539596213243256145L;

	/**
	 * Name of a default group.
	 */
	public static final String DEFAULT_GROUP_NAME = "Universe";

	/**
	 * Name of group as chosen by user. It must be unique in group database.
	 */
	private String groupName;
	
	/**
	 * List of hosts in group. This list contains only host names.
	 */
	private HashSet< String > hostList;
	
	/**
	 * User data associated with group (description...). This is only for user convenience.
	 */
	private String groupMeta;
	
	/**
	 * Description of the group.
	 */
	private String description;
	
	/**
	 * Specifies whether group is default group (that is, whether group is automatically managed by HM
	 * to contain all hosts).
	 */
	private boolean defaultGroup;
	
	/**
	 * Create empty group with given name. This does NOT check whether name is unique since group does not know about
	 * any other groups present in group database. If name of group is equal to <code>DEFAULT_GROUP_NAME</code>
	 * group is marked as default.
	 * 
	 * @param name Name of group to create.
	 * 
	 * @throws InvalidArgumentException if invalid name is passes as parameter (<code>null</code> 
	 *         or empty string).
	 */
	public HostGroup(String name) throws InvalidArgumentException {
		
		if ((name == null) || name.equals("")) {
			throw new InvalidArgumentException("Invalid group name.");
		}

		groupName = name;
		groupMeta = "";
		hostList = new HashSet< String >();
		description = "";
		
		defaultGroup = name.equals(DEFAULT_GROUP_NAME);
	}

	/**
	 * Create copy of another group.
	 * 
	 * @param hg Group to copy to this.
	 */
	public HostGroup(HostGroup hg) {
		
		groupName = hg.groupName;
		groupMeta = hg.groupMeta;
		hostList = hg.hostList;
		description = hg.description;
		defaultGroup = hg.defaultGroup;
	}
	
	/**
	 * Create new group from given input source.
	 * 
	 * @param input Input source containing XML representation of the group.
	 * 
	 * @throws InputParseException Error parsing group data.
	 */
	public HostGroup(InputSource input) throws InputParseException {
		
		hostList = new HashSet< String >();
		
		parse(input);
	}
	
	/**
	 * Parse XML data stored in given input source.
	 * 
	 * @param input Input source with group data.
	 * 
	 * @throws InputParseException If there was an error parsing data from input source. 
	 */
	private void parse(InputSource input) throws InputParseException {

		DocumentBuilder builder = null;
		
		// create document builder
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new InputParseException("DocumentBuilder : " + e.getMessage());
		}
		
		Document document = null;
		
		// create document
		try {
			document = builder.parse(input);
		} catch (Exception e) {
			throw new InputParseException("Document : " + e.getMessage());
		}
		
		// parse document
		try {
			Node hostGroupNode = XMLHelper.getSubNodeByName("hostGroup", document);
			
			groupName = XMLHelper.getSubNodeValueByName("groupName", hostGroupNode);
			defaultGroup = XMLHelper.getSubNodeValueByName("isDefault", hostGroupNode).equalsIgnoreCase("true"); 
			groupMeta = XMLHelper.getSubNodeValueByName("groupMeta", hostGroupNode);
			description = XMLHelper.getSubNodeValueByName("description", hostGroupNode);
			
			ArrayList< Node > hosts = XMLHelper.getChildNodesByName("host", XMLHelper.getSubNodeByName("hosts", hostGroupNode));
			
			// read all hosts from file
			for (Iterator< Node > it = hosts.iterator(); it.hasNext(); ) {
			
				hostList.add(XMLHelper.getNodeValue(it.next()));
			}
		} catch (Exception e) {
			throw new InputParseException(e);
		}
	}
	
	/*
	 * NOTE: this method is not supported. It will always throw UnsupportedOperationException. You
	 *       should use constructors to create and initialise new instances of the class.
	 *
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	public void parseXMLNode(Node node) throws InputParseException {
		
		throw new UnsupportedOperationException("HostGroup cannot be created from XML node."
				+ " Use constructor instead.");
	}
	
	/**
	 * Adds host to group.
	 * 
	 * @param name Name of host to add. It does not check for host existence.
	 * @return <code>true</code> if host has been added to group, <code>false</code> if host is already present
	 *         in group and so it was not added.
	 */
	public boolean addHost(String name) {
		
		return hostList.add(name);		
	}
	
	/**
	 * Add all hosts from collection to the group. Duplicate names will be added only once. This may change
	 * order of hosts in current group.
	 * 
	 * @param names Names of hosts to add to the groups.
	 * 
	 * @return Number of hosts actually added to the group. This can be less than length of the input 
	 *         collection if there were duplicities in the input collection or collision between hosts
	 *         in group and in input collection.
	 */
	public int addHosts(Collection< String > names) {
		
		if (names != null) {

			int oldLen = hostList.size();

			hostList.addAll(names);
			
			return hostList.size() - oldLen;
			
		} else {
			return 0;
		}
	}
	
	/**
	 * Add all hosts from the array to the group. Duplicate names will be added only once. This method may
	 * change order of hosts in group.
	 * 
	 * @param names Array with names of hosts to add to the group.
	 * 
	 * @return Number of hosts actually added to the group. This can be less than length of the input 
	 *         array if there were duplicities in the input collection or collision between hosts
	 *         in group and in input array.
	 */
	public int addHosts(String []names) {
		
		if (names != null) {
			
			int oldLen = hostList.size();
			
			for (String current: names) {
				addHost(current);
			}
			
			return hostList.size() - oldLen;
		} else {
			return 0;
		}
	}
	
	/**
	 * Remove host from group.
	 * 
	 * @param name Name of host to remove.
	 * 
	 * @return <code>true</code> if host has been removed from the group
	 */
	public boolean removeHost(String name) {
		
		if (hostList.contains(name)) {
			
			hostList.remove(name);
			
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Test whether group contains given host.
	 * 
	 * @param name Name of host to check.
	 * @return <code>true</code> if host has been found in group, <code>false</code> otherwise.
	 */
	public boolean containsHost(String name) {
		
		return hostList.contains(name);
	}
	
	/**
	 * Merge another group with this one. Second group is not deleted, only it's hosts are added to this group.
	 * Each host will be only once in resulting group even if it was in both groups. This may change
	 * order of hosts in current group.
	 * 
	 * @param hg Group to merge into this group.
	 * 
	 * @return Number of hosts added to the current group.
	 */
	public int addGroup(HostGroup hg) {
		
		return addHosts(hg.hostList);
	}

	/**
	 * Get list of all hosts in the group.
	 * 
	 * @return Set containing all hosts in the group.
	 */
	public Set< String > getAllHosts() {
		
		HashSet< String > result = new HashSet< String >();
		
		result.addAll(hostList);
		
		return result;
	}
	
	/**
	 * Get number of hosts in this group.
	 * 
	 * @return Number of hosts in group.
	 */
	public int getHostCount() {
		
		return hostList.size();
	}
	
	/**
	 * Remove all hosts from group. Group is not deleted.
	 */
	public void removeAllHosts() {
		
		hostList.clear();
	}
	
	/**
	 * Set meta-data associated with this group.
	 * 
	 * @param data string with meta-data. There are no limitations on string format or length.
	 */
	public void setMetadata(String data) {
		
		groupMeta = data;
	}
	
	/**
	 * Get meta-data associated with this group.
	 * 
	 * @return string with meta-data.
	 */
	public String getMetadata() {
		
		return groupMeta;
	}
	
	/**
	 * Get description of the group.
	 * 
	 * @return String with description of the group.
	 */
	public String getDescription() {
		
		return description;
	}
	
	/**
	 * Set description of the group.
	 * 
	 * @param desc String with group description. Group description is free form string which is provided 
	 *        only for user's convenience.
	 */
	public void setDescription(String desc) {
		
		description = desc;
	}
	
	/**
	 * Get name of group.
	 * 
	 * @return name of group.
	 */
	public String getName() {
		
		return groupName;
	}
	
	/**
	 * Set group name. Useful for group editing in CLI.
	 * 
	 * @param name The new name to assign.
	 */
	public void setName(String name) {
		
		groupName = name;
	}

	/**
	 * Return iterator over a set of names of hosts in this group.
	 *  
	 * @return Iterator over host names.
	 */
	public Iterator< String > iterator() {
		
		return new HostGroupIterator();
	}
	
	/**
	 * Create XML element containing data representing the group.
	 */
	public Element exportAsElement(Document document) {
		
		Element groupElement = document.createElement("hostGroup");
		
		groupElement.appendChild(XMLHelper.writeValueToElement(document, groupName, "groupName"));
		groupElement.appendChild(XMLHelper.writeValueToElement(document, isDefaultGroup(), "isDefault"));
		groupElement.appendChild(XMLHelper.writeValueToElement(document, groupMeta, "groupMeta"));
		groupElement.appendChild(XMLHelper.writeValueToElement(document, description, "description"));
		
		Element hostsElement = document.createElement("hosts");
		
		groupElement.appendChild(hostsElement);
		
		for (String host: hostList) {
			hostsElement.appendChild(XMLHelper.writeValueToElement(document, host, "host"));
		}
		
		return groupElement;
	}
	
	/**
	 * Creates textual representation of group.
	 * 
	 * @return String containing name of group and list of hosts in the group.
	 */
	@Override
	public String toString() {
		
		String result = "\"" + groupName + "\"={";
		
		for (Iterator< String > it = hostList.iterator(); it.hasNext(); ) {
				
			result += it.next() + (it.hasNext() ? ", " : "");
		}
		
		return result + "}";
	}
	
	/**
	 * Test whether group is default or not.
	 * 
	 * @return <code>true</code> if group is default, <code>false</code> otherwise.
	 */
	public boolean isDefaultGroup() {
		
		return defaultGroup;
	}
	
	/**
	 * This class is read-only iterator which can be used to iterate over set of the host names in
	 * the group.
	 *
	 * @author Branislav Repcek
	 */
	private class HostGroupIterator implements Iterator< String > {

		/**
		 * Iterator pointing to the current element.
		 */
		private Iterator< String > it;
		
		/**
		 * Create new iterator.
		 */
		public HostGroupIterator() {
			
			it = hostList.iterator();
		}
		
		/*
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			
			return it.hasNext();
		}

		/*
		 * @see java.util.Iterator#next()
		 */
		public String next() {

			return it.next();
		}

		/**
		 * This iterator is read-only, so this method always throws.
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			
			throw new UnsupportedOperationException("HostGroup iterators are read-only.");
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	public String getXMLNodeName() {
		
		return "hostGroup";
	}
}
