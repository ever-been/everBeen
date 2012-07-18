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
import java.io.OutputStream;

/**
 * A simple output stream that wraps a command line response and writes to its error output.
 * 
 * @author Andrej Podzimek
 */
final class ResponseErrorRawStream extends OutputStream {
	
	/** The command line response to which all the write() calls will be relayed. */
	private final CommandLineResponse response;
	
	/**
	 * Initializes a new instance of the stream wrapper with the supplied command line response.
	 * 
	 * @param response
	 */
	public ResponseErrorRawStream( CommandLineResponse response ) {
		this.response = response;
	}

	@Deprecated
	@Override
	public void write( int b ) throws IOException {
		response.sendRawErr( new byte[] { (byte) b } );
	}
	
	@Override
	public void write( byte[] b ) throws IOException {
		response.sendRawErr( b );
	}

	@Deprecated
	@Override
	public void write( byte[] b, int off, int len ) throws IOException {
		if ( 0 == off && b.length == len ) {
			response.sendRawErr( b );
		} else {
			byte[] copy;
			
			copy = new byte[ len ];
			System.arraycopy( b, off, copy, 0, len );
			response.sendRawErr( copy );
		}
	}
}
