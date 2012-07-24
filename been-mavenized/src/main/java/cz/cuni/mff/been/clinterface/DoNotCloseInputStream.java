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

import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream decorator that discards all attempts to close the stream. Used with RMIIO,
 * where the close() operation has a special meaning and should be reported via callback
 * interfaces, but the actual call on the local stream must not occur.
 * 
 * @author Andrej Podzimek
 */
final class DoNotCloseInputStream extends InputStream {

	/** The stream to which all the calls (except close()) will be relayed. */
	private final InputStream stream;
	
	/**
	 * Initializes a new instance of this decorator with the supplied input stream.
	 * 
	 * @param stream The stream to which all the calls except close() will be relayed.
	 */
	public DoNotCloseInputStream( InputStream stream ) {
		this.stream = stream;
	}
	
	@Override
	public int read() throws IOException {
		return stream.read();
	}

	@Override
	public int read( byte[] b ) throws IOException {
		return stream.read( b );
	}
	
	@Override
	public int read( byte[] b, int off, int len ) throws IOException {
		return stream.read( b, off, len );
	}
	
	@Override
	public void close() throws IOException {
		// Do nothing!
	}
	
	@Override
	public int available() throws IOException {
		return stream.available();
	}
	
	@Override
	public synchronized void mark( int readlimit ) {
		stream.mark( readlimit );
	}
	
	@Override
	public synchronized void reset() throws IOException {
		stream.reset();
	}
	
	@Override
	public long skip( long n ) throws IOException {
		return stream.skip( n );
	}
	
	@Override
	public boolean markSupported() {
		return stream.markSupported();
	}	
}
