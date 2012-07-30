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
 * 
 * Storage class for disk partition related informations.
 * 
 * @author Branislav Repcek
 *
 */
public class DiskPartition extends PropertyTree 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {
	
	private static final long	serialVersionUID	= -7109701721143726952L;

	/**
	 * Encapsulation of constants with names of properties of this object.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
		/**
		 * Name of property containing size of partition in bytes.
		 */
		public static final String SIZE = "size";
		
		/**
		 * Name of property containing free space on partition in bytes.
		 */
		public static final String FREE_SPACE = "freespace";
		
		/**
		 * Name of property containing filesystem name.
		 */
		public static final String FILE_SYSTEM = "filesystem";
		
		/**
		 * Name of property containing device name.
		 */
		public static final String DEVICE_NAME = "device";
		
		/**
		 * Name of property containing name of partition (path to root folder on partition).
		 */
		public static final String NAME = "name";
	}
	
	/**
	 * Partition size in bytes.
	 */
	private long size;
	
	/**
	 * Size of free space left on partition in bytes. This value will be 0 when partition manager 
	 * in host operating system does not recognise partition type (for example this will happen 
	 * for Linux partitions on computer running Windows).
	 */
	private long freeSpace;
	
	/**
	 * Name of filesystem used on partition. "unknown" for filesystem not supported by host OS. 
	 */
	private String fileSystem;
	
	/**
	 * Name of device assigned to the partition by given OS. Format of this value depends on host OS.
	 */
	private String deviceName;
	
	/**
	 * Name of partition (this is user-defined). On Windows this is e.g. "C:", for UNIX/Linux it is path 
	 * to the mount-point. 
	 */
	private String name;

	/**
	 * Create and initialise class.
	 * 
	 * @param partName name of partition.
	 * @param fs Filesystem name.
	 * @param device Device name.
	 * @param partSize Size of partition in bytes.
	 * @param free Size of free space left on partition in bytes.
	 */
	public DiskPartition(String partName, String fs, String device, long partSize, long free) {
		
		super(DiskDrive.Objects.PARTITION, null);
		
		size = partSize;
		freeSpace = free;
		fileSystem = fs;
		deviceName = device;
		name = partName;
		
		try {
			putProperty(Properties.SIZE, new ValueInteger(size, "B"));
			putProperty(Properties.FREE_SPACE, new ValueInteger(freeSpace, "B"));
			putProperty(Properties.FILE_SYSTEM, new ValueString(fileSystem));
			putProperty(Properties.DEVICE_NAME, new ValueString(deviceName));
			putProperty(Properties.NAME, new ValueString(name));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add basic properties to DiskPartition.";
		}
	}
	
	/**
	 * Read info about partition from XML file node.
	 * 
	 * @param partNode <code>diskPartition</code> node from XML file.
	 * 
	 * @throws InputParseException if error occurred while parsing node's data.
	 */
	public DiskPartition(Node partNode) throws InputParseException {
		
		super(DiskDrive.Objects.PARTITION, null);

		parseXMLNode(partNode);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {

		deviceName = XMLHelper.getSubNodeValueByName("deviceName", node);
		name = XMLHelper.getSubNodeValueByName("name", node);
		fileSystem = XMLHelper.getSubNodeValueByName("fileSystem", node);
		size = Long.valueOf(XMLHelper.getSubNodeValueByName("size", node)).longValue();
		freeSpace = Long.valueOf(XMLHelper.getSubNodeValueByName("freeSpace", node)).longValue();
		
		try {
			putProperty(Properties.SIZE, new ValueInteger(size, "B"));
			putProperty(Properties.FREE_SPACE, new ValueInteger(freeSpace, "B"));
			putProperty(Properties.FILE_SYSTEM, new ValueString(fileSystem));
			putProperty(Properties.DEVICE_NAME, new ValueString(deviceName));
			putProperty(Properties.NAME, new ValueString(name));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add basic properties to DiskPartition.";
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element partitionElement = document.createElement("diskPartition");
		
		partitionElement.appendChild(XMLHelper.writeValueToElement(document, deviceName, "deviceName"));
		partitionElement.appendChild(XMLHelper.writeValueToElement(document, name, "name"));
		partitionElement.appendChild(XMLHelper.writeValueToElement(document, fileSystem, "fileSystem"));
		partitionElement.appendChild(XMLHelper.writeValueToElement(document, size, "size"));
		partitionElement.appendChild(XMLHelper.writeValueToElement(document, freeSpace, "freeSpace"));
		
		return partitionElement;
	}
	
	/**
	 * Get size of partition.
	 * 
	 * @return Size of partition in bytes.
	 */
	public long getSize() {
		
		return size;
	}
	
	/**
	 * Get size of free space.
	 * 
	 * @return Size of free space in bytes.
	 */
	public long getFreeSpace() {
		
		return freeSpace;
	}
	
	/**
	 * Get name of filesystem used  on partition.
	 * 
	 * @return Filesystem name (e.g. "NTFS", "EXT2").
	 */
	public String getFileSystemName() {
		
		return fileSystem;
	}
	
	/**
	 * Name of device assigned by host OS.
	 * 
	 * @return Device name identification string (on Windows eg. "Disk #0, Partition #3", on Linux eg. "/dev/hda3").
	 */
	public String getDeviceName() {
		
		return deviceName;
	}
	
	/**
	 * User defined name of partition.
	 * 
	 * @return Name of partition or mount-point path.
	 */
	public String getName() {
		
		return name;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "DiskPartition{" + fileSystem + ", size=" + (size / 1073741824)
		       + " GB, free=" + (freeSpace / 1073741824) + " GB}";
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "diskPartition";
	}
}
