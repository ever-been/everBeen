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

package cz.cuni.mff.been.hostmanager.value;

import java.io.Serializable;

import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;

/**
 * Common ancestor to all query related storage classes for Host Manager.
 *
 * @author Branislav Repcek
 */
public interface ValueCommonInterface extends Serializable, XMLSerializableInterface {
	
	/**
	 * Convert value to string.
	 * 
	 * @see java.lang.Object#toString() 
	 */
	String toString();	
	
	/**
	 * Test whether other object is equal to this one.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object) 
	 */
	boolean equals(Object o);
	
	/**
	 * Calculate has code of current object.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	int hashCode();
	
	/**
	 * Type getter. Much simpler than {@code instanceof} and the like.
	 * 
	 * @return Type of the value.
	 */
	public ValueType getType();
	
	/**
	 * Type getter. Returns type of the element. Sometimes the type can't be guessed at runtime.
	 * For example, empty lists and unbounded ranges don't have any run-time type information.
	 * However, nulls should be compatible with just about any runtime type, so this should not
	 * be a problem.
	 * 
	 * @return getType() for basic types, element type for compound types, or null.
	 */
	public ValueType getElementType();
}
