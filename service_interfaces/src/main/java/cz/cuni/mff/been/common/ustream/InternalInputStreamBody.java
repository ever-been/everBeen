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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * The reference contained within RemoteInputStream that communicates with the stream's remote
 * implementation.
 * 
 * @author Andrej Podzimek
 */
final class InternalInputStreamBody extends UnicastRemoteObject implements InternalInputStream {

	private static final long serialVersionUID = -1997432325090561964L;
	
	/** The input stream to which read() calls are relayed. */
	private final InputStream localInputStream;

	/**
	 * Initializes the internal stream binding with the provided input stream.
	 * 
	 * @param localInputStream The input stream to which calls will be relayed.
	 * @throws RemoteException When it rains.
	 */
	InternalInputStreamBody( InputStream localInputStream ) throws RemoteException {
		this.localInputStream = localInputStream;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] read( int length ) throws IOException {
		byte[] result;
		int read;
		
		result = new byte[ length ];
		read = localInputStream.read( result );
		
		if ( length == read ) {
			return result;
		} else {
			byte[] smaller;
			
			smaller = new byte[ read ];
			System.arraycopy( result, 0, smaller, 0, read );
			return smaller;
		}
	}
}
