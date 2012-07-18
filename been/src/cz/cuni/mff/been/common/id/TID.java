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

/**
 * Class representing the task identifier.
 *
 * TOOD Tomcanyi: more comments about purpose of this class.
 * 
 * @author Michal Tomcanyi
 * @author David Majda
 */
public class TID extends OID {

	private static final long	serialVersionUID	= 1557050362239276802L;

	/**
	 * Allocates a new <code>TID</code> object, representing task
	 * identifier given in the <code>value</code> parameter.
	 * 
	 * @param value analysis identifier to represent
	 */
	public TID(long value) {
		super(value);
	}
	
	/**
	 * Allocates a new <code>TID</code> object. Used by the
	 * <code>IDManager</code>. 
	 */
	TID() {
	}
	
	/**
	 * Returns an <code>TID</code> object holding the value of the specified
	 * <code>String</code>.
	 * 
	 * @param s the string to be parsed
	 * @return an <code>AID</code> object holding the value represented by the
	 *          string argument
	 * @throws NumberFormatException if the <code>String</code> does not contain
	 *          a parsable <code>TID</code>
	 */
	public static TID valueOf(String s) {
		return new TID(Long.parseLong(s));
	}
}
