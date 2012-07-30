/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.webinterface.ref;

import java.rmi.Remote;
import java.util.Date;

import cz.cuni.mff.been.common.ComponentInitializationException;

/**
 * Abstract class encpsulating reference to the remote interface and handling
 * its renewal. Is is parametrized by the type of the remote interface.
 * 
 * Descendants are expected to implement the <code>acquire</code> method, which
 * acquires the reference.
 * 
 * @param <T> remote interface type
 * 
 * @author David Majda
 */
public abstract class RMIReference<T extends Remote> {
	/** Time (in seconds), after which the reference is not considered valid. */
	private static final int TIMEOUT = 1;
	/** Number of miliseconds in one second. */
	private static final int MILISECONDS_IN_SECOND = 1000;
	
	/** Reference to the encapsulated RMI interface. */
	private T reference;
	/**
	 * Time (number of milliseconds since January 1, 1970, 00:00:00 GMT) when
	 * current encapsulated reference was acquired.
	 */
	private long acquireTime;
	
	/**
	 * Checks if the encapsulated reference is valid.
	 * 
	 * @return <code>true</code> if the reference is valid;
	 *          <code>false</code> otherwise
	 */
	private boolean referenceIsValid() {
		return reference != null 
			&& acquireTime + TIMEOUT * MILISECONDS_IN_SECOND >= new Date().getTime();
	}
	
	/**
	 * Returns the encapsulated reference, acquiring it if needed (when calling
	 * the method for the first time, or after the reference expiration).
	 * 
	 * @return ecapsulated reference
	 * @throws ComponentInitializationException if acquiring of the reference
	 *                                           failed
	 */
	public T get() throws ComponentInitializationException {
		if (referenceIsValid()) {
			return reference;
		} else {
			reference = acquire();
			acquireTime = new Date().getTime();
			return reference;
		}
	}
	
	/**
	 * Calls the <code>get</code> method and returns <code>true</code> if it
	 * returns valid reference. This allows to check if we have valid encapsulated
	 * reference without explicitly getting it and catching
	 * <code>ComponentInitializationException</code>. 
	 * 
	 * @return <code>true</code> if the <code>get</code> mathod returns a
	 *          valid encapsulated reference;
	 *          <code>false</code> if it does not
	 */
	public boolean hasReference() {
		try {
			get();
			return true;
		} catch (ComponentInitializationException e) {
			return false;
		}
	}
	
	/**
	 * Drops the encapsulated reference.
	 */
	public void drop() {
		reference = null;
	}
	
	/**
	 * Acquires the encapsulated reference.
	 * 
	 * @return newly acquired reference
	 * @throws ComponentInitializationException if acquiring of the reference
	 *                                           failed
	 */
	public abstract T acquire() throws ComponentInitializationException;
}
