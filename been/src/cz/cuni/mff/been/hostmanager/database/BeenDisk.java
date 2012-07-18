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

import cz.cuni.mff.been.hostmanager.InputParseException;

import cz.cuni.mff.been.hostmanager.util.XMLHelper;

import cz.cuni.mff.been.hostmanager.value.ValueInteger;
import cz.cuni.mff.been.hostmanager.value.ValueString;

/**
 * This class stores data about the drive BEEN is installed on.
 *
 * @author Branislav Repcek
 */
public class BeenDisk extends PropertyTree 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {
	
	private static final long	serialVersionUID	= -8643398660971930499L;

	/**
	 * Size of the disk.
	 */
	private long diskSize;
	
	/**
	 * Free space on the drive.
	 */
	private long diskFree;
	
	/**
	 * Path to root folder of BEEN installation.
	 */
	private String beenHome;
	
	/**
	 * Contains constants with names of object's properties. 
	 */
	public static class Properties {
		
		/**
		 * Path to the BEEN installation directory.
		 */
		public static final String BEEN_HOME = "beenhome";
		
		/**
		 * Size of the drive BEEN is installed on in bytes.
		 */
		public static final String DISK_SIZE = "size";
		
		/**
		 * Free space on the drive BEEN is installed on in bytes.
		 */
		public static final String DISK_FREE = "freespace";
	}
	
	/**
	 * Create BeenDisk object.
	 * 
	 * @param homePath Path to installation of BEEN.
	 * @param size Size of the drive with BEEN.
	 * @param free Free space on the drive with BEEN.
	 */
	public BeenDisk(String homePath, long size, long free) {
		
		super(HostInfoInterface.Objects.BEEN_DISK, null);
		
		beenHome = homePath;
		diskSize = size;
		diskFree = free;
		
		try {
			putProperty(Properties.BEEN_HOME, new ValueString(beenHome));
			putProperty(Properties.DISK_SIZE, new ValueInteger(diskSize, "B"));
			putProperty(Properties.DISK_FREE, new ValueInteger(diskFree, "B"));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to BeenDisk object.";
		}
	}
	
	/**
	 * Create and initialise BeenDisk object from XML node.
	 * 
	 * @param bdNode Node with BeenDisk data.  
	 * 
	 * @throws InputParseException If error occurred when parsing XML file. 
	 */
	public BeenDisk(Node bdNode) throws InputParseException {
		
		super(HostInfoInterface.Objects.BEEN_DISK, null);
		
		parseXMLNode(bdNode);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {
		
		beenHome = XMLHelper.getSubNodeValueByName("path", node);
		diskSize = Long.valueOf(XMLHelper.getSubNodeValueByName("size", node)).longValue();
		diskFree = Long.valueOf(XMLHelper.getSubNodeValueByName("freeSpace", node)).longValue();

		try {
			putProperty(Properties.BEEN_HOME, new ValueString(beenHome));
			putProperty(Properties.DISK_SIZE, new ValueInteger(diskSize, "B"));
			putProperty(Properties.DISK_FREE, new ValueInteger(diskFree, "B"));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to BeenDisk object.";
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element beenDiskElement = document.createElement("beenDisk");
		
		beenDiskElement.appendChild(XMLHelper.writeValueToElement(document, beenHome, "path"));
		beenDiskElement.appendChild(XMLHelper.writeValueToElement(document, diskSize, "size"));
		beenDiskElement.appendChild(XMLHelper.writeValueToElement(document, diskFree, "freeSpace"));
		
		return beenDiskElement;
	}
	
	/**
	 * Get path to the root folder of BEEN installation.
	 * 
	 * @return BEEN home string.
	 */
	public String getBeenHomePath() {
		
		return beenHome;
	}
	
	/**
	 * Get size of the drive BEEN is installed on. This size may be lower than the actual size of the drive
	 * due to various user quotas set for user which ran detector.
	 *  
	 * @return Size of the disk in bytes.
	 */
	public long getDiskSize() {
		
		return diskSize;
	}
	
	/**
	 * Get free space on the drive with BEEN installation. This may be lower than the actual free space 
	 * on the drive due to various user quotas that may be set for the user which ran detector.
	 *  
	 * @return Free space on the drive in bytes.
	 */
	public long getDiskFree() {
		
		return diskFree;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "BeenDisk{" + beenHome + ", free=" + (diskFree / 1073741824) + " GB}";
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "beenDisk";
	}
}
