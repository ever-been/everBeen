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
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.common.value.ValueInteger;
import cz.cuni.mff.been.common.value.ValueString;
import cz.cuni.mff.been.hostmanager.InputParseException;



/**
 * 
 * DiskDrive acts as storage for disk/cdrom/tape drive parameters.
 * 
 * @author Branislav Repcek
 */
public class DiskDrive extends PropertyTree 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {

	private static final long	serialVersionUID	= 1557280501717112880L;

	/**
	 * Encapsulates constants for names of properties of DiskDrive object.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
		/**
		 * Name of property with size of drive.
		 */
		public static final String SIZE = "size";
		
		/**
		 * Name of property containing model name string.
		 */
		public static final String MODEL_NAME = "model";
		
		/**
		 * Name of property containing media type.
		 */
		public static final String MEDIA_TYPE = "media";
		
		/**
		 * Name of property containing name of device assigned to drive by OS.
		 */
		public static final String DEVICE_NAME = "device";
		
		/**
		 * Name of property containing number of partitions on current drive.
		 */
		public static final String PARTITIONS = "partitions";
	}

	/**
	 * Encapsulates constants for names of sub-objects.
	 *
	 * @author Branislav Repcek
	 */
	public static class Objects {
		
		/**
		 * Name of object with information about one partition on current drive.
		 */
		public static final String PARTITION = "partition";
	}
	
	/**
	 * Disk size in bytes.
	 */
	private long size;
	
	/**
	 * Model name of drive.
	 */
	private String modelName;
	
	/**
	 * Path to device associated with drive by operating system.
	 */
	private String deviceName;
	
	/**
	 * Type of media in drive (disk, cdrom etc.)
	 */
	private String mediaType;
	
	/**
	 * List of partitions on current drive.
	 */
	private ArrayList< DiskPartition > partitions;
	
	/**
	 * Create and initialise DiskDrive class with given values.
	 *  
	 * @param size Size of drive in bytes.
	 * @param model String with model name.
	 * @param media Media type description string.
	 * @param device Device name (path to device associated by OS with this drive). 
	 */
	public DiskDrive(long size, String model, String media, String device) {

		super(HostInfoInterface.Objects.DRIVE, null);
		
		this.size = size;
		this.modelName = model;
		this.mediaType = media;
		this.deviceName = device;

		partitions = new ArrayList< DiskPartition >();
		
		try {
			putProperty(Properties.SIZE, new ValueInteger(this.size, "B"));
			putProperty(Properties.MODEL_NAME, new ValueString(this.modelName));
			putProperty(Properties.MEDIA_TYPE, new ValueString(this.mediaType));
			putProperty(Properties.DEVICE_NAME, new ValueString(this.deviceName));
			putProperty(Properties.PARTITIONS, new ValueInteger(0));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add basic properties to DiskDrive.";
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element driveElement = document.createElement("diskDrive");
		
		driveElement.appendChild(XMLHelper.writeValueToElement(document, modelName, "model"));
		driveElement.appendChild(XMLHelper.writeValueToElement(document, deviceName, "deviceName"));
		driveElement.appendChild(XMLHelper.writeValueToElement(document, size, "size"));
		driveElement.appendChild(XMLHelper.writeValueToElement(document, mediaType, "mediaType"));
		
		for (DiskPartition partition: partitions) {
			driveElement.appendChild(partition.exportAsElement(document));
		}
		
		return driveElement;
	}
	
	/**
	 * Read info about disk drive from XML file node.
	 * 
	 * @param diskDriveNode <code>diskDrive</code> node from XML file.
	 * 
	 * @throws InputParseException If error occurred when parsing node data.
	 */
	public DiskDrive(Node diskDriveNode) throws InputParseException {
		
		super(HostInfoInterface.Objects.DRIVE, null);

		partitions = new ArrayList< DiskPartition >();
	
		parseXMLNode(diskDriveNode);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {

		modelName = XMLHelper.getSubNodeValueByName("model", node);
		deviceName = XMLHelper.getSubNodeValueByName("deviceName", node);
		size = Long.valueOf(XMLHelper.getSubNodeValueByName("size", node)).longValue();
		mediaType = XMLHelper.getSubNodeValueByName("mediaType", node);
		
		// Read all partitions
		ArrayList< Node > partitionNodes = XMLHelper.getChildNodesByName("diskPartition", node);

		try {
			putProperty(Properties.SIZE, new ValueInteger(size, "B"));
			putProperty(Properties.MODEL_NAME, new ValueString(modelName));
			putProperty(Properties.MEDIA_TYPE, new ValueString(mediaType));
			putProperty(Properties.DEVICE_NAME, new ValueString(deviceName));
			putProperty(Properties.PARTITIONS, new ValueInteger(partitions.size()));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add basic properties to DiskDrive.";
		}
		
		// does this drive have any partitions?
		if (partitionNodes.size() > 0) {
			
			for (Iterator< Node > it = partitionNodes.iterator(); it.hasNext(); ) {
				
				addPartition(new DiskPartition(it.next()));
			}
		}
	}
	
	/**
	 * Get drive size.
	 * 
	 * @return Size of drive in bytes. This value is usually not exactly equal to drive's physical 
	 *         capacity due to way system computes it.
	 */
	public long getSize() {
		
		return size;
	}
	
	/**
	 * Get model name of drive. This is reported by drive and is set by manufacturer.
	 * 
	 * @return Model name string.
	 */
	public String getModelName() {
		
		return modelName;
	}
	
	/**
	 * Get type of media in drive. Media type is textual description like disk, cd-rom etc.
	 * 
	 * @return Media type string.
	 */
	public String getMediaType() {
		
		return mediaType;
	}
	
	/**
	 *  Get name of device associated with drive by operating system. Format of device name differs 
	 *  across various operating systems. On UNIX-like it's like "/dev/hda", on Windows it looks like
	 *  "\\.\PHYSICALDRIVE0"
	 * 
	 * @return Device name string.
	 */
	public String getDeviceName() {
		
		return deviceName;
	}
	
	/**
	 * Get number of partitions on current drive.
	 * 
	 * @return Number of partitions on drive.
	 */
	public int getPartitionCount() {
		
		return partitions.size();
	}
	
	/**
	 * Get info about given partition.
	 * 
	 * @param index Index of partition on drive. First index is 0, last is getPartitionCount()-1. 
	 * @return Class containing info about requested partition.
	 * 
	 * @throws IndexOutOfBoundsException when index is outside array bounds.
	 */
	public DiskPartition getPartition(int index) throws IndexOutOfBoundsException {

		return partitions.get(index);
	}
	
	/**
	 * Add partition to the list of drive's partitions.
	 * 
	 * @param newPart partition to add.
	 */
	private void addPartition(DiskPartition newPart) {

		newPart.setParent(this);
		partitions.add(newPart);
		addObject(newPart);
		
		try {
			setPropertyValue(Properties.PARTITIONS, new ValueInteger(partitions.size()));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to set pratition count in DiskDrive.";
		}
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "Drive{" + modelName + ", " + mediaType + ", size=" + (size / 1073741824) + " GB}";
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "diskDrive";
	}
}
