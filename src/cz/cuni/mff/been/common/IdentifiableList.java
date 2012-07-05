/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Michal Tomcanyi
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

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.been.common.id.ID;
import cz.cuni.mff.been.common.id.Identifiable;

/**
 * Class represents a list storing objects that carry unique IDs. Provides some utility methods for searching by IDs.
 * 
 * @author Michal Tomcanyi
 */
public class IdentifiableList< T extends ID > extends LinkedList< Identifiable< T > > {

	private static final long	serialVersionUID	= 3431609334630421762L;

	/**
	 * Searches for an object stored in the list carrying specific ID.
	 * If objects in the list do not have unique IDs, then the first object
	 * carrying the ID is returned.
	 * 
	 * @param id	to search for
	 * @return	object carrying provided ID or <code>null</code> when no matches
	 */
	public Identifiable< T > findByID(T id) {
		for (Identifiable< T > item : this) {
			if (item.getID().equals(id)) {
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * @return (Multi)set of IDs available in the list 
	 */
	public List< T > getIDs() {
		
		LinkedList< T > result = new LinkedList< T >();
		
		for (Identifiable< T > item : this) {
			result.add(item.getID());
		}
		
		return result;
		
	}
}
