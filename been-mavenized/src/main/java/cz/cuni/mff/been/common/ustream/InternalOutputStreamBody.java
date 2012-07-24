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
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * The reference contained within RemoteOutputStream that communicates with the stream's remote
 * implementation.
 * 
 * @author Andrej Podzimek
 */
final class InternalOutputStreamBody extends UnicastRemoteObject implements InternalOutputStream {

	private static final long serialVersionUID = -2536391154768648614L;

	/** The output stream to which write() calls are relayed. */
	private final OutputStream localOutputStream;

	/**
	 * Initializes the internal stream binding with the provided output stream.
	 * 
	 * @param localOutputStream The output stream to which calls will be relayed.
	 * @throws RemoteException When it rains.
	 */
	InternalOutputStreamBody( OutputStream localOutputStream ) throws RemoteException {
		this.localOutputStream = localOutputStream;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void write( byte[] data ) throws IOException {
		localOutputStream.write( data );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void write( byte[] data, int offset, int length ) throws IOException {
		localOutputStream.write( data, offset, length );
	}
}
