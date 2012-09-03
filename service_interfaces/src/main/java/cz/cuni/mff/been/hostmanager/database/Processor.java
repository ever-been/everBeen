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
import cz.cuni.mff.been.common.value.ValueInteger;
import cz.cuni.mff.been.common.value.ValueString;
import cz.cuni.mff.been.hostmanager.InputParseException;



/**
 * 
 * Class which stores basic info about each processor in system.
 *
 * @author Branislav Repcek
 *
 */
public class Processor extends PropertyTree 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {
	
	private static final long	serialVersionUID	= -5989106767526846103L;

	/**
	 * Encapsulation of names of properties of Processor object. 
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
		/**
		 * Processor model name.
		 */
		public static final String MODEL_NAME = "model";
		
		/**
		 * Vendor.
		 */
		public static final String VENDOR_NAME = "vendor";
		
		/**
		 * Speed in Hz.
		 */
		public static final String SPEED = "speed";
		
		/**
		 * Size of L2 cache in B.
		 */
		public static final String CACHE_SIZE = "cache";
	}
	
	/**
	 * Model name of CPU (set by vendor)
	 */
	private String modelName;
	
	/**
	 * Unique identification of CPU vendor, it's always 12 characters long (AuthenticAMD, GenuineIntel...)
	 */
	private String vendorName;
	
	/**
	 * Speed of CPU in MHz as measured by OS.
	 */
	private long speed;
	
	/**
	 * Size of internal L2 cache in KB.
	 */
	private long cacheSize;
	
	/**
	 * Constructor with data initialisation
	 * 
	 * @param model Model name.
	 * @param vendor Name of vendor.
	 * @param speed Speed of cpu in MHz.
	 * @param cache Size of cache in KB.
	 */
	public Processor(String model, String vendor, int speed, long cache) {

		super(HostInfoInterface.Objects.PROCESSOR, null);
		
		this.modelName = model;
		this.vendorName = vendor;
		this.speed = speed * 1000000;
		this.cacheSize = cache * 1024;
		
		try {
			putProperty(Properties.MODEL_NAME, new ValueString(this.modelName));
			putProperty(Properties.VENDOR_NAME, new ValueString(this.vendorName));
			putProperty(Properties.SPEED, new ValueInteger(this.speed, "Hz"));
			putProperty(Properties.CACHE_SIZE, new ValueInteger(this.cacheSize, "B"));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to Processor.";
		}
	}
	
	/**
	 * Read data about processor from host XML file node.
	 * 
	 * @param procNode <code>processor</code> node from XML file.
	 * 
	 * @throws InputParseException if error occurred when parsing node data.
	 */
	public Processor(Node procNode) throws InputParseException {
		
		super(HostInfoInterface.Objects.PROCESSOR, null);
		
		parseXMLNode(procNode);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {
	
		modelName = XMLHelper.getSubNodeValueByName("model", node);
		vendorName = vendorIDToVendorName(XMLHelper.getSubNodeValueByName("vendor", node));
		speed = 1000000L * Long.valueOf(XMLHelper.getSubNodeValueByName("speed", node)).intValue();
		cacheSize = 1024L * Long.valueOf(XMLHelper.getSubNodeValueByName("l2CacheSize", node)).intValue();		

		try {
			putProperty(Properties.MODEL_NAME, new ValueString(modelName));
			putProperty(Properties.VENDOR_NAME, new ValueString(vendorName));
			putProperty(Properties.SPEED, new ValueInteger(speed, "Hz"));
			putProperty(Properties.CACHE_SIZE, new ValueInteger(cacheSize, "B"));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to Processor.";
		}
	}
	
	/**
	 * Get model name string of CPU.
	 * 
	 * @return Model name.
	 */
	public String getModelName() {
		
		return modelName;
	}
	
	/**
	 * Get name of vendor.
	 * 
	 * @return String with vendor's name.
	 */
	public String getVendorName() {
	
		return vendorName;
	}
	
	/**
	 * Convert vendor ID to vendor name.
	 * 
	 * @param cpuID ID to convert.
	 * @return Vendor name.
	 */
	public static String vendorIDToVendorName(String cpuID) {

		if (cpuID.equals("AuthenticAMD")) {
			return "AMD";
		} else if (cpuID.equals("GenuineIntel")) {
			return "Intel";
		} else if (cpuID.equals("CentaurHauls")) {
			return "Centaur";
		} else if (cpuID.equals("CyrixInstead")) {
			return "Cyrix";
		} else if (cpuID.equals("UMC UMC UMC")) {
			return "UMC";
		} else if (cpuID.equals("NexGenDriven")) {
			return "NexGen";
		} else if (cpuID.equals("RiseRiseRise")) {
			return "Rise";
		} else if (cpuID.equals("GenuineTMx86")) {
			return "Transmeta";
		} else {
			return cpuID;
		}
	}
	
	/**
	 * Get CPU speed.
	 * 
	 * @return Speed of CPU in Hz.
	 */
	public long getSpeed() {
		
		return speed;
	}
	
	/**
	 * Get size of on-board cache.
	 * 
	 * @return Size of L2 cache in B.
	 */
	public long getCacheSize() {
		
		return cacheSize;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element processorElement = document.createElement("processor");
		
		processorElement.appendChild(XMLHelper.writeValueToElement(document, modelName, "model"));
		processorElement.appendChild(XMLHelper.writeValueToElement(document, vendorName, "vendor"));
		processorElement.appendChild(XMLHelper.writeValueToElement(document, speed / 1000000, "speed"));
		processorElement.appendChild(XMLHelper.writeValueToElement(document, cacheSize / 1024, "l2CacheSize"));
		
		return processorElement;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "Processor{" + modelName + " @ " + (speed / 1000000) + " MHz}";
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "processor";
	}
}
