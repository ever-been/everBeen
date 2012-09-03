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

import java.util.Arrays;
import java.util.Date;

import cz.cuni.mff.been.common.util.MiscUtils;
import cz.cuni.mff.been.common.util.TimeUtils;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.OutputWriteException;


/**
 * One load sample taken on the host. Contains all data collected by the load monitor during
 * one measurement.
 *
 * @author Branislav Repcek
 */
public class LoadSample implements ByteBufferSerializableInterface {

	private static final long	serialVersionUID	= -5877684029105539172L;

	/**
	 * Identification of the file node.
	 */
	private static final byte FILE_NODE_ID = 0x1;
	
	/**
	 * Size of the required data from the sample.
	 */
	private static final int REQUIRED_DATA_SIZE = 38;
	
	/**
	 * TSC counter of the processor = number of ticks since computer has started up.
	 * On multi-cpu machines this may get unsynchronised (TSC drift) if thread jumps from one CPU 
	 * to another - this happens with AMD dual-core CPUs on Windows XP without AMD hotfix 
	 * (may happen on older Windows too).
	 */
	private long tsc;
	
	/**
	 * Time when sample has been taken. This is measured in 100 ns intervals since 1.1.1601 0:00.
	 */
	private long timeStamp;
	
	/**
	 * Usage of each of the processors in the system in per cent.
	 */
	private short []processorUsage;
	
	/**
	 * Number of bytes written per second for each physical drive installed.
	 */
	private long []diskWriteBytes;
	
	/**
	 * Number of bytes read per second for each physical drive installed.
	 */
	private long []diskReadBytes;
	
	/**
	 * Number of bytes received per second for each network interface.
	 */
	private int []networkBytesReceived;
	
	/**
	 * Number of bytes sent per second for each network interface.
	 */
	private int []networkBytesSent;
	
	/**
	 * Free physical memory in bytes.
	 */
	private long memoryFree;
	
	/**
	 * Number of processes running on the system.
	 */
	private int processCount;
	
	/**
	 * Number of processes waiting in the thread queue. This can be used to detect if the processor
	 * is under heavy load (if this remains higher for longer time periods).
	 */
	private int processQueueLength;

	/**
	 * Create new sample with time stamp only.
	 */
	public LoadSample() {

		this.timeStamp = TimeUtils.convertJavaDateToWindowsTime(new Date());
	}

	/**
	 * Gets rate at which bytes are read from each of the drives on the host.
	 *   
	 * @return Array containing read speed of each drive in bytes per second.
	 */
	public long[] getDiskReadBytesPerSecond() {
		
		if (diskReadBytes == null) {
			return null;
		} else {
			return diskReadBytes.clone();
		}
	}

	/**
	 * Gets rate at which bytes are written to each of the drives on the host.
	 *   
	 * @return Array containing write speed of each drive in bytes per second.
	 */
	public long[] getDiskWriteBytesPerSecond() {
		
		if (diskWriteBytes == null) {
			return null;
		} else {
			return diskWriteBytes.clone();
		}
	}

	/**
	 * Get number of physical drives installed on the host. Note that this may change over time (even
	 * when host is running, since hot-swapable drives maybe added or removed).
	 * 
	 * @return Number of physical drives.
	 */
	public int getDriveCount() {
		
		if (diskWriteBytes == null) {
			return 0;
		} else {
			return diskWriteBytes.length;
		}
	}

	/**
	 * Get amount of free physical memory.
	 * 
	 * @return Free memory in bytes.
	 */
	public long getMemoryFree() {
		
		return memoryFree;
	}

	/**
	 * Get rate at which bytes are received through each network interface.
	 * 
	 * @return Number of bytes per second each interface is receiving.
	 */
	public int[] getNetworkBytesReceivedPerSecond() {
		
		if (networkBytesReceived == null) {
			return null;
		} else {
			return networkBytesReceived.clone();
		}
	}

	/**
	 * Get rate at which bytes are sent through each network interface.
	 * 
	 * @return Number of bytes per second each interface is sending.
	 */
	public int[] getNetworkBytesSentPerSecond() {
		
		if (networkBytesSent == null) {
			return null;
		} else {
			return networkBytesSent.clone();
		}
	}

	/**
	 * Get number of network interfaces. This may change even between samples due to the hot-swapable
	 * devices.
	 * 
	 * @return Number of network interfaces.
	 */
	public int getNetworkInterfaceCount() {
	
		if ((networkBytesReceived == null) || (networkBytesSent == null)) {
			return 0;
		} else {
			return networkBytesReceived.length;
		}
	}

	/**
	 * Get number of processes running on the host.
	 * 
	 * @return Number of processes running on the host.
	 */
	public int getProcessCount() {
		
		return processCount;
	}

	/**
	 * Get number of processors installed on the host. Note that "virtual" processors (such as
	 * processors in HT machines) are also counted.
	 * 
	 * @return Number of processors.
	 */
	public int getProcessorCount() {
		
		if (processorUsage == null) {
			return 0;
		} else {
			return processorUsage.length;
		}
	}

	/**
	 * Get number of threads waiting in the processor queue. The higher this number the higher is load.
	 * If this remains higher than 2 or 3 for longer time it means that processor is under heavy load.
	 * 
	 * @return Number of threads waiting in thread queue.
	 */
	public int getProcessQueueLength() {
		
		return processQueueLength;
	}

	/**
	 * @return Get processor usage in per cent for each processor on the host.
	 */
	public short[] getProcessorUsage() {
		
		if (processorUsage == null) {
			return null;
		} else {
			return processorUsage.clone();
		}
	}

	/**
	 * Time stamp value of this sample. Time stamp is measured in 100 ns intervals since 1. 1. 1601 
	 * 0:00 (Windows epoch).
	 * 
	 * @return Time stamp of current sample. 
	 */
	public long getTimeStamp() {
		
		return timeStamp;
	}

	/**
	 * Time stamp counter of the processor on the host in the time sample has been taken. TSC is
	 * number of ticks since computer has been started. Note that this value may jump sometimes as 
	 * thread may be shifted to different processor and TSC of various processors are generally not
	 * synchronised.
	 * 
	 * @return TSC of the host's processor.
	 */
	public long getTSC() {
		
		return tsc;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.ByteBufferSerializableInterface#load(java.nio.ByteBuffer)
	 */
	public ByteBuffer load(ByteBuffer buffer) throws InputParseException {

		/*
		 * For details about data storage see comments in save method below.
		 */
		
		try {
			timeStamp = buffer.getLong();
			tsc = buffer.getLong();
			
			int processorCount = buffer.getShort();
			if (processorCount < 0) {
				throw new InputParseException("Invalid number of processors: " + processorCount);
			}
			if (processorCount > 0) {
				processorUsage = new short[processorCount];
			}
	
			int driveCount = buffer.getShort();
			if (driveCount < 0) {
				throw new InputParseException("Invalid number of disk drives: " + driveCount);
			}
			if (driveCount > 0) {
				diskReadBytes = new long[driveCount];
				diskWriteBytes = new long[driveCount];
			}
			
			int networkCount = buffer.getShort();
			if (networkCount < 0) {
				throw new InputParseException("Invalid number of network adapters: " + networkCount);
			}
			if (networkCount > 0) {
				networkBytesReceived = new int[networkCount];
				networkBytesSent = new int[networkCount];
			}
	
			memoryFree = buffer.getLong();
	
			if (memoryFree < 0) {
				throw new InputParseException("Invalid free memory size: " + memoryFree);
			}
	
			processCount = buffer.getInt();
			
			if (processCount < 0) {
				throw new InputParseException("Invalid process count: " + processCount);
			}
			
			processQueueLength = buffer.getInt();
			if (processQueueLength < 0) {
				throw new InputParseException("Invalid process queue length: " + processQueueLength);
			}
			
			// read cpu usage for each cpu
			for (int i = 0; i < processorCount; ++i) {
				processorUsage[i] = buffer.getShort();
				
				if ((processorUsage[i] < 0) || (processorUsage[i] > 100)) {
					throw new InputParseException("Invalid CPU usage for processor " + i + ": " 
							+ processorUsage[i]);
				}
			}
			
			// read drive read bytes for each drive
			for (int i = 0; i < driveCount; ++i) {
				diskReadBytes[i] = buffer.getLong();
	
				if (diskReadBytes[i] < 0) {
					throw new InputParseException("Invalid read speed for drive " + i + ": " 
							+ diskReadBytes[i]); 
				}
			}
			
			// read drive write speed for each drive
			for (int i = 0; i < driveCount; ++i) {
				diskWriteBytes[i] = buffer.getLong();
	
				if (diskWriteBytes[i] < 0) {
					throw new InputParseException("Invalid write speed for drive " + i + ": " 
							+ diskReadBytes[i]); 
				}
			}
			
			// read speed at which bytes are received through each network adapter
			for (int i = 0; i < networkCount; ++i) {
				networkBytesReceived[i] = buffer.getInt();
				
				if (networkBytesReceived[i] < 0) {
					throw new InputParseException("Invalid read speed for network adapter " + i 
							+ ": " + networkBytesReceived[i]);
				}
			}
			
			// read speed at which bytes are sent through each network adapter
			for (int i = 0; i < networkCount; ++i) {
				networkBytesSent[i] = buffer.getInt();
				
				if (networkBytesSent[i] < 0) {
					throw new InputParseException("Invalid read speed for network adapter " + i 
							+ ": " + networkBytesReceived[i]);
				}
			}
		} catch (BufferUnderflowException e) {
			throw new InputParseException("Error reading sample data.", e);
		}
		
		return buffer;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.ByteBufferSerializableInterface#save(java.nio.ByteBuffer)
	 */
	public ByteBuffer save(ByteBuffer buffer) throws OutputWriteException {

		/*
		 * Sample data are stored in this way:
		 * 
		 *     Offset(B)     Type    Description
		 *        0          long    Time stamp.
		 *        8          long    Time stamp counter of the CPU.
		 *       16          short   Number of processors: P.
		 *       18          short   Number of disk drives: D.
		 *       20          short   Number of network adapters/interfaces: N.
		 *       22          long    Free physical memory in bytes.
		 *       30          int     Number of processes running on the system.
		 *       34          int     Process queue length.
		 *       38          short[] Usage of each processor in percent, length of the array is P.
		 *     38+2*P        long[]  Number of bytes read per second for each drive. 
		 *                           Length of the array is D.
		 *   38+2*P+8*D      long[]  Number of bytes written per second for each drive. 
		 *                           Array length is D.
		 *  38+2*P+16*D      int[]   Number of bytes received per second for each network interface.
		 *                           Array length is N.
		 * 38+2*P+16*D+4*N   int[]   Number of bytes sent per second for each network interface.
		 *                           Array length is N.
		 * 
		 * Total size of the sample is therefore: 38+2*P+16*D+8*N bytes. For typical system with
		 * one processor, one drive and two network interfaces (Ethernet and loopback) it is 72 B.
		 * Minimum size of the sample is 38 bytes (that is, no processor, drive or network interfaces
		 * were detected).
		 */
		
		if (buffer.isReadOnly()) {
			throw new OutputWriteException("Output buffer is read-only.");
		}

		try {
			// write time stamp and TSC values
			buffer.putLong(timeStamp);
			buffer.putLong(tsc);
			
			// write number of cpus, drives and adapters
			buffer.putShort((short) getProcessorCount());
			buffer.putShort((short) getDriveCount());
			buffer.putShort((short) getNetworkInterfaceCount());
	
			buffer.putLong(memoryFree);
			buffer.putInt(getProcessCount());
			buffer.putInt(getProcessQueueLength());
	
			// write CPU usage data
			for (int i = 0; i < getProcessorCount(); ++i) {
				buffer.putShort(processorUsage[i]);
			}
			
			// write disk read speed
			for (int i = 0; i < getDriveCount(); ++i) {
				buffer.putLong(diskReadBytes[i]);
			}
			
			// write disk write speed
			for (int i = 0; i < getDriveCount(); ++i) {
				buffer.putLong(diskWriteBytes[i]);
			}
			
			// write network receiving speed
			for (int i = 0; i < getNetworkInterfaceCount(); ++i) {
				buffer.putInt(networkBytesReceived[i]);
			}
			
			// write network sending speed
			for (int i = 0; i < getNetworkInterfaceCount(); ++i) {
				buffer.putInt(networkBytesSent[i]);
			}

		} catch (BufferOverflowException e) {
			throw new OutputWriteException("Unable to write sample data.", e);
		}
		
		return buffer;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.ByteBufferSerializableInterface#getTotalSize()
	 */
	public int getDataSize() {
		
		return REQUIRED_DATA_SIZE
		       + 2 * getProcessorCount()
		       + 16 * getDriveCount()
		       + 8 * getNetworkInterfaceCount();
	}

	/**
	 * Set read and write speeds for each drive.
	 * 
	 * @param diskReadBytes Read speed for each drive. Values have to be non-negative. 
	 * @param diskWriteBytes Write speed for each drive. Values have to be non-negative.
	 *  
	 * @throws InvalidArgumentException If one of the parameters is <tt>null</tt> or if input arrays
	 *         have different lengths or if some value is negative.
	 */
	public void setDiskReadAndWriteBytes(long[] diskReadBytes, long[] diskWriteBytes) 
		throws InvalidArgumentException {

		MiscUtils.verifyParameterIsNotNull(diskReadBytes, "diskReadBytes");
		MiscUtils.verifyParameterIsNotNull(diskWriteBytes, "diskWriteBytes");
		
		if (diskReadBytes.length != diskWriteBytes.length) {
			throw new InvalidArgumentException("Length of diskReadBytes and diskWriteBytes has to be same.");
		}

		this.diskReadBytes = new long[diskReadBytes.length];
		this.diskWriteBytes = new long[diskWriteBytes.length];

		for (int i = 0; i < diskReadBytes.length; ++i) {
			if (diskReadBytes[i] < 0) {
				throw new InvalidArgumentException("Negative read speed for drive " + i);
			}
			
			if (diskWriteBytes[i] < 0) {
				throw new InvalidArgumentException("Negative write speed for drive " + i);
			}
			
			this.diskReadBytes[i] = diskReadBytes[i];
			this.diskWriteBytes[i] = diskWriteBytes[i];
		}		
	}

	/**
	 * @param memoryFree Free memory.
	 * 
	 * @throws InvalidArgumentException If free memory size specified is negative.
	 */
	public void setMemoryFree(long memoryFree) throws InvalidArgumentException {
		
		MiscUtils.verifyIntParameterGEZero(memoryFree, "memoryFree");
		
		this.memoryFree = memoryFree;
	}

	/**
	 * Set read and write speeds for all network interfaces.
	 * 
	 * @param networkBytesReceived Read speed for given network interface. Values have to be non-negative.
	 * @param networkBytesSent Write speed for given network interface. Values have to be non-negative.
	 * 
	 * @throws InvalidArgumentException If one of the parameters is <tt>null</tt> or if input arrays
	 *         have different lengths or if some value is negative.
	 */
	public void setNetworkReadWriteSpeed(int []networkBytesReceived, int[] networkBytesSent) 
		throws InvalidArgumentException {
		
		MiscUtils.verifyParameterIsNotNull(networkBytesReceived, "networkBytesReceived");
		MiscUtils.verifyParameterIsNotNull(networkBytesSent, "networkBytesSent");
		
		if (networkBytesReceived.length != networkBytesSent.length) {
			throw new InvalidArgumentException("Length of networkBytesReceived and networkBytesSent "
					+ "has to be the same.");
		}
		
		this.networkBytesReceived = new int[networkBytesReceived.length];
		this.networkBytesSent = new int[networkBytesReceived.length];
		
		for (int i = 0; i < networkBytesReceived.length; ++i) {
			if (networkBytesReceived[i] < 0) {
				throw new InvalidArgumentException("Read speed for interface " + i + " is negative.");
			}
			
			if (networkBytesSent[i] < 0) {
				throw new InvalidArgumentException("Write speed for interface " + i + " is negative.");
			}
			
			this.networkBytesReceived[i] = networkBytesReceived[i];
			this.networkBytesSent[i] = networkBytesSent[i];
		}
	}
	
	/**
	 * @param processCount Number of processes. Value has to be non-negative.
	 * 
	 * @throws InvalidArgumentException If process count is negative. 
	 */
	public void setProcessCount(int processCount) throws InvalidArgumentException {

		MiscUtils.verifyIntParameterGEZero(processCount, "processCount");
		
		this.processCount = processCount;
	}

	/**
	 * Set usage of each processor.
	 * 
	 * @param processorUsage Usage of each processor. Each value has to be in range [0, 100].
	 * 
	 * @throws InvalidArgumentException If processor usage is <tt>null</tt> or some value is out of range.
	 */
	public void setProcessorUsage(short[] processorUsage) throws InvalidArgumentException {
		
		MiscUtils.verifyParameterIsNotNull(processorUsage, "processorUsage");
		
		this.processorUsage = new short[processorUsage.length];
		
		for (int i = 0; i < processorUsage.length; ++i) {
			if ((processorUsage[i] < 0) || (processorUsage[i] > 100)) {
				throw new InvalidArgumentException("Invalid CPU usage value for processor " + i);
			}
			
			this.processorUsage[i] = processorUsage[i];
		}
	}

	/**
	 * @param processQueueLength Length of the process queue. Value has to be non-negative.
	 * 
	 * @throws InvalidArgumentException If given value is negative.
	 */
	public void setProcessQueueLength(int processQueueLength) throws InvalidArgumentException {
		
		MiscUtils.verifyIntParameterGEZero(processQueueLength, "processQueueLength");
		
		this.processQueueLength = processQueueLength;
	}

	/**
	 * @param timeStamp New time stamp value.
	 */
	public void setTimeStamp(long timeStamp) {
		
		this.timeStamp = timeStamp;
	}

	/**
	 * @param tsc Time stamp counter value.
	 */
	public void setTSC(long tsc) {
		
		this.tsc = tsc;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof LoadSample) {
			return equals((LoadSample) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Test if two samples contain same values.
	 * 
	 * @param sample Sample to compare to.
	 * 
	 * @return <tt>true</tt> if sample contain same values, <tt>false</tt> otherwise.
	 */
	public boolean equals(LoadSample sample) {
		
		if (sample == this) {
			return true;
		}
		
		if (sample == null) {
			return false;
		}
		
		if (sample.getDataSize() != getDataSize()) {
			return false;
		}
		
		if ((sample.getTimeStamp() != getTimeStamp())
			|| (sample.getTSC() != getTSC())
			|| (sample.getMemoryFree() != getMemoryFree())
			|| (sample.getProcessCount() != getProcessCount())
			|| (sample.getProcessQueueLength() != getProcessQueueLength())) {
			
			return false;
		}
		
		return Arrays.equals(processorUsage, sample.processorUsage)
		       && Arrays.equals(diskReadBytes, sample.diskReadBytes)
		       && Arrays.equals(diskWriteBytes, sample.diskWriteBytes)
		       && Arrays.equals(networkBytesReceived, sample.networkBytesReceived)
		       && Arrays.equals(networkBytesSent, sample.networkBytesSent);
	}
	
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		return 2 * (int) timeStamp
		       + 3 * (int) tsc
		       + 5 * (int) memoryFree
		       + 7 * processCount
		       + 11 * processQueueLength
		       + 13 * Arrays.hashCode(processorUsage)
		       + 17 * Arrays.hashCode(diskReadBytes)
		       + 19 * Arrays.hashCode(diskWriteBytes)
		       + 23 * Arrays.hashCode(networkBytesReceived)
		       + 29 * Arrays.hashCode(networkBytesSent);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.ByteBufferSerializableInterface#getChunkID()
	 */
	public byte getChunkID() {
		
		return FILE_NODE_ID;
	}
}
