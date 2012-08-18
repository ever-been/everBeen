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
 * Interface for compound classes - classes which can contain other basic types. This should be used
 * for things like list, range, etc.
 *
 * @param <T> Type of the elements of the compound. This type has to extend ValueBasicInterface< T >.
 *
 * @author Branislav Repcek
 */
public interface ValueCompoundInterface< T extends ValueBasicInterface< ? > > 
	extends ValueCommonInterface, XMLSerializableInterface, Serializable {
	
	/**
	 * Test whether given value is contained within compound. <code>compareTo()</code> and <code>equals()</code> 
	 * methods from <code>ValueBasicInterface</code> are used to test values.
	 * 
	 * @param vb Value to test against compound.
	 * 
	 * @return <code>true</code> if given value is contained in compound, <code>false</code> otherwise.
	 */
	boolean contains(T vb);
}
