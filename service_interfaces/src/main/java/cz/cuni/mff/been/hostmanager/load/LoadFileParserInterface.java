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

import java.io.IOException;

import java.util.List;

import cz.cuni.mff.been.hostmanager.IllegalOperationException;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;

/**
 * Interface for the load file parser. This interface provides only read-only access to the load files.
 * 
 * @param <T> Type of the nodes in the file.
 * 
 * @author Branislav Repcek
 */
public interface LoadFileParserInterface< T extends ByteBufferSerializableInterface > {

	/**
	 * Get next node from the file.
	 * 
	 * @return Node read from the file.
	 *  
	 * @throws InputParseException If an error occurred while parsing node's data.
	 * @throws IOException If an error occurred while reading data from disk.
	 * @throws IllegalOperationException If the file has been closed.
	 */
	T getNext() throws InputParseException, IOException, IllegalOperationException;
	
	/**
	 * Get multiple nodes from file.
	 * 
	 * @param n Number of nodes to read from file.
	 * @return List of the nodes read from the file.
	 * 
	 * @throws InputParseException If an error occurred while parsing nodes' data.
	 * @throws IOException If an error occurred while reading data from disk (e. g. end of file).
	 * @throws IllegalOperationException If the file has been closed.
	 * @throws InvalidArgumentException If n is negative.
	 */
	List< T > getNext(int n)
		throws InputParseException, IOException, IllegalOperationException, InvalidArgumentException;
	
	/**
	 * Test if next node is present in the file.
	 * 
	 * @return <tt>true</tt> if current node is not the last one, <tt>false</tt> otherwise.
	 * 
	 * @throws InputParseException If an error occurred while parsing node's data.
	 * @throws IOException If an error occurred while reading data from disk.
	 * @throws IllegalOperationException If the file has been closed.
	 */
	boolean hasNext() 
		throws InputParseException, IOException, IllegalOperationException;
	
	/**
	 * Skip some nodes.
	 * 
	 * @param amount Number of nodes to skip. If there are not enough nodes in the file, an exception
	 *        is thrown.
	 * 
	 * @throws InputParseException If an error occurred while parsing node's data.
	 * @throws IOException If an error occurred while reading data from disk.
	 * @throws IllegalOperationException If the file has been closed.
	 * @throws InvalidArgumentException If amount is negative.
	 */
	void skip(int amount)
		throws InputParseException, IOException, IllegalOperationException, InvalidArgumentException;
	
	/**
	 * Close the file. Any attempts to read from file will result in IllegalOperationException.
	 * 
	 * @throws IOException If an IO error occurred while closing the file.
	 */
	void close() throws IOException;
	
	/**
	 * Seek to the given position in the file. Since nodes in file may be of different sizes, great
	 * care has to be taken when seeking to ensure that correct data will be read.
	 * 
	 * @param position Position in the file to seek to. Measured in bytes since the beginning of 
	 *        the file.
	 *        
	 * @throws IllegalArgumentException If position is negative.
	 * @throws IOException If an IO error occurred.
	 */
	void seek(long position) throws IllegalArgumentException, IOException;
	
	/**
	 * Get current position in the file. Position is measured in bytes from the beginning of the
	 * file and denotes place within the file on which next read or write will occur.
	 * 
	 * @return Position in the file in bytes from the beginning of the file.
	 * 
	 * @throws IOException If an IO error occurred.
	 */
	long getPosition() throws IOException;
}
