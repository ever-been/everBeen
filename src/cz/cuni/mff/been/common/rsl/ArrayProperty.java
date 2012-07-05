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
 * Interface representing an array property. Array property is an inner
 * node of the property tree and contains other container properties, referenced
 * by their indexes.
 * 
 * Restriction of contained items to container properties is introduced, because
 * it simplifies code in the RSL interpreter, but could be abandoned later if
 * necessary.  
 * 
 * RSL clients (modules using RSL for querying on their data) could contain a
 * class implementing this interface, if they use array properties.
 * 
 * For description of the property tree see comment of the <code>Property</code>
 * interface.
 * 
 * @author David Majda
 */
public interface ArrayProperty extends Property {
	/**
	 * Returns contained properties. 
	 * 
	 * @return contained properties
	 */
	ContainerProperty[] getItems();
}
