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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import java.nio.channels.FileChannel;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.been.common.util.MiscUtils;
import cz.cuni.mff.been.hostmanager.IllegalOperationException;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.OutputWriteException;


/**
 * This class implements parser of the load files. Data in load files is stored in chunks of
 * varying size. Load file can contain only one type of chunk (e.g. all chunks are either LoadSample
 * or LoadMonitorEvent). This class provides simple, iterator-like interface to work with these files.
 * Parser can be used to either write new files by appending new chunks at the end of the file
 * (modifications in the middle of the file are not allowed) or it can be used to read chunks from
 * file sequentially. 
 *
 * @param <T> Type of the data stored in the file. Only homogenous files are supported.
 *
 * @author Branislav Repcek
 */
public class LoadFileParser< T extends ByteBufferSerializableInterface > 
	implements LoadFileParserInterface< T > {

	/**
	 * Size of the header buffer in bytes. This buffer has constant size and is never reallocated.
	 */
	public static final int HEADER_BUFFER_SIZE = 5;
	
	/**
	 * Size of the footer buffer in bytes. It has constant size and is never reallocated.
	 */
	public static final int FOOTER_BUFFER_SIZE = 1;
	
	/**
	 * File channel that is used when reading/writing to the file.
	 */
	private FileChannel channel;
	
	/**
	 * Buffer for data. Initial size of the buffer is MINIMUM_BUFFER_SIZE.
	 */
	private ByteBuffer dataBuffer;
	
	/**
	 * Buffer for the header. Size is always HEADER_BUFFER_SIZE.
	 */
	private ByteBuffer headerBuffer;
	
	/**
	 * Buffer for the footer. Size is always FOOTER_BUFFER_SIZE.
	 */
	private ByteBuffer footerBuffer;
	
	/**
	 * Class of the class we are reading from the file.
	 */
	private Class< T > fjg;
	
	/**
	 * Value of the next element. If <tt>null</tt>, next element has not been cached yet (or it does not exist).
	 */
	private T next;
	
	/**
	 * ID of the node this stream can save/read.
	 */
	private byte nodeId;
	
	private RandomAccessFile dataFile;
	
	/**
	 * Create new parser.
	 * 
	 * @param fileName Name of the file input/output file. If parser is in append mode and file does
	 *        not exist, it will be automatically created.
	 * @param append If set to <tt>true</tt>, parser that can append but not read data will be created.
	 *        If set to <tt>false</tt>, parser that only reads data will be created.
	 * @param fjg Class of the output data type.
	 * 
	 * @throws FileNotFoundException If in read mode and requested file does not exist.
	 * @throws InvalidArgumentException If class is invalid or if given path does not point to the
	 *         regular file.
	 * @throws IOException If in append mode and there was an error creating new output file. 
	 */
	public LoadFileParser(String fileName, boolean append, Class< T > fjg) 
		throws FileNotFoundException, InvalidArgumentException, IOException {
		
		this.fjg = fjg;
		
		MiscUtils.verifyStringParameterBoth(fileName, "fileName");
		MiscUtils.verifyParameterIsNotNull(fjg, "fjg");
		
		// Now verify that we have correct class.
		try {
			T x = this.fjg.newInstance();
			
			nodeId = x.getChunkID();
		} catch (Exception e) {
			throw new InvalidArgumentException("Invalid class.", e);
		}
		
		File f = new File(fileName);
		
		if (append) {
			dataFile = new RandomAccessFile(f, "rws");
			channel = dataFile.getChannel();
			dataFile.seek(dataFile.length());
		} else {
			dataFile = new RandomAccessFile(f, "r");
			channel = dataFile.getChannel().position(0);
		}

		headerBuffer = ByteBuffer.allocateDirect(HEADER_BUFFER_SIZE);
		footerBuffer = ByteBuffer.allocateDirect(FOOTER_BUFFER_SIZE);
	}
	
	/**
	 * Create new parser.
	 * 
	 * @param file File that will be read or written to. If parser is in append mode and file does
	 *        not exist, it will be automatically created.
	 * @param append If set to <tt>true</tt>, parser that can append but not read data will be created.
	 *        If set to <tt>false</tt>, parser that only reads data will be created.
	 * @param fjg Class of the output data type.
	 * 
	 * @throws FileNotFoundException If in read mode and requested file does not exist.
	 * @throws InvalidArgumentException If class is invalid or if given path does not point to the
	 *         regular file.
	 * @throws IOException If in append mode and there was an error creating new output file. 
	 */
	public LoadFileParser(File file, boolean append, Class< T > fjg)
		throws FileNotFoundException, InvalidArgumentException, IOException {
		
		this.fjg = fjg;
		
		MiscUtils.verifyParameterIsNotNull(file, "file");
		MiscUtils.verifyParameterIsNotNull(fjg, "fjg");
		
		// Now verify that we have correct class.
		try {
			T x = this.fjg.newInstance();
			
			nodeId = x.getChunkID();
		} catch (Exception e) {
			throw new InvalidArgumentException("Invalid class.", e);
		}
		
		if (append) {
			dataFile = new RandomAccessFile(file, "rws");
			channel = dataFile.getChannel();
			dataFile.seek(dataFile.length());
		} else {
			dataFile = new RandomAccessFile(file, "r");
			channel = dataFile.getChannel().position(0);
		}

		headerBuffer = ByteBuffer.allocateDirect(HEADER_BUFFER_SIZE);
		footerBuffer = ByteBuffer.allocateDirect(FOOTER_BUFFER_SIZE);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadFileParserInterface#getNext()
	 */
	public T getNext() throws InputParseException, IOException, IllegalOperationException {

		if (channel == null) {
			throw new IllegalOperationException("Unable to read from the closed file.");
		}
		
		// This will cache new entry if needed.
		if (hasNext()) {
			T result = next;
			next = null;
			
			return result;
		} else {
			throw new IOException("End of file reached.");
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadFileParserInterface#getNext(int)
	 */
	public List< T > getNext(int n)
		throws InputParseException, IOException, IllegalOperationException {

		if (channel == null) {
			throw new IllegalOperationException("Unable to read from the closed file.");
		}
		
		List< T > result = new ArrayList< T >();
		
		for (int i = 0; i < 0; ++i) {
			result.add(getNext());
		}
		
		return result;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadFileParserInterface#hasNext()
	 */
	public boolean hasNext() 
		throws InputParseException, IOException, RuntimeException, IllegalOperationException {
	
		if (channel == null) {
			throw new IllegalOperationException("Unable to read from the closed file.");
		}
		
		if (next == null) {
			return getNextNode();
		} else {
			return true;
		}
		
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadFileParserInterface#skip(int)
	 */
	public void skip(int amount)
		throws InputParseException, IOException, InvalidArgumentException, IllegalOperationException {
		
		if (channel == null) {
			throw new IllegalOperationException("Unable to read from the closed file.");
		}
		
		if (amount < 0) {
			throw new InvalidArgumentException("Unable to skip negative amount of nodes.");
		}

		for (int i = 0; i < amount; ++i) {
			getNext();
		}
	}

	/**
	 * Append node to the file.
	 * 
	 * @param value data to append to the file.
	 * 
	 * @throws InvalidArgumentException If value is <tt>null</tt>.
	 * @throws IllegalOperationException If parser is in read mode or if the file has been closed.
	 * @throws IOException If IO error occurred.
	 */
	public void append(T value)
		throws InvalidArgumentException, IllegalOperationException, IOException {
		
		if (channel == null) {
			throw new IllegalOperationException("Unable to write to the closed file.");
		}
		
		MiscUtils.verifyParameterIsNotNull(value, "value");
		
		if ((dataBuffer == null)
			|| (dataBuffer.capacity() < value.getDataSize() + HEADER_BUFFER_SIZE + FOOTER_BUFFER_SIZE)) {
			dataBuffer = ByteBuffer.allocateDirect(value.getDataSize()
			                                       + HEADER_BUFFER_SIZE
			                                       + FOOTER_BUFFER_SIZE);
		}

		dataBuffer.clear();
		
		dataBuffer.put(value.getChunkID());
		dataBuffer.putInt(value.getDataSize());

		try {
			value.save(dataBuffer);
		} catch (OutputWriteException e) {
			throw new IOException("Unable to write data to the buffer.");
		}
		
		dataBuffer.put((byte) -value.getChunkID());
		
		dataBuffer.flip();
		
		try {
			channel.write(dataBuffer);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Append multiple nodes from array to the file.
	 * 
	 * @param value Array of nodes to append.
	 * 
	 * @throws IOException If an IO error occurred.
	 * @throws InvalidArgumentException If input array is <tt>null</tt> or if some element 
	 *         is <tt>null</tt>.
	 * @throws IllegalOperationException If parser is in read mode or if the file has been closed.
	 */
	public void append(T []value) 
		throws IOException, InvalidArgumentException, IllegalOperationException {
		
		MiscUtils.verifyParameterIsNotNull(value, "value");

		for (T x: value) {
			append(x);
		}
	}
	
	/**
	 * Append data from the list to the output file.
	 * 
	 * @param value List of data nodes to append.
	 * 
	 * @throws IOException If an IO error occurred.
	 * @throws InvalidArgumentException If input list is <tt>null</tt> or if some element 
	 *         is <tt>null</tt>.
	 * @throws IllegalOperationException If parser is in read mode or if the file has been closed.
	 */
	public void append(List< T > value)
		throws IOException, InvalidArgumentException, IllegalOperationException {
		
		MiscUtils.verifyParameterIsNotNull(value, "value");

		for (T x: value) {
			append(x);
		}
	}
	
	/**
	 * Close file.
	 */
	public void close() throws IOException {
		
		channel.close();
		channel = null;
	}

	/*
	 * @see java.lang.Object#finalize()
	 */
	@Override
	public void finalize() throws Throwable {
		
		if (channel != null) {
			try {
				channel.close();
			} catch (Exception e) {
				// Do nothing.
			}
		}
		
		super.finalize();
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.LoadFileParserInterface#getPosition()
	 */
	public long getPosition() throws IOException {
		
		return dataFile.getFilePointer();
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.LoadFileParserInterface#seek(long)
	 */
	public void seek(long position) throws IllegalArgumentException, IOException {

		MiscUtils.verifyIntParameterGEZero(position, "position");
		
		dataFile.seek(position);
	}

	/**
	 * Get next node from file and store it in local cache.
	 * 
	 * @return <tt>true</tt> if operation completed successfully, <tt>false</tt> if end of file has
	 *         been reached.
	 * 
	 * @throws InputParseException If invalid data have been found in file.
	 * @throws IOException If some read error has occurred or if data in file is incomplete.
	 * @throws RuntimeException If an error occurred while instantiating data class.
	 * @throws IllegalOperationException If parser is in append mode.
	 */
	private boolean getNextNode()
		throws InputParseException, IOException, RuntimeException, IllegalOperationException {

		next = null;
		
		// Read node's header data from file. 
		headerBuffer.clear();
		int headerBytes = 0;
		
		try {
			headerBytes = channel.read(headerBuffer);
		} catch (Exception e) {
			throw new IOException("Error reading data from file.");
		}
		
		if (headerBytes == -1) {
			return false;
		}
		
		if (headerBytes < HEADER_BUFFER_SIZE) {
			throw new InputParseException("Unexpected end of file.");
		}
		
		headerBuffer.rewind();
		
		// Parse header from file.
		byte currentId;
		int dataLength;
		
		try {
			currentId = headerBuffer.get();
			dataLength = headerBuffer.getInt();
		} catch (Exception e) {
			throw new InputParseException("Error reading data.", e);
		}
		
		if (currentId != nodeId) {
			throw new InputParseException("Invalid node identification. Found \"" + currentId
					+ "\", expected \"" + nodeId + "\".");
		}
		
		if (dataLength < 0) {
			throw new InputParseException("Invalid node's data size: \"" + dataLength + "\".");
		}

		// Allocate new buffer if necessary.
		if ((dataBuffer == null) || (dataBuffer.capacity() < dataLength)) {
			dataBuffer = ByteBuffer.allocateDirect(dataLength);
		}

		// Read data from file to buffer and rewind buffer to the beginning.
		dataBuffer.clear();
		dataBuffer.limit(dataLength);
		int dataBytes;
		try {
			dataBytes = channel.read(dataBuffer);
		} catch (Exception e) {
			throw new IOException("Error reading data from file.");
		}
		if (dataBytes < dataLength) {
			throw new InputParseException("Unable to read data from file.");
		}
		dataBuffer.rewind();

		// Create new instance of the type we are reading from the file.
		try {
			next = fjg.newInstance();
		} catch (Exception e) {
			// This should never happen, since we checked class in ctor.
			throw new RuntimeException("Unable to create new instance of node.", e);
		}
		
		// Now call load method on the target object.
		next.load(dataBuffer);

		// And finally read footer of the node and verify end mark.
		footerBuffer.clear();
		int footerBytes;
		try {
			footerBytes = channel.read(footerBuffer);
		} catch (Exception e) {
			throw new IOException("Unable to read data.");
		}
		
		if (footerBytes < FOOTER_BUFFER_SIZE) {
			throw new InputParseException("Unexpected end of file.");
		}
		footerBuffer.rewind();
		
		try {
			byte endId = footerBuffer.get();
			
			if (endId != (byte) -nodeId) {
				throw new InputParseException("Invalid end mark of the node. Found \"" + endId
						+ "\", expected value is \"" + (-nodeId) + "\".");
			}
		} catch (BufferUnderflowException e) {
			throw new IOException("Error reading data.");
		}
		
		return true;
	}
}
