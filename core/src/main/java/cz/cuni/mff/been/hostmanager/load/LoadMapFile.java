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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cz.cuni.mff.been.common.util.MiscUtils;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.OutputWriteException;


/**
 * This class handles all manipulation with load maps. Load map file is file which stores positions
 * and of significant events from the load file. Load files have structure that does not permit 
 * random access (since nodes may have different sizes) and can be read only sequentially.
 * Therefore load map file stores file position of each monitor start, monitor stop and hardware
 * description event and timestamp of the event. This provides for much faster seeking in the file
 * because only a portion of the file has to be parsed sequentially (from the nearest older
 * significant event).
 *
 * @author Branislav Repcek
 */
class LoadMapFile {

	/**
	 * File.
	 */
	private RandomAccessFile file;
	
	/**
	 * Channel.
	 */
	private FileChannel channel;
	
	/**
	 * Buffer.
	 */
	private ByteBuffer buffer;
	
	/**
	 * Number of entries that will fit to the preallocated buffer.
	 */
	private static final int BUFFER_ENTRY_COUNT = 10;

	/**
	 * Open load map file.
	 * 
	 * @param fileName Name of the file to open.
	 * @param append If set to <tt>true</tt> file will be open in append mode, otherwise it will be
	 *        opened in read-only mode.
	 * 
	 * @throws IOException If an error occurred.
	 */
	public LoadMapFile(String fileName, boolean append) throws IOException {
		
		File f = new File(fileName);
		
		if (append) {
			if (!f.isFile()) {
				if (!f.createNewFile()) {
					throw new IOException("Unable to create new file.");
				}
			}
			
			file = new RandomAccessFile(fileName, "rws");
			channel = file.getChannel();
			file.seek(file.length());
		} else {
			if (!f.isFile()) {
				throw new IOException("Unable to open file \"" + fileName + "\".");
			}
			
			file = new RandomAccessFile(fileName, "r");
			channel = file.getChannel();
		}
		
		buffer = ByteBuffer.allocateDirect(BUFFER_ENTRY_COUNT * FileEntry.ENTRY_SIZE);
	}
	
	/**
	 * Read entry from given position in the load map file.
	 * 
	 * @param index Index of the entry to read. First entry has index 0, last entry has index
	 *        <tt>getCount() - 1</tt>.
	 *        
	 * @return Requested entry.
	 * 
	 * @throws InputParseException If an error occurred while reading entry from file.
	 * @throws IndexOutOfBoundsException If index is negative or too big.
	 */
	public FileEntry readFrom(int index) throws InputParseException, IndexOutOfBoundsException {
		
		int count = 0;
		
		try {
			count = getCount();
		} catch (IOException e) {
			throw new InputParseException("Unable to determine file size.", e);
		}
		
		if ((index < 0) || (index >= count)) {
			throw new IndexOutOfBoundsException("Entry index out of bounds.");
		}
		
		try {
			file.seek(index * FileEntry.ENTRY_SIZE);
		} catch (IOException e) {
			throw new InputParseException("Error seeking in the file.", e);
		}
		
		ByteBuffer buf = ByteBuffer.allocate(FileEntry.ENTRY_SIZE);
		int bytes = 0;
		
		try {
			bytes = channel.read(buf);
		} catch (IOException e) {
			throw new InputParseException("Unable to read data from channel.", e);
		}
		
		if (bytes < FileEntry.ENTRY_SIZE) {
			throw new InputParseException("Unable to read whole entry.");
		}
		
		buf.rewind();
		
		FileEntry entry = new FileEntry();
		
		entry.load(buf);
		
		return entry;
	}
	
	/**
	 * Read multiple entries from the given position in the map file.
	 * 
	 * @param index Index of the first entry to read. First entry in the file has index 0, last
	 *        entry has index <tt>getCount() - 1</tt>.
	 * @param n Number of entries to read. If there are not enough entries left in the file all
	 *        remaining entries will be read.
	 *   
	 * @return List with all entries that have been read from the file.
	 * 
	 * @throws InputParseException If an error occurred while reading file data.
	 * @throws IndexOutOfBoundsException If index is negative or too big.
	 * @throws InvalidArgumentException If number of entries to read is negative or zero.
	 */
	public List< FileEntry > readFrom(int index, int n)
		throws InputParseException, IndexOutOfBoundsException, InvalidArgumentException {

		MiscUtils.verifyIntParameterGZero(n, "n");
		
		int count = 0;
		
		try {
			count = getCount();
		} catch (IOException e) {
			throw new InputParseException("Unable to determine file size.", e);
		}
		
		if ((index < 0) || (index >= count)) {
			throw new IndexOutOfBoundsException("Entry index out of bounds.");
		}
		
		try {
			file.seek(index * FileEntry.ENTRY_SIZE);
		} catch (IOException e) {
			throw new InputParseException("Error seeking in the file.", e);
		}

		ArrayList< FileEntry > entries = new ArrayList< FileEntry >(n);
		
		int bytes = 0;
		int entriesRead = 0;
		
		while (entriesRead < n) {
			
			buffer.clear();
			
			try {
				bytes = channel.read(buffer);
			} catch (IOException e) {
				throw new InputParseException("Unable to read data from channel.", e);
			}
			
			if (bytes < 0) {
				break;
			}
			
			if (bytes % FileEntry.ENTRY_SIZE != 0) {
				throw new InputParseException("Unable to read whole entry. Read " + bytes + " bytes.");
			}
			
			int ec = bytes / FileEntry.ENTRY_SIZE;

			buffer.rewind();

			for (int i = 0; i < ec; ++i) {
				FileEntry entry = new FileEntry();
				
				entry.load(buffer);
				
				entries.add(entry);
			}
			
			entriesRead += ec;
		}
		
		return entries;
	}
	
	/**
	 * Append one entry at the end of the file.
	 * 
	 * @param entry Entry to append.
	 * 
	 * @return Number of entries in the file.
	 * 
	 * @throws OutputWriteException If an error occurred while writing data.
	 * @throws IOException If an error occurred while parsing file.
	 */
	public int append(FileEntry entry) throws OutputWriteException, IOException {
		
		int count = 0;
		
		count = getCount();

		buffer.clear();
		
		entry.store(buffer);
		
		buffer.flip();
		
		try {
			channel.write(buffer);
		} catch (IOException e) {
			throw new OutputWriteException("Unable to write output.", e);
		}
		
		return count + 1;
	}
	
	/**
	 * Append multiple entries to the end of the load map file.
	 * 
	 * @param entry Array of entries to append.
	 * 
	 * @return Number of entries in the file.
	 * 
	 * @throws OutputWriteException If an error occurred while writing data.
	 * @throws IOException If an error occurred while parsing file.
	 */
	public int append(FileEntry []entry) throws OutputWriteException, IOException {

		int count = 0;
		
		count = getCount();

		int index = 0;
		
		while (index < entry.length) {

			buffer.clear();
			
			for (int q = 0; (index < entry.length) && (q < BUFFER_ENTRY_COUNT); ++q, ++index) {
				entry[index].store(buffer);
			}
			
			buffer.flip();
			
			try {
				channel.write(buffer);
			} catch (IOException e) {
				throw new OutputWriteException("Unable to write output.", e);
			}
		}

		return count + entry.length;
	}

	/**
	 * Append multiple entries at the end of the file.
	 * 
	 * @param entry Collection with entries.
	 * 
	 * @return Number of entries in file after append.
	 * 
	 * @throws OutputWriteException If an error occurred while writing data.
	 * @throws IOException If an error occurred while parsing file.
	 */
	public int append(Collection< FileEntry > entry) throws OutputWriteException, IOException {
		
		int count = 0;
		
		count = getCount();
		
		Iterator< FileEntry > it = entry.iterator();
		
		while (it.hasNext()) {
			
			buffer.clear();
			
			for (int q = 0; it.hasNext() && (q < BUFFER_ENTRY_COUNT); ++q) {
				FileEntry e = it.next();
				e.store(buffer);
			}
			
			buffer.flip();
			
			try {
				channel.write(buffer);
			} catch (IOException e) {
				throw new OutputWriteException("Unable to write output.", e);
			}
		}
		
		return count + entry.size();
	}
	
	/**
	 * Close output (or input) file.
	 * 
	 * @throws IOException If an error occurred while closing the file.
	 */
	public void close() throws IOException {
		
		file.close();
	}
	
	/*
	 * @see java.lang.Object#finalize()
	 */
	@Override
	public void finalize() throws Throwable {
		
		try {
			file.close();
		} finally {
			super.finalize();
		}
	}
	
	/**
	 * Get number of entries in the file.
	 * 
	 * @return Total number of entries in the file.
	 * 
	 * @throws IOException If an error occurred while reading file.
	 */
	public int getCount() throws IOException {
		
		return (int) (file.length() / FileEntry.ENTRY_SIZE);
	}
	
	/**
	 * This class holds data about entry in the load file.
	 *
	 * @author Branislav Repcek
	 */
	public static class FileEntry {

		/**
		 * Size of the entry in bytes.
		 */
		public static final int ENTRY_SIZE = 17;
		
		/**
		 * Timestamp (windows time).
		 */
		private long timeStamp;
		
		/**
		 * Position in the file.
		 */
		private long position;
		
		/**
		 * Type of the event.
		 */
		private byte type;
		
		/**
		 * Create new FileEntry.
		 */
		public FileEntry() {
		}

		/**
		 * Create new FileEntry.
		 * 
		 * @param timeStamp Entry timestamp.
		 * @param position Position in the file.
		 * @param type Event type.
		 * 
		 * @throws InvalidArgumentException If index or position is negative. 
		 */
		public FileEntry(long timeStamp, long position, LoadMonitorEvent.EventType type)
			throws InvalidArgumentException {
			
			MiscUtils.verifyIntParameterGEZero(position, "position");
			
			this.timeStamp = timeStamp;
			this.position = position;
			this.type = (byte) type.ordinal();
		}

		/**
		 * @return Position of the entry in the load file (bytes from the beginning of the file).
		 */
		public long getPosition() {
			
			return position;
		}

		/**
		 * @return Timestamp of the entry (windows time).
		 */
		public long getTimeStamp() {
			
			return timeStamp;
		}
		
		/**
		 * @return Event type.
		 */
		public LoadMonitorEvent.EventType getType() {
			
			return LoadMonitorEvent.EventType.valueOf(type);
		}
		
		/**
		 * Store data from this entry in the buffer.
		 * 
		 * @param buffer Buffer to store data in.
		 * 
		 * @return Buffer after the data has been stored.
		 * 
		 * @throws OutputWriteException If an error occurred while writing data to the buffer.
		 */
		public ByteBuffer store(ByteBuffer buffer) throws OutputWriteException {

			try {
				buffer.putLong(timeStamp);
				buffer.putLong(position);
				buffer.put(type);
			} catch (Exception e) {
				throw new OutputWriteException("Unable to write to the buffer.", e);
			}
			
			return buffer;
		}

		/**
		 * Read data from the buffer.
		 * 
		 * @param buffer Buffer to read the data from.
		 * 
		 * @return Buffer after the data has been read.
		 * 
		 * @throws InputParseException If an error occurred while reading data or if the buffer
		 *         contains invalid data.
		 */
		public ByteBuffer load(ByteBuffer buffer) throws InputParseException {
			
			try {
				timeStamp = buffer.getLong();
				position = buffer.getLong();
				type = buffer.get();
				
				if (position < 0) {
					throw new InputParseException("Invalid file position: \"" + position + "\".");
				}
				
			} catch (BufferUnderflowException e) {
				throw new InputParseException("Buffer underflow.", e);
			}
			
			return buffer;
		}

		/*
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			
			return getType().toString() + ": " + getTimeStamp() + "@" + getPosition();
		}
		
		/* 
		 * @see java.lang.Object#hashCode() 
		 */
		@Override
		public int hashCode() {
			
			final int prime = 31;
			
			int result = 1;

			result = prime * result + (int) (position ^ (position >>> 32));
			result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
			result = prime * result + (type ^ (type >>> 32));
			
			return result;
		}

		/* 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			
			if (this == obj) {
				return true;
			}
			
			if (obj == null) {
				return false;
			}
			
			if (getClass() != obj.getClass()) {
				return false;
			}
			
			final FileEntry other = (FileEntry) obj;
			
			if (position != other.position) {
				return false;
			}
			
			if (timeStamp != other.timeStamp) {
				return false;
			}
			
			if (type != other.type) {
				return false;
			}
			
			return true;
		}
		
		
	}
}
