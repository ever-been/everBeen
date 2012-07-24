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

import java.io.Serializable;

import java.nio.ByteBuffer;

import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.OutputWriteException;

/**
 * Interface which contains methods to work with objects that can be serialised to the byte stream. 
 *
 * @author Branislav Repcek
 */
public interface ByteBufferSerializableInterface extends Serializable {

	/**
	 * Load contents of the object from the ByteBuffer.
	 * 
	 * @param buffer Buffer from which data is to be read. 
	 * 
	 * @return Buffer after data has been read. Buffer position has to be set to the byte just
	 *         after the data for current object.
	 *         
	 * @throws InputParseException If an error occurred while parsing buffer's data.
	 */
	ByteBuffer load(ByteBuffer buffer) throws InputParseException;
	
	/**
	 * Save object's data to the buffer.
	 * 
	 * @param buffer Buffer to which data should be saved. Buffer has to be opened for writing (that 
	 *        is, it cannot be read-only).
	 * 
	 * @return Buffer after modifications. Buffer position has to be set just after the data of
	 *         current object. 
	 *         
	 * @throws OutputWriteException If an error occurred when data is being written to the buffer.
	 */
	ByteBuffer save(ByteBuffer buffer) throws OutputWriteException;

	/**
	 * Get total size in bytes this object will occupy when serialised.
	 * 
	 * @return Number of bytes object will occupy when serialised.
	 */
	int getDataSize();
	
	/**
	 * Get ID of the container chunk.
	 * 
	 * @return ID of the container chunk. ID cannot be 0.
	 */
	byte getChunkID();
}
