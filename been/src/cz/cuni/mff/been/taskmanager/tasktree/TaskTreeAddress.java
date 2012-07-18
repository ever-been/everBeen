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
package cz.cuni.mff.been.taskmanager.tasktree;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * This is how tree addresses are visible from outside this package.
 * 
 * @author Andrej Podzimek
 */
public final class TaskTreeAddress implements Serializable, Comparable< TaskTreeAddress > {
	
	private static final long	serialVersionUID	= -8286134727311710593L;
	
	/** The hash code to look up the address in the task tree. */
	private final long longHashCode;
	
	/**
	 * Initializes a new address reference with the given hash code.
	 * 
	 * @param longHashCode A hash code pointing at a task tree address.
	 */
	TaskTreeAddress( long longHashCode ) {
		this.longHashCode = longHashCode;
	}
	
	/**
	 * Hash code getter.
	 * 
	 * @return A unique hash code representing this address path.
	 * @throws RemoteException
	 */
	public long longHashCode() {
		return longHashCode;
	}
	
	@Override
	public int compareTo( TaskTreeAddress other ) {
		return longHashCode < other.longHashCode ? -1 : longHashCode > other.longHashCode ? 1 : 0;
	}
	
	@Override
	public boolean equals( Object other ) {
		if ( null == other ) {
			return false;
		}
		if ( other.getClass() == TaskTreeAddress.class ) {											// Faster than instanceof.
			return longHashCode == ( (TaskTreeAddress) other ).longHashCode;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return ( (int) longHashCode ) ^ ( (int) ( longHashCode >> Integer.SIZE ) ); 
	}
}
