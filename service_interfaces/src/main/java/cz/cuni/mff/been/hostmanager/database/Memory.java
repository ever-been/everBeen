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
import cz.cuni.mff.been.hostmanager.InputParseException;



/**
 * Storage class for memory information.
 *
 * @author Branislav Repcek
 */
public class Memory extends PropertyTree 
	implements Serializable, XMLSerializableInterface, PropertyTreeReadInterface {

	private static final long	serialVersionUID	= -3121560518368790338L;

	/**
	 * Encapsulates constants with names of properties of Memory object.
	 *
	 * @author Branislav Repcek
	 */
	public static class Properties {
		
		/**
		 * RAM size (in bytes).
		 */
		public static final String PHYSICAL_MEMORY_SIZE = "physical";
		
		/**
		 * Virtual memory size in bytes.
		 */
		public static final String VIRTUAL_MEMORY_SIZE = "virtual";
		
		/**
		 * Total swap size in bytes.
		 */
		public static final String SWAP_SIZE = "swap";
		
		/**
		 * Total paging file size in bytes.
		 */
		public static final String PAGING_FILE_SIZE = "pagefile";
		
		/** The size used to compute the load capacity when no information is available. */
		public static final long PHYSICAL_MEMORY_GUESS = 0x40000000L;								// 1 GB
		
		/** Number of bytes per load unit. */
		public static final int BYTES_PER_UNIT = 0x100000;											// 1 MB
	}
	
	/**
	 * Size of physical memory (RAM) in bytes.
	 */
	private long physicalSize;
	
	/**
	 * Size of virtual memory in bytes.
	 */
	private long virtualSize;
	
	/**
	 * Size of swap space in bytes.
	 */
	private long swapSize;
	
	/**
	 * Size of paging files in bytes.
	 */
	private long pagingSize;
	
	/**
	 * Initialise class data.
	 * 
	 * @param physicalMemorySize Size of physical memory in bytes.
	 * @param virtualSize Size of virtual memory in bytes.
	 * @param swapSize Size of swap file.
	 * @param pagingFilesSize Size of paging files.
	 */
	public Memory(long physicalMemorySize, long virtualSize, long swapSize, long pagingFilesSize) {
		
		super(HostInfoInterface.Objects.MEMORY, null);
		
		this.physicalSize = physicalMemorySize;
		this.virtualSize = virtualSize;
		this.swapSize = swapSize;
		this.pagingSize = pagingFilesSize;
		
		try {
			putProperty(Properties.PHYSICAL_MEMORY_SIZE, new ValueInteger(this.physicalSize, "B"));
			putProperty(Properties.VIRTUAL_MEMORY_SIZE, new ValueInteger(this.virtualSize, "B"));
			putProperty(Properties.SWAP_SIZE, new ValueInteger(this.swapSize, "B"));
			putProperty(Properties.PAGING_FILE_SIZE, new ValueInteger(this.pagingSize, "B"));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to Memory.";
		}
	}
	
	/**
	 * Read memory info from XML file node.
	 * 
	 * @param memoryNode <code>memory</code> node from host XML file.
	 * 
	 * @throws InputParseException If error occurred when parsing node data.
	 */
	public Memory(Node memoryNode) throws InputParseException {
		
		super(HostInfoInterface.Objects.MEMORY, null);

		parseXMLNode(memoryNode);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {

		physicalSize = Long.valueOf(XMLHelper.getSubNodeValueByName("physicalMemorySize", node)).longValue();
		virtualSize = Long.valueOf(XMLHelper.getSubNodeValueByName("virtualMemorySize", node)).longValue();
		swapSize = Long.valueOf(XMLHelper.getSubNodeValueByName("swapSize", node)).longValue();
		pagingSize = Long.valueOf(XMLHelper.getSubNodeValueByName("pagingFileSize", node)).longValue();

		try {
			putProperty(Properties.PHYSICAL_MEMORY_SIZE, new ValueInteger(physicalSize, "B"));
			putProperty(Properties.VIRTUAL_MEMORY_SIZE, new ValueInteger(virtualSize, "B"));
			putProperty(Properties.SWAP_SIZE, new ValueInteger(swapSize, "B"));
			putProperty(Properties.PAGING_FILE_SIZE, new ValueInteger(pagingSize, "B"));
		} catch (Exception e) {
			e.printStackTrace();
			
			assert false : "Unable to add properties to Memory.";
		}
	}
	
	/**
	 * Get size of physical memory.
	 * 
	 * @return Size of RAM in bytes.
	 */
	public long getPhysicalMemorySize() {
		
		return physicalSize;
	}
	
	/**
	 * Get size of virtual memory.
	 * 
	 * @return Size of virtual memory.
	 */
	public long getVirtualMemorySize() {
		
		return virtualSize;
	}
	
	/**
	 * Get size of swap space.
	 * 
	 * @return Swap space size in bytes.
	 */
	public long getSwapSize() {
		
		return swapSize;
	}
	
	/**
	 * Get size of paging files.
	 * 
	 * @return Size of paging files in bytes.
	 */
	public long getPagingSize() {
		
		return pagingSize;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {
		
		Element memoryElement = document.createElement("memory");
		
		memoryElement.appendChild(XMLHelper.writeValueToElement(document, physicalSize, "physicalMemorySize"));
		memoryElement.appendChild(XMLHelper.writeValueToElement(document, virtualSize, "virtualMemorySize"));
		memoryElement.appendChild(XMLHelper.writeValueToElement(document, swapSize, "swapSize"));
		memoryElement.appendChild(XMLHelper.writeValueToElement(document, pagingSize, "pagingFileSize"));
		
		return memoryElement;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "Memory{" + (physicalSize / 1048576) + " MB}";
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {
		
		return "memory";
	}
}
