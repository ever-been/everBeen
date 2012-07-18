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

package cz.cuni.mff.been.hostmanager.load;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import java.util.Date;

import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.OutputWriteException;
import cz.cuni.mff.been.hostmanager.util.TimeUtils;

/**
 * This class stores data about names of various hardware components whose performance is monitored
 * by the Load Monitor.
 * Note that data from the Load Monitor may be different than the data stored in the 
 * {@link cz.cuni.mff.been.hostmanager.database.HostInfoInterface} and they cannot be used 
 * interchargeably.
 *
 * @author Branislav Repcek
 */
public class HardwareDescription implements ByteBufferSerializableInterface {

	private static final long	serialVersionUID	= -5635551460048221971L;

	/**
	 * Id of the chunk in the file.
	 */
	private static final byte FILE_CHUNK_ID = 0x3;
	
	/**
	 * Minimum size of the node.
	 */
	private static final int MINIMUM_DATA_SIZE = 22; // Gaaaahaaahah.... 4 hours of debugging!!!!!!
	
	/**
	 * Number of processors installed on the system.
	 */
	private short cpuCount;
	
	/**
	 * Size of physical memory in bytes.
	 */
	private long memorySize;
	
	/**
	 * Drives' names.
	 */
	private String []drives;
	
	/**
	 * Adapters' names.
	 */
	private String []adapters;

	/**
	 * Timestamp (Windows time).
	 */
	private long timeStamp;
	
	/**
	 * Create empty hardware description with timestamp set to current time.
	 */
	public HardwareDescription() {
		
		timeStamp = TimeUtils.convertJavaDateToWindowsTime(new Date());
	}

	/**
	 * @return Number of processors installed on the system.
	 */
	public short getCpuCount() {

		return cpuCount;
	}

	/**
	 * @param cpuCount Number of processors installed on the system.
	 */
	public void setCpuCount(short cpuCount) {
		
		this.cpuCount = cpuCount;
	}

	/**
	 * @return Size of the physical memory in bytes.
	 */
	public long getMemorySize() {
		
		return memorySize;
	}

	/**
	 * @param memorySize Size of the physical memory in bytes.
	 */
	public void setMemorySize(long memorySize) {
		
		this.memorySize = memorySize;
	}

	/**
	 * @return Number of drives.
	 */
	public int getDriveCount() {
		
		if (drives == null) {
			return 0;
		}
		
		return drives.length;
	}
	
	/**
	 * @return Number of adapters.
	 */
	public int getAdapterCount() {
		
		if (adapters == null) {
			return 0;
		}
		
		return adapters.length;
	}
	
	/**
	 * @return Array containing names of the adapters.
	 */
	public String[] getAdapters() {
		
		return adapters;
	}

	/**
	 * @param adapters Array containing names of the network adapters.
	 */
	public void setAdapters(String[] adapters) {
		
		this.adapters = adapters;
	}

	/**
	 * @return Array containing names of the physical drives.
	 */
	public String[] getDrives() {
		
		return drives;
	}

	/**
	 * @param drives Array containing names of the physical drives.
	 */
	public void setDrives(String[] drives) {
		
		this.drives = drives;
	}

	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		
		return timeStamp;
	}

	/**
	 * @param timeStamp Timestamp (Windows time).
	 */
	public void setTimeStamp(long timeStamp) {
		
		this.timeStamp = timeStamp;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.ByteBufferSerializableInterface#getDataSize()
	 */
	public int getDataSize() {
		
		int driveStrings = 0;
		
		for (int i = 0; i < getDriveCount(); ++i) {
			driveStrings += 4 + 2 * drives[i].length();
		}
		
		int adapterStrings = 0;
		
		for (int i = 0; i < getAdapterCount(); ++i) {
			adapterStrings += 4 + 2 * adapters[i].length();
		}

		return MINIMUM_DATA_SIZE + driveStrings + adapterStrings;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.ByteBufferSerializableInterface#getChunkID()
	 */
	public byte getChunkID() {
		
		return FILE_CHUNK_ID;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.ByteBufferSerializableInterface#load(java.nio.ByteBuffer)
	 */
	public ByteBuffer load(ByteBuffer buffer) throws InputParseException {

		/*
		 * For data storage details see comments in save method.
		 */
		
		try {
			timeStamp = buffer.getLong();
			
			cpuCount = buffer.getShort();
			if (cpuCount < 0) {
				throw new InputParseException("CPU count is too small.");
			}

			memorySize = buffer.getLong();
			if (memorySize < 0) {
				throw new InputParseException("Memory size cannot be negative.");
			}
			
			int driveCount = buffer.getShort();
			
			if (driveCount < 0) {
				throw new InputParseException("Drive count is negative.");
			}
			
			if (driveCount > 0) {
				drives = new String[driveCount];
				
				for (int i = 0; i < driveCount; ++i) {
					drives[i] = readString(buffer);
				}
				
			} else {
				drives = null;
			}
			
			int adapterCount = buffer.getShort();
			
			if (adapterCount < 0) {
				throw new InputParseException("Adapter count is negative.");
			}
			
			if (adapterCount > 0) {
				adapters = new String[adapterCount];
				
				for (int i = 0; i < adapterCount; ++i) {
					adapters[i] = readString(buffer);
				}
			} else {
				adapters = null;
			}
			
		} catch (BufferUnderflowException e) {
			throw new InputParseException("Buffer underflow.", e);
		}
		
		return buffer;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.ByteBufferSerializableInterface#save(java.nio.ByteBuffer)
	 */
	public ByteBuffer save(ByteBuffer buffer) throws OutputWriteException {
		
		/*
		 * Data layout:
		 * 
		 *     Offset(B)     Type       Description
		 *        0          long       Time stamp.
		 *        8          short      Number of processors.
		 *       10          long       RAM size.
		 *       18          short      Number of drives.
		 *       20          String[]   Descriptions for drives
		 *       ?           short      Number of adapters.
		 *       ?+2         String[]   Descriptions for adapters.
		 */

		if (buffer.isReadOnly()) {
			throw new OutputWriteException("Unable to write to the read-only buffer.");
		}
		
		try {
			buffer.putLong(timeStamp);
			
			buffer.putShort(cpuCount);
			buffer.putLong(memorySize);
			buffer.putShort((short) getDriveCount());
			
			for (int i = 0; i < getDriveCount(); ++i) {
				saveString(buffer, drives[i]);
			}
			
			buffer.putShort((short) getAdapterCount());
			
			for (int i = 0; i < getAdapterCount(); ++i) {
				saveString(buffer, adapters[i]);
			}
			
		} catch (BufferOverflowException e) {
			throw new OutputWriteException("Buffer overflow.", e);
		}
		
		return buffer;
	}
	
	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof HardwareDescription) {
			return equals((HardwareDescription) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Compare two hardware descriptions for equality.
	 * 
	 * @param hardware Object to compare to.
	 * 
	 * @return <tt>true</tt> if both objects contain same data, <tt>false</tt> otherwise.
	 */
	public boolean equals(HardwareDescription hardware) {
		
		if (hardware == this) {
			return true;
		}
		
		if (hardware == null) {
			return false;
		}
		
		if (timeStamp != hardware.timeStamp) {
			return false;
		}
		
		if (getDriveCount() > 0) {
			if (hardware.getDriveCount() != getDriveCount()) {
				return false;
			}
			
			for (int i = 0; i < getDriveCount(); ++i) {
				if (!drives[i].equals(hardware.drives[i])) {
					return false;
				}
			}
		} else {
			if (hardware.getDriveCount() > 0) {
				return false;
			}
		}
		
		if (getAdapterCount() > 0) {
			if (hardware.getAdapterCount() != getAdapterCount()) {
				return false;
			}
			
			for (int i = 0; i < getAdapterCount(); ++i) {
				if (!adapters[i].equals(hardware.adapters[i])) {
					return false;
				}
			}
		} else {
			if (hardware.getAdapterCount() > 0) {
				return false;
			}
		}

		return cpuCount == hardware.cpuCount;
	}
	
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 2 * (int) timeStamp + 7 * cpuCount;
		
		for (int i = 0; i < getDriveCount(); ++i) {
			hash += 3 * drives[i].hashCode();
		}
		
		for (int i = 0; i < getAdapterCount(); ++i) {
			hash += 5 * adapters[i].hashCode();
		}
		
		return hash;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "Description: " + String.valueOf(timeStamp); 
	}
	
	/**
	 * Serialise string to the {@link ByteBuffer}. Total size of the data written to the buffer will
	 * be <tt>4 + 2 * s.length()</tt>.
	 * 
	 * @param buffer Buffer to which data should be saved.
	 * @param s String to store in the buffer. At first, length of the string is stored as int, then
	 *        all characters of the string are stored.
	 *        
	 * @throws BufferOverflowException If buffer overflow occurred.
	 */
	private void saveString(ByteBuffer buffer, String s) throws BufferOverflowException {
		
		if (s == null) {
			buffer.putInt(0);
		} else {
			buffer.putInt(s.length());
			
			for (int i = 0; i < s.length(); ++i) {
				buffer.putChar(s.charAt(i));
			}
		}
	}
	
	/**
	 * Read string from the buffer. String has to be saved using saveString method.
	 * 
	 * @param buffer Buffer to read string from.
	 * 
	 * @return String as read from current position in the buffer.
	 * 
	 * @throws BufferUnderflowException Buffer underflow.
	 * @throws InputParseException If string length is negative. 
	 */
	private String readString(ByteBuffer buffer) throws BufferUnderflowException, InputParseException {
		
		StringBuilder builder = new StringBuilder();
		
		int strLen = buffer.getInt();
		
		if (strLen < 0) {
			throw new InputParseException("String length is negative.");
		}
		
		for (int i = 0; i < strLen; ++i) {
			builder.append(buffer.getChar());
		}
		
		return builder.toString();
	}
}
