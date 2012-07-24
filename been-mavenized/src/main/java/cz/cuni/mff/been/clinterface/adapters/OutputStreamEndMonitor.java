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
package cz.cuni.mff.been.clinterface.adapters;

import com.healthmarketscience.rmiio.RemoteOutputStreamMonitor;
import com.healthmarketscience.rmiio.RemoteOutputStreamServer;

/**
 * Monitors the status of an output stream and signals a sleeping thread when the stream is closed.
 * 
 * @author Andrej Podzimek
 */
public final class OutputStreamEndMonitor extends RemoteOutputStreamMonitor {
	
	/** Status of the remote stream. */
	private RemoteStreamStatus status;
	
	/** Exception thrown on stream failure (when applicable) */
	private Exception exception;
	
	/**
	 * Initializes a new monitor to wait for the end of the given stream.
	 */
	public OutputStreamEndMonitor() {
		this.status = RemoteStreamStatus.WORKING;
	}
	
	@Override
	public synchronized void closed( RemoteOutputStreamServer stream, boolean clean ) {
		this.status = clean ? RemoteStreamStatus.CLOSED : RemoteStreamStatus.DIRTY;
		notify();
	}
	
	@Override
	public synchronized void failure( RemoteOutputStreamServer stream, Exception exception ) {
		this.exception = exception;
		this.status = RemoteStreamStatus.FAILED;
		notify();
	}

	/**
	 * Status getter.
	 * 
	 * @return Status of the remote stream;
	 */
	RemoteStreamStatus getStatus() {
		return status;
	}

	/**
	 * Exception getter.
	 * 
	 * @return Exception thrown by the remote stream, null if none.
	 */
	Exception getException() {
		return exception;
	}
}
