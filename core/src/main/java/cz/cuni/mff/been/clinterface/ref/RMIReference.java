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
package cz.cuni.mff.been.clinterface.ref;

import java.rmi.Remote;
import java.util.Date;

import cz.cuni.mff.been.common.ComponentInitializationException;

/**
 * Abstract class encpsulating a reference to a remote interface and handling its renewal.
 * Is is parametrized by the type of the remote interface. Descendants are expected to implement
 * the <code>acquire</code> method, which acquires the reference.
 * 
 * @param <T> remote interface type
 * 
 * @author David Majda
 * @author Andrej Podzimek
 */
public abstract class RMIReference< T extends Remote > {
	
	/** Time [miliseconds], after which the reference is not considered valid. */
	private static final int TIMEOUT = 1000;
	
	/** A reference to the encapsulated RMI interface. */
	private T reference;
	
	/** Time when the current reference needs to be reacquired. */
	private long deadline;
	
	/**
	 * Returns the encapsulated reference, acquiring it if needed.
	 * 
	 * @return An up-to-date reference, no older than {@code TIMEOUT} miliseconds.
	 * @throws ComponentInitializationException When acquiring is needed and fails.
	 */
	public T get() throws ComponentInitializationException {
		final long currentTime;
		
		currentTime = new Date().getTime();
		if ( deadline < currentTime ) {
			reference = acquire();
			deadline = currentTime + TIMEOUT;
			return reference;
		}
		return reference;
	}
	
	/**
	 * Drops the encapsulated reference.
	 */
	public void drop() {
		deadline = 0;
	}
	
	/**
	 * Acquires the encapsulated reference.
	 * 
	 * @return An up-to-date (reacquired) reference.
	 * @throws ComponentInitializationException When acquiring fails.
	 */
	public abstract T acquire() throws ComponentInitializationException;
}
