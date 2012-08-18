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
package cz.cuni.mff.been.common.rsl;

/**
 * Interface representing a container property. Container property is an inner
 * node of the property tree and contains other properties, referenced by
 * their names.
 * 
 * Also, root of the property tree is expressed as a container property for
 * consistency.
 * 
 * RSL clients (modules using RSL for querying on their data) should contain a
 * class implementing this interface.
 * 
 * For description of the property tree see comment to the <code>Property</code>
 * interface.
 * 
 * @author David Majda
 */
public interface ContainerProperty extends Property {
	/**
	 * Tests if the property with given property name exists in this container
	 * property. 
	 * 
	 * @param propertyName property name
	 * @return <code>true</code> if property identified by given name exists;
	 *          <code>false</code> otherwise
	 */
	boolean hasProperty(String propertyName);
	
	/**
	 * Returns the property with given name. Implementors of the property tree
	 * should guarantees that this method will only be called with valid property
	 * name (as determined by <code>hasProperty</code> method).
	 * 
	 * @param propertyName property name
	 * @return property with given name
	 */
	Property getProperty(String propertyName);
}
