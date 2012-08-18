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

package cz.cuni.mff.been.common.value;

import java.io.Serializable;

import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;


/**
 * Interface for query related storage classes for basic data types (int, boolean...).
 *
 * @param <T> Type of object that instance of class implementing this interface will contain.
 *
 * @author Branislav Repcek
 */
public interface ValueBasicInterface< T extends ValueBasicInterface< T > >
	extends ValueCommonInterface, Comparable< T >, XMLSerializableInterface, Serializable {

	/**
	 * Test whether this object is greater than given object.
	 * 
	 * @param vb Object to test.
	 * @return <code>true</code> if current object is greater than <code>vb</code>, <code>false</code>
	 *         otherwise. 
	 */
	boolean greaterThan(Object vb);
	
	/**
	 * Test whether this object is less than given object.
	 * 
	 * @param vb Object to test.
	 * @return <code>true</code> if current object is less than <code>vb</code>, <code>false</code>
	 *         otherwise. 
	 */
	boolean lessThan(Object vb);
	
	/**
	 * Get unit in which value of the object is measured. Note that units are not serialised or 
	 * deserialised with the value and they are not used when comparing values.
	 * 
	 * @return Short name for the unit or <code>null</code> if unit is not applicable to the value.
	 */
	String getUnit();
}
