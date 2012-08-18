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
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * The stream-like facade to remote output streams.
 * 
 * @author Andrej Podzimek
 */
public final class RemoteOutputStream extends OutputStream implements Serializable {

	private static final long serialVersionUID = -8809780567152878209L;
	
	/** The UnicastRemoteObject to which all the write() calls are relayed. */
	private final InternalOutputStream internalOutputStream;

	/**
	 * Creates a new instance of RemoteOutputStream that (after RMI transfer) will behave exactly
	 * the same way as if it was a local stream.
	 * 
	 * @param localOutputStream The local output stream this stream will write to.
	 * @throws RemoteException 
	 */
	public RemoteOutputStream( OutputStream localOutputStream ) throws RemoteException {
		internalOutputStream = new InternalOutputStreamBody( localOutputStream );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write( int b ) throws IOException {
		throw new UnsupportedOperationException( "Remote output streams only transfer arrays." );
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write( byte[] b ) throws IOException {
		internalOutputStream.write( b );
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write( byte[] b, int off, int len ) throws IOException {
		internalOutputStream.write( b, off, len );
	}
}
