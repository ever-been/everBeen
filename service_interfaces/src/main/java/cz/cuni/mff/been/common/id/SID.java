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
 * Class representing the screen in the benchmark plugin configuration UI.
 * 
 * @author David Majda
 */
public class SID extends OID {

	private static final long	serialVersionUID	= -282177715128664255L;

	/**
	 * Allocates a new <code>SID</code> object, representing an analysis
	 * identifier given in the <code>value</code> parameter.
	 * 
	 * @param value analysis identifier to represent
	 */
	public SID(long value) {
		super(value);
	}
	
	/**
	 * Allocates a new <code>SID</code> object. Used by the
	 * <code>IDManager</code>. 
	 */
	SID() {	
	}
	
	/**
	 * Returns an <code>SID</code> object holding the value of the specified
	 * <code>String</code>.
	 * 
	 * @param s the string to be parsed
	 * @return an <code>SID</code> object holding the value represented by the
	 *          string argument
	 * @throws NumberFormatException if the <code>String</code> does not contain
	 *          a parsable <code>AID</code>
	 */
	public static SID valueOf(String s) {
		return new SID(Long.parseLong(s));
	}
}
