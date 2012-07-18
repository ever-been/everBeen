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
package cz.cuni.mff.been.common;

import java.io.Serializable;

/**
 * General download handle. It is used to uniquely identify the download operation.
 * 
 * @author David Majda
 */
public class DownloadHandle implements Serializable {

	private static final long	serialVersionUID	= -347970410049814113L;

	/** Next handle value. */
	private static int nextValue = 0;
    
	/** Handle value. */
	private int value;
    
	/**
	 * Factory method for creating new <code>DownloadHandle</code>s.
	 * 
	 * @return new <code>DownloadHandle</code> instance with unique value
	 */
	public static DownloadHandle createDownloadHandle() {
		return new DownloadHandle(nextValue++);
	}
    
	/**
	 * Allocates a new <code>DownloadHandle</code> object.
	 * 
	 * @param value handle value
	 */
	private DownloadHandle(int value) {
		this.value = value;
	}
    
	/** 
	 * Compares this handle to the specified object. The result is <code>true</code>
	 * if and only if the argument is not <code>null</code> and is a <code>DownloadHandle</code>
	 * object that represents the same handle as this object.
	 * 
	 * This method must be overriden because we send handles via RMI back and forth
	 * and they must be considered equal even when they are physically different
	 * objects. (By default, only the physically same objects are considered equal.)      
	 * 
	 * @throws ClassCastException if the argument is not a <code>DownloadHandle</code>.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return o instanceof DownloadHandle && value == ((DownloadHandle) o).value;
	}
    
	/**
	 * Returns the object hashcode.
	 *  
	 * This method must be overriden because we send handles via RMI back and forth
	 * and they must have the same hashcode even when they are physically different
	 * objects. (By default, hashcodes are the same only for the physically same objects.)      
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return value;
	}
}
