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
 * Class representing the analysis identifier.
 * 
 * @author David Majda
 * @author Jiri Tauber
 */
public class AID extends OID {

	private static final long	serialVersionUID	= -6800755098749041685L;

	private static final char SEPARATOR_CHAR = '_';

	private String name = null;

	/**
	 * Allocates a new <code>AID</code> object, representing an analysis
	 * identifier given in the <code>value</code> parameter.
	 * 
	 * @param value analysis identifier to represent
	 */
	public AID(long value, String name) {
		super(value);
		this.name = name;
	}
	
	/**
	 * Allocates a new <code>AID</code> object. Used by the
	 * <code>IDManager</code>. 
	 */
	AID() {	
	}

	/**
	 * Sets the name of analysis in this identifier
	 * 
	 * @param name the analysis name
	 */
	void setName( String name ){
		this.name = name;
	}

	/**
	 * @return he analysis name
	 */
	public String getName(){
		return name;
	}

	/**
	 * Returns an <code>AID</code> object holding the value of the specified
	 * <code>String</code>.
	 * 
	 * @param s the string to be parsed
	 * @return an <code>AID</code> object holding the value represented by the
	 *          string argument
	 * @throws NumberFormatException if the <code>String</code> does not contain
	 *          a parsable <code>AID</code>
	 */
	public static AID valueOf(String s) {
		int i = s.lastIndexOf(SEPARATOR_CHAR);
		String name = s.substring(0, i);
		long id = Long.parseLong(s.substring(i));
		return new AID(id, name);
	}
	
	/**
	 * @param id identifier of analysis
	 * @return	string AID(id)
	 */
	public static String toNamedString(AID id) {
		return "AID(" + id + ")";
	}

	@Override
	public String toString(){
		return name+SEPARATOR_CHAR+super.toString();
	}
}
