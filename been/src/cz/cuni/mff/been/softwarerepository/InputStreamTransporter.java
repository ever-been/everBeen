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
package cz.cuni.mff.been.softwarerepository;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;

import cz.cuni.mff.been.clinterface.adapters.InputStreamEndMonitor;

/**
 * This is a simple wrapper for the RMIIO library.
 * 
 * @author Andrej Podzimek
 */
public final class InputStreamTransporter implements InputStreamInterface {
	
	private static final long	serialVersionUID	= 8857452141714859386L;
	
	/** The internal serializable stream instance. */
	final RemoteInputStream stream;
	
	/**
	 * Initializes the new wrapper with the supplied input stream and stream monitor.
	 * 
	 * @param stream The input stream to make available remotely.
	 * @param monitor The monitor used to track the activity of the stream.
	 * @throws RemoteException When it rains.
	 */
	public InputStreamTransporter( InputStream stream, InputStreamEndMonitor monitor )
	throws RemoteException {
		this.stream = new SimpleRemoteInputStream( stream, monitor ).export();
	}

	/**
	 * Input stream getter.
	 * 
	 * @return An input stream which points at the remote stream server.
	 * @throws IOException When something bad happens.
	 */
	public InputStream getInputStream() throws IOException {
		return RemoteInputStreamClient.wrap( stream );
	}

	/**
	 * Closes the underlying remote stream implementation.
	 * 
	 * @param success The flag set by the remote stream.
	 * @throws IOException When closing fails.
	 */
	public void close( boolean success ) throws IOException {
		stream.close( success );
	}
}
