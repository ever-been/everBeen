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
package cz.cuni.mff.been.common.id;

import java.io.Serializable;

/**
 * General abstract class representing unique identifier of some entity.
 * The value of the identifier can be of any type and is returned by the
 * <code>value</code> method.
 * 
 * Every unique identifier represented by some subclass of this class is
 * expected to be serializable to <code>String</code> via standard methods
 * <code>toString</code> and <code>valueOf</code>. This requirement is to
 * allow the web interface to put these identifiers to the HTML forms.
 * 
 * @author Michal Tomcanyi
 * @author David Majda
 */
public abstract class ID implements Serializable, Comparable< ID > {

	private static final long	serialVersionUID	= -2170459861926071612L;

	/**
	 * Returns the value of the unique identifier. Because the value can be
	 * implemented as any type, method returns general class <code>Object</code>. 
	 * 
	 * @return returns value of the unique identifier
	 */
	public abstract Object value();

	/**
	 * Compares this ID with the specified object for order. Returns a negative 
	 * integer, zero, or a positive integer as this object is less than, equal
	 * to, or greater than the specified ID.
	 *       
	 * @param o the <code>Object</code> to be compared
	 * @return a negative integer, zero, or a positive integer as this ID is less
	 *          than, equal to, or greater than the specified object
	 * @throws ClassCastException if the argument is not an ID representing the
	 *          same type of entity
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public abstract int compareTo( ID o );

	/** 
	 * Compares this ID to the specified object. The result is <code>true</code>
	 * if and only if the argument is not <code>null</code> and is an <code>ID</code>
	 * object that represents the same entity as this object.
	 * 
	 * @throws ClassCastException if the argument is not an ID representing the
	 *          same type of entity
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public abstract boolean equals(Object o);
	
	/**
	 * Returns a hash code value for this object.
	 * 
	 * @return a hash code value for this object
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public abstract int hashCode();

	/** 
	 * Returns a string representation of the identifier.
	 *  
	 * @return string representation of the identifier
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();
	
	/**
	 * Returns an <code>ID</code> object holding the value of the specified
	 * <code>String</code>.
	 * 
	 * @param s the string to be parsed
	 * @return an <code>ID</code> object holding the value represented by the
	 *          string argument
	 */
	public static ID valueOf(String s) {
		return null; // can't be static and abstract in one time
	}
}
