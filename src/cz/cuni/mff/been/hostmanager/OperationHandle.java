/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager;

import java.io.Serializable;

/**
 * This handle is returned by every method that adds/refreshes hosts. It can be used to keep 
 * track of results of asynchronous operations. 
 *
 * @author Branislav Repcek
 */
public class OperationHandle implements Serializable {
	
	private static final long	serialVersionUID	= -3031451466758110149L;

	/**
	 * Handle.
	 */
	private long handle;
	
	/**
	 * Default constructor.
	 */
	public OperationHandle() {
		
		handle = 0;
	}
	
	/**
	 * Create handle with given value.
	 * 
	 * @param handle Value of the handle.
	 */
	private OperationHandle(long handle) {
		
		this.handle = handle;
	}
	
	/**
	 * Get handle next to current one.
	 * 
	 * @return Next handle.
	 */
	public OperationHandle getNext() {
		
		return new OperationHandle(handle + 1);
	}
	
	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		
		if (other instanceof OperationHandle) {
			return handle == ((OperationHandle) other).handle;
		} else {
			return false;
		}
	}
	
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		return (int) (handle ^ (handle >>> 32));
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return Long.toString(handle);
	}
	
	/**
	 * Returns an <code>OperationHandle</code> object holding the value of the specified <code>String</code>.
	 * 
	 * @param s the string to be parsed
	 * @return an <code>OperationHandle</code> object holding the value represented by the string argument.
	 * @throws NumberFormatException If the <code>String</code> cannot be parsed as 
	 *         <code>OperationHandle</code>.
	 */
	public static OperationHandle valueOf(String s) {

		return new OperationHandle(Long.valueOf(s));
	}	
}
