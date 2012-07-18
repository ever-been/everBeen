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
 * Class representing the plugin entry identifier.
 * 
 * @author Michal Tomcanyi
 * @author David Majda
 */
public class PEID extends ID {

	private static final long	serialVersionUID	= 710229502787805960L;

	public static final PEID INVALID = new PEID("");
	
	private final String pluginID;
	
	/**
	 * Allocates a new <code>PEID</code> object, representing an 
	 * unique plugin identifier given in the <code>pluginID</code> parameter.
	 * 
	 * @param pluginID experiment identifier to represent
	 */
	public PEID(String pluginID) {
		this.pluginID = pluginID;
	}

	/**
	 * Returns an <code>PEID</code> object holding the value of the specified
	 * <code>String</code>.
	 * 
	 * @param s the string to be parsed
	 * @return an <code>PEID</code> object holding the value represented by the
	 *          string argument
	 * @throws NumberFormatException if the <code>String</code> does not contain
	 *          a parsable <code>PEID</code>
	 */
	public static PEID valueOf(String s) {
		return new PEID(s);
	}
	
	/**
	 * @see ID#value()
	 */
	@Override
	public Object value() {
		return this.pluginID;
	}

	/**
	 * Compares this <code>PEID</code> to another <code>Object</code>. If the
	 * <code>Object</code> is an <code>PEID</code>, this function behaves like
	 * <code>compareTo(PEID)</code>. Otherwise, it throws a
	 * <code>ClassCastException</code> (as <code>PEID</code>s are comparable only
	 * to other <code>PEID</code>s).
	 *       
	 * @param o the <code>Object</code> to be compared.
	 * @return the value <code>0</code> if the argument is an PEID equal
	 *          to this PEID; a value less than <code>0</code>
	 *          if the argument is an PEID greater than this PEID;
	 *          and a value greater than <code>0</code> if the argument 
	 *          is an PEID less than this PEID.
	 * @throws ClassCastException if the argument is not an <code>PEID</code>.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */

	@Override
	public int compareTo(ID o) {
		return compareTo((PEID) o);
	}
	
	/**
	 * Compares two <code>PEID</code>s. Comparison algorithm simply compares 
	 * plugin IDs as Strings  
	 *       
	 * @param p the <code>PEID</code> to be compared.
	 * @return the value <code>0</code> if the argument is an PEID equal
	 *          to this PEID; a value less than <code>0</code>
	 *          if the argument is an PEID greater than this PEID;
	 *          and a value greater than <code>0</code> if the argument 
	 *          is an PEID less than this PEID.
	 */
	public int compareTo(PEID p) {
		String otherValue = (String) p.value();
		return this.pluginID.compareTo(otherValue);
	}
	

	/** 
	 * Compares this <code>PEID</code> to the specified object. The result is
	 * <code>true</code> if and only if the argument is not <code>null</code> and
	 * is an <code>PEID</code> object that represents the same version as this
	 * object.
	 * 
	 * @throws ClassCastException if the argument is not an <code>PEID</code>.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if ( o instanceof PEID ) {
			return compareTo((PEID)o) == 0;
		} else {
			return false;
		}
	}

	/**
	 * @see ID#hashCode()
	 */
	@Override
	public int hashCode() {
		return pluginID.hashCode();
	}

	/**
	 * @see ID#toString()
	 */
	@Override
	public String toString() {
		return pluginID;
	}
}
