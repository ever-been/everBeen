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
 * Class representing the binary identifier.
 * 
 * @author David Majda
 */
public class BID extends OID {

	private static final long	serialVersionUID	= -3282794258582256651L;

	/**
	 * Allocates a new <code>BID</code> object, representing an binary
	 * identifier given in the <code>value</code> parameter.
	 * 
	 * @param value binary identifier to represent
	 */
	public BID(long value) {
		super(value);
	}

	/**
	 * Allocates a new <code>BID</code> object. Used by the
	 * <code>IDManager</code>. 
	 */
	BID() {	
	}

	/**
	 * Returns an <code>BID</code> object holding the value of the specified
	 * <code>String</code>.
	 * 
	 * @param s the string to be parsed
	 * @return an <code>BID</code> object holding the value represented by the
	 *          string argument
	 * @throws NumberFormatException if the <code>String</code> does not contain
	 *          a parsable <code>BID</code>
	 */
	public static BID valueOf(String s) {
		return new BID(Long.parseLong(s));
	}
}
