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

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which stores a value (of type <code>long</code>) and its unit. The unit
 * is divided into <em>unit prefix</em> and <em>unit name</em>.
 * 
 * The unit prefix is a multiplier - characters <code>'k'</code>,
 * <code>'M'</code>, <code>'G'</code>, <code>'T'</code> and <code>'P'</code>
 * mean "basic unit multiplied by 1024", "basic unit multiplied by
 * 1024<sup>2</sup>" etc. When no multiplier is used,
 * <code>NO_UNIT_PREFIX</code> costant is used as the unit prefix.
 * 
 * The unit name is arbitrary string. You can pass <code>null</code> as the unit
 * name, which means the number is unit-less. 
 * 
 * To get the value after appling the unit prefix, call method
 * <code>getValueWithAppliedPrefix</code>.
 * 
 * @author David Majda
 */
public class LongWithUnit implements Serializable, Comparable<Object> {

	private static final long	serialVersionUID	= -3774457853516386604L;

	/**
	 * Character which is used as a unit prefix to express that no unit prefix is
	 * really used.
	 */
	public static final char NO_UNIT_PREFIX = '\0';

	/** Characters denoting unit prefixes, sorted by their size. This allows:
	 *
	 * 1. Effective lookup whether given character is unit prefix:
	 *    
	 *    UNIT_PREFIX_CHARS.indexOf(ch) != -1
	 *
	 * 2. Effective determinatinon of the unit prefix size:
	 *
	 *    size = 1L << (UNIT_PREFIX_CHARS.indexOf(ch) * 10)
	 *    
	 * Note that character with ASCII code 0 represents no unit.
	 */
	private static final String UNIT_PREFIX_CHARS = NO_UNIT_PREFIX + "kMGTP";

	/** Stored value. */
	private long value;
	/**
	 * Unit prefix; one of <code>'k'</code>,<code>'M'</code>, <code>'G'</code>,
	 * <code>'T'</code>, <code>'P'</code> and <code>NO_UNIT_PREFIX</code>.
	 */ 
	private char unitPrefix;
	/** Unit name; can be <code>null</code> if no unit is specified. */
	private String unitName;
	
	
	/** @return stored value */
	public long getValue() {
		return value;
	}

	/**
	 * @return stored value after application of the prefix (i.e. after
	 * multiplying with approperiate power of 1024)
	 */
	public long getValueWithAppliedPrefix() {
		return value << (UNIT_PREFIX_CHARS.indexOf(unitPrefix) * 10);
	}

	/**
	 * @return unit prefix; one of <code>'k'</code>,<code>'M'</code>,
	 * <code>'G'</code>, <code>'T'</code>, <code>'P'</code> and
	 * <code>NO_UNIT_PREFIX</code>
	 */ 
	public char getUnitPrefix() {
		return unitPrefix;
	}

	/** @return unit name; can be <code>null</code> if no unit is specified */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * Allocates a new <code>LongWithUnit</code> object.
	 * 
	 * @param value stored value
	 * @param unitPrefix unit prefix; one of <code>'k'</code>,<code>'M'</code>,
	 *         <code>'G'</code>, <code>'T'</code>, <code>'P'</code> and
	 *         <code>NO_UNIT_PREFIX</code>
	 * @param unitName unit name; can be <code>null</code> if no unit is
	 *         specified
	 */
	public LongWithUnit(long value, char unitPrefix, String unitName) {
		if (!isUnitPrefix(unitPrefix)) {
			throw new IllegalArgumentException("Invalid unit prefix.");
		}
		
		this.value = value;
		this.unitPrefix = unitPrefix;
		this.unitName = unitName;
	}	

	/**
	 * Allocates a new <code>LongWithUnit</code> object with value, unit prefix
	 * and unit name parsed from a string. The string must match a pattern
	 * <code>^[0-9]+[kMGTP]?[a-zA-Z]*$</code>, where <code>[0-9]+</code> is the
	 * stored value, <code>[kMGTP]?</code> unit prefix and <code>[a-zA-Z]</code>
	 * the unit name. If no unit prefix is specified in the string, the
	 * <code>unitPrefix</code> attribute is set to <code>NO_UNIT_PREFIX</code>.
	 * Analogically, if no unit name is specified in the string, the
	 * <code>unitName</code> attribute is set to <code>null</code>. 
	 * 
	 * @param s string to parse the value and unit from
	 */
	public LongWithUnit(String s) {
		if (s == null) {
			throw new NullPointerException("Parameter \"s\" cannot be null.");
		}
		
		if (s.matches("^[0-9]+$")) { /* Only number, no unit. */
			value = Long.valueOf(s);
			unitPrefix = NO_UNIT_PREFIX;
			unitName = null;
		} else {
			Matcher matcher = Pattern.compile("^([0-9]+)([kMGTP]?)([a-zA-Z]*)$").matcher(s);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Parameter \"s\" has invalid format.");
			}
			
			assert matcher.groupCount() == 3: "Malformed regexp.";
			assert matcher.group(2).length() == 1 || matcher.group(2).length() == 0:
				"Malformed regexp.";
			
			value = Long.valueOf(matcher.group(1));
			unitPrefix = matcher.group(2).length() > 0
				? matcher.group(2).charAt(0)
				: NO_UNIT_PREFIX; 
			unitName = matcher.group(3).length() > 0
				? matcher.group(3)
				: null;
		}
	}
		
	/**
	 * Determines whether given character is valid unit prefix.
	 * 
	 * @param ch character to test
	 * @return <code>true</code> if the character is valid unit prefix;
	 *          <code>false</code> otherwise
	 */
	private boolean isUnitPrefix(char ch) {
		return UNIT_PREFIX_CHARS.indexOf(ch) != -1;
	}
		
	/**
	 * Determines if this object is comparable with another
	 * <code>LongWithUnit</code> object.
	 * 
	 * Two <code>LongWithUnit</code> objects are comparable iff at least one of
	 * them doesn't specify the unit name or both specify the same unit name.
	 * 
	 * @param o object to test comparability with
	 * @return <code>true</code> id this object is comparable with the other one;
	 *          <code>false</code> otherwise
	 */
	public boolean isComparableWith(LongWithUnit o) {
		return unitName == null
			|| o.unitName == null
			|| unitName.equals(o.unitName);
	}
	
	/* Comparable<LongWithUnit> methods */

  /**
   * Compares this object with another <code>LongWithUnit</code> object. That
   * object must be comparable with this object, as determined by the
   * <code>isComparableWith</code> method.
   * 
   * @param  o the <code>LongWithUnit</code> object to be compared.
   * @return a negative integer, zero, or a positive integer as this object
   *          is less than, equal to, or greater than the specified object.
   * @throws IllegalArgumentException if the other object is not comparable
   *          with this object
   *          
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object o) {
	  	if ( o instanceof LongWithUnit ) {	  
		  	LongWithUnit	olwu;
		  	
		  	olwu = (LongWithUnit) o;
			if (!isComparableWith(olwu)) {
				throw new IllegalArgumentException("The compared object has "
					+ "incompatible unit name.");
			}
			return Long.valueOf(getValueWithAppliedPrefix())
				.compareTo((olwu).getValueWithAppliedPrefix());
	  	} else {
	  		throw new IllegalArgumentException();
	  	}
	}

	/* Object methods. */

	/**
	 * Returns a hash code value for this object.
	 * 
	 * @return a hash code value for this object
	 * 
	 * @see java.lang.Object#hashCode()
s	 */
	@Override
	public int hashCode() {
		return Long.valueOf(value).hashCode() ^ unitPrefix ^ unitName.hashCode();
	}

	/** 
	 * Compares this object to another object. The result is <code>true</code>
	 * if and only if the argument is not <code>null</code> and is a
	 * <code>LongWithUnit</code> object comparable with this object and
	 * <code>compareTo</code> method applied on that object returns
	 * <code>0</code>.
	 * 
	 * @return <code>true</code> if this object is the same as the other object;
   *          <code>false</code> otherwise.
	 * @throws ClassCastException if the argument is not a
	 *          <code>LongWithUnit</code>.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return o instanceof LongWithUnit
			&& isComparableWith((LongWithUnit) o)
			&& compareTo(o) == 0;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Long.valueOf(value).toString()
			+ (unitPrefix != NO_UNIT_PREFIX ? unitPrefix : "")
			+ (unitName != null ? unitName : "");
	}
}
