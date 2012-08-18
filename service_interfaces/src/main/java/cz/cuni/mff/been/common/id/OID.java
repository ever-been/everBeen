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
 * Class representing unique identifier of some entity. This identifier has
 * type long.
 * 
 * @author Michal Tomcanyi
 * @author David Majda
 */
public class OID extends ID {

	private static final long	serialVersionUID	= 8617377210883115756L;

	/** Invalid OID. */
	public static final OID INVALID = new OID(-1);
	
	/** Value of the unique identifier. */
	private long value;
	
	/**
	 * Allocates a new <code>OID</code> object, representing an ID given in the 
	 * <code>value</code> parameter.
	 * 
	 * @param value unique ID to represent
	 */
	public OID(long value) {
		this.value = value;
	}
	
	/**
	 * Allocates a new <code>OID</code> object. Used by the
	 * <code>IDManager</code>. 
	 */
	OID() {		
	}
	
	/**
	 * Sets the value of the unique identifier.
	 *
	 * @param value value of the unique identifier to set
	 */
	void setValue(long value) {
		this.value = value;
	}
	
	/**
	 * Returns the value of te unique identifier.
	 * 
	 * @return value of te unique identifier
	 */
	@Override
	public Object value() {
		return new Long(value);
	}

	/**
	 * Compares this <code>OID</code> to another <code>Object</code>. If the
	 * <code>Object</code> is an <code>OID</code>, this function behaves like
	 * <code>compareTo(OID)</code>. Otherwise, it throws a
	 * <code>ClassCastException</code> (as <code>OID</code>s are comparable only
	 * to other <code>OID</code>s).
	 *       
	 * @param o the <code>Object</code> to be compared.
	 * @return the value <code>0</code> if the argument is an OID equal
	 *          to this OID; a value less than <code>0</code>
	 *          if the argument is an OID greater than this OID;
	 *          and a value greater than <code>0</code> if the argument 
	 *          is an OID less than this OID.
	 * @throws ClassCastException if the argument is not an <code>OID</code>.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ID o) {
		return compareTo((OID) o);
	}

	/**
	 * Compares two <code>OID</code>s. Comparison algorithm simply compares the
	 * values numerically.      
	 *       
	 * @param anotherOID the <code>OID</code> to be compared.
	 * @return the value <code>0</code> if the argument is an OID equal
	 *          to this OID; a value less than <code>0</code>
	 *          if the argument is an OID greater than this OID;
	 *          and a value greater than <code>0</code> if the argument 
	 *          is an OID less than this OID.
	 */
	public int compareTo(OID anotherOID) {
		/* Because the values are longs and we return int, we can't do the usual
		 * trick "return value - anotherOID.value;" without risk of overflow. 
		 */
		long diff = value - anotherOID.value;
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	/** 
	 * Compares this <code>OID</code> to the specified object. The result is
	 * <code>true</code> if and only if the argument is not <code>null</code>,
	 * is the same type as <code>this</code> and carries the same numeric value
	 * as <code>this</code> object.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		/*
		 * This is not sufficient:  
		 * return o instanceof OID && compareTo(o) == 0;
		 * 
		 * In such case (EID uses OID.equals()): OID(1).equals(EID(1)) == true
		 * 
		 * I used dynamic version of 'instanceof' operator Class.isInstance()
		 * so that equals() method does not need to be redefined on each
		 * extension of OID object
		 */
		if (o == null) {
			return false;
		}
		
		Class< ? > objectClass = o.getClass();
		Class< ? > thisClass = getClass();
		if ( objectClass.isInstance(this) && thisClass.isInstance(o) ) {
			return compareTo((ID)o) == 0;
		} else {
			return false;
		}
	}

	/**
	 * Returns a hash code value for this object. We simply compute the hash code
	 * of the represented value.
	 * 
	 * @return a hash code value for this object
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new Long(value).hashCode();
	}

	/** 
	 * Returns a string representation of the identifier.
	 *  
	 * @return string representation of the identifier
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Long.toString(value);
	}

	/**
	 * Returns an <code>OID</code> object holding the value of the specified
	 * <code>String</code>.
	 * 
	 * @param s the string to be parsed
	 * @return an <code>OID</code> object holding the value represented by the
	 *          string argument
	 * @throws NumberFormatException if the <code>String</code> does not contain a parsable <code>OID</code>
	 */
	public static OID valueOf(String s) {
		return new OID(Long.parseLong(s));
	}
	
}
