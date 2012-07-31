/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.clinterface;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A stream filter that emits two zeros instead of one.
 * 
 * @author Andrej Podzimek
 */
final class ResponseEscapeStream extends OutputStream {
	
	/** The escape sequence sent instead of 0. */
	private static final byte[] ZERO_ZERO = { 0, 0 };
	
	/** The stream to which calls will be relayed. Must be buffered to avoid huge overhead. */
	private final BufferedOutputStream stream;
	
	/**
	 * Initializes a new instance of the stream filter with the supplied buffered stream.
	 * 
	 * @param stream The output stream to which all the calls will be relayed.
	 */
	public ResponseEscapeStream( BufferedOutputStream stream ) {
		this.stream = stream;
	}

	@Override
	public void write( int b ) throws IOException {
		if ( 0 == b ) {
			stream.write( ZERO_ZERO );
		} else {
			stream.write( b );
		}
	}
	
	@Override
	public void flush() throws IOException {
		stream.flush();
	}
	
	@Override
	public void close() throws IOException {
		stream.close();
	}
}
