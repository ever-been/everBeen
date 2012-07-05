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
 * Interface representing a simple property. Simple property is a leaf of the
 * property tree and has a value of some RSL-compatible type, i.e.
 * <code>Long</code>, <code>Version</code>, <code>Date</code>,
 * <code>String</code>, <code>PackageType</code> or <code>List</code> (of
 * <code>String</code>s).
 * 
 * RSL clients (modules using RSL for querying on their data) should contain a
 * class implementing this interface.
 * 
 * For description of the property tree see comment to the <code>Property</code>
 * interface.
 * 
 * @author David Majda
 */
public interface SimpleProperty extends Property {
	/**
	 * Returns a class of this property. Class should be RSL-compatible, i.e.
	 * <code>Long</code>, <code>Version</code>, <code>Date</code>,
	 * <code>String</code>, <code>PackageType</code> or <code>List</code> (of
	 * <code>String</code>s). RSL evaluation will fail, if the returned class
	 * won't be RSL-compatible.
	 * 
	 * @return class of this property
	 */
	Class< ? > getValueClass();

	/**
	 * Returns a value of this property. This value must have type returned by the
	 * <code>getValueClass</code> method. 
	 * 
	 * @return value of this property
	 */
	Object getValue();
}
