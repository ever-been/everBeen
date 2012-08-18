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
 * Class representing the benchmark run identifier. In the web interfeace, user
 * can run more benchmarks in one session (e.g. open more web browser windows,
 * and click through the wizards in both of them). This means that we need more
 * instances of objects capturing the state (<code>BenchmarkRunState</code>)
 * and they must be identified - and this is accomplished by this identifier.
 * 
 * @author David Majda
 */
public class BRID extends OID {

	private static final long	serialVersionUID	= 7504776240872130887L;

	/**
	 * Allocates a new <code>BRID</code> object, representing an benchmark run
	 * identifier given in the <code>value</code> parameter.
	 * 
	 * @param value analysis identifier to represent
	 */
	public BRID(long value) {
		super(value);
	}
	
	/**
	 * Allocates a new <code>BRID</code> object. Used by the
	 * <code>IDManager</code>. 
	 */
	BRID() {
	}
	
	/**
	 * Returns an <code>BRID</code> object holding the value of the specified
	 * <code>String</code>.
	 * 
	 * @param s the string to be parsed
	 * @return an <code>BRID</code> object holding the value represented by the
	 *          string argument
	 * @throws NumberFormatException if the <code>String</code> does not contain
	 *          a parsable <code>BRID</code>
	 */
	public static BRID valueOf(String s) {
		return new BRID(Long.parseLong(s));
	}
}
