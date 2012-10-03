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
package cz.cuni.mff.been.common;

import java.io.Serializable;

import cz.cuni.mff.been.utils.ArrayUtils;

/**
 * Class representing a version of something. It allows parsing the version
 * from string, intelligent manipulation with versions (especially comparing)
 * and writing them out as strings.
 * 
 * Internally, versions are stored as array of strings representing
 * version parts (e.g. "5.1" is stored as array containing elements "5"
 * and "1").  
 *
 * @author David Majda
 */
public class Version implements Cloneable, Comparable< Object >, Serializable {
	
	private static final long	serialVersionUID	= 6215665807083287584L;

	/**
	 * Separator of the version parts.
	 */
	private static final String PART_SEPARATOR = ".";
	/**
	 * Separator of the version parts, written as a regular expression.
	 */
	private static final String PART_SEPARATOR_REGEX = "\\.";
	/**
	 * String used in comparisons in case of missing version part in one of
	 * compared versions.
	 */
	private static final String ZERO_PART = "0";
	
	/**
	 * Version parts.
	 */
	private String[] parts = {};
	
	/**
	 * Allocates a new <code>Version</code> object, without any version
	 * represented. Shoulnd't be used. 
	 */
	public Version() {
		super();
	}
	
	/**
	 * Allocates a new <code>Version</code> object, representing a version
	 * given in the <code>s</code> parameter.
	 *  
	 * @param s version to represent 
	 */
	public Version(String s) {
		super();
		valueOf(s);
	}
	
	/** @return part count */
	public int partCount() {
		return parts.length;
	}
	
	/**
	 * @param index part index
	 * @return index-th part
	 */
	public String getPart(int index) {
		return parts[index];
	}
	
	/**
	 * Compares this Version to another Object. If the Object is a Version,
	 * this function behaves like <code>compareTo(Version)</code>. Otherwise,
	 * it throws a <code>ClassCastException</code> (as <code>Versions</code>
	 * are comparable only to other <code>Versions</code>).
	 *       
	 * @param o the <code>Object</code> to be compared.
	 * @return the value <code>0</code> if the argument is a version equal
	 *          to this version; a value less than <code>0</code>
	 *          if the argument is a version greater than this version;
	 *          and a value greater than <code>0</code> if the argument 
	 *          is a version less than this version.
	 * @throws ClassCastException if the argument is not a <code>Version</code>.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		
		return compareTo((Version) o);
	}
	
	/**
	 * Compares two versions. Comparison algorithm is this:
	 * <ol>
	 *   <li>If number of parts in one of the compared versions is smaller than
	 *       in the other version, add necessary number of parts containing
	 *       string "0" to the end of one version, so the number of parts
	 *       in both versions is equal.</li>
	 *   <li>Compare version parts form the left as strings. If they aren't equal,
	 *       return the string comparison return value, otherwise continue with
	 *       the next part.</li>
	 *   <li>If all parts are equal, return <code>0</code>.</li> 
	 * </ol>      
	 *       
	 * @param anotherVersion the <code>Version</code> to be compared.
	 * @return the value <code>0</code> if the argument is a version equal
	 *          to this version; a value less than <code>0</code>
	 *          if the argument is a version greater than this version;
	 *          and a value greater than <code>0</code> if the argument 
	 *          is a version less than this version.
	 */
	public int compareTo(Version anotherVersion) {
		int maxPartCount = Math.max(parts.length, anotherVersion.parts.length);
		for (int i = 0; i < maxPartCount; i++) {
			int result = getPartOrZero(i).compareTo(anotherVersion.getPartOrZero(i));
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}
	
	/** 
	 * Compares this version to the specified object. The result is <code>true</code>
	 * if and only if the argument is not <code>null</code> and is a <code>Version</code>
	 * object that represents the same version as this object.
	 * 
	 * @throws ClassCastException if the argument is not a <code>Version</code>.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return o instanceof Version && compareTo(o) == 0;
	}
	
	/**
	 * Returns a hash code value for this object. Because comparing version
	 * is a bit tricky (versions like "1.5", "1.5.0" and "1.5.0.0" are all
	 * equal), we use following algorithm to compute a hash code:
	 * <ol>
	 *   <li>Strip all parts with value <code>ZERO_PART</code> ("0") from the end
	 *       of the version.
	 *   <li>Set the initial hash code to <code>0</code> and iterate remaining
	 *       parts of the version. In each step, xor current hash code with the
	 *       hash code of current part.</li>
	 *   <li>Return final hash code.</li> 
	 * </ol>
	 *       
	 * Hash code computed by this algorithm maintains the general contract
	 * for the <code>hashCode</code> method, which states that equal objects
	 * must have equal hash codes.
	 * 
	 * @return a hash code value for this object
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 0;
		int i = parts.length - 1;
		while (parts[i].equals(ZERO_PART)) {
			i--;
		}
		while (i >= 0) {
			result ^= parts[i].hashCode();
			i--;
		}
		return result;
	}
	
	/**
	 * Parses the version from a string.
	 *  
	 * @param s string to parse the version from
	 */
	public void valueOf(String s) {
		parts = s.split(PART_SEPARATOR_REGEX);
	}
	
	/** 
	 * @return string representation of the version
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ArrayUtils.join(PART_SEPARATOR, parts);
	}
	
	/**
	 * Returns version part with specified index. If the index is too large, returns
	 * string "0". This behaviour is useful for comparing versions.
	 * 
	 * @param index part index
	 * @return version part with specified index or string "0" if the index is
	 *          too large
	 */
	private String getPartOrZero(int index) {
		if (index < parts.length) {
			return parts[index];
		} else {
			return ZERO_PART;
		}
	}
}
