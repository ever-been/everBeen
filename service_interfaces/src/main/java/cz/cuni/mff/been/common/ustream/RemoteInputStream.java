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
package cz.cuni.mff.been.common.ustream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * The stream-like facade for remote input streams.
 * 
 * @author Andrej Podzimek
 */
public final class RemoteInputStream extends InputStream implements Serializable {

	private static final long serialVersionUID = 2564886087073331886L;

	/** The UnicastRemoteObject to which all the read() calls are relayed. */
	private final InternalInputStreamBody internalInputStream;
	
	/**
	 * Creates a new instance of RemoteInputStream that (after RMI transfer) will behave exactly
	 * the same way as if it was a local stream.
	 * 
	 * @param localInputStream The local input stream this stream will read from.
	 * @throws RemoteException When it rains.
	 */
	public RemoteInputStream( InputStream localInputStream ) throws RemoteException {
		internalInputStream = new InternalInputStreamBody( localInputStream );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read() throws IOException {
		throw new UnsupportedOperationException( "Remote input streams only transfer arrays." );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read( byte[] b ) throws IOException {
		byte[] result;
		
		result = internalInputStream.read( b.length );
		System.arraycopy( result, 0, b, 0, result.length );
		return result.length;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read( byte[] b, int off, int len ) throws IOException {
		byte[] result;
		
		result = internalInputStream.read( len );
		System.arraycopy( result, 0, b, off, result.length );
		return result.length;
	}
	
	/**
	 * A more efficent implementation of read() that doesn't involve an extra arrayCopy().
	 * 
	 * @param len Number of bytes to read.
	 * @return An array of at most {@code len} bytes.
	 * @throws IOException When it rains.
	 */
	public byte[] read( int len ) throws IOException {
		return internalInputStream.read( len );
	}
}
