/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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
import java.util.Map.Entry;

/**
 * Class which enables you to pair two objects inside one.
 * 
 * @param <T1> Type of the first element of the pair.
 * @param <T2> Type of the second element of the pair.
 *
 * @author Branislav Repcek
 */
public class Pair< T1, T2 > implements Serializable, Entry< T1, T2 > {
	
	private static final long	serialVersionUID	= -1088238695088779918L;

	/**
	 * First element of the pair.
	 */
	private T1 first;
	
	/**
	 * Second element of the pair.
	 */
	private T2 second;
	
	/**
	 * Create empty pair. Elements will be set to null.
	 */
	public Pair() {
		
		first = null;
		second = null;
	}
	
	/**
	 * Create new instance of pair.
	 * 
	 * @param newFirst Value of the first element.
	 * @param newSecond Value of the second element.
	 */
	public Pair(T1 newFirst, T2 newSecond) {
		
		first = newFirst;
		second = newSecond;
	}
	
	/**
	 * A convenience method that can create pairs based on operand types.
	 * 
	 * @param <S1> Type of the first pair item.
	 * @param <S2> Type of the second pair item.
	 * @param first The first pair item.
	 * @param second The second pair item.
	 * @return A pair of the required type consisting of the supplied items.
	 */
	public static < S1, S2 > Pair< S1, S2 > pair( S1 first, S2 second ) {
		return new Pair< S1, S2 >( first, second );
	}
	
	/**
	 * Get value of the first element.
	 * 
	 * @return Value of the first element.
	 */
	public T1 getKey() {
		
		return first;
	}
	
	/**
	 * Get value of the second element.
	 * 
	 * @return Value of the second element of the pair. 
	 */
	public T2 getValue() {
		
		return second;
	}
	
	/**
	 * Convert value to the string. Resulting string will have this format:<br>
	 * <code>[first, second]</code><br>
	 * where <code>first</code> (<code>second</code>) are values of the first (second) elements of the pair.
	 */
	@Override
	public String toString() {
		
		String s1 = first == null ? "null" : first.toString();
		String s2 = second == null ? "null" : second.toString();
		
		return "[" + s1 + ", " + s2 + "]";
	}
	
	/**
	 * Calculate hash code of the object. Hash is calculated from the string representation of the pair. 
	 */
	@Override
	public int hashCode() {

		return first.hashCode() + second.hashCode();
	}

	/**
	 * Test for equality. Two pair object are equal only if both elements of both objects are equal.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof Pair) { 
			return equals((Pair< T1, T2 >) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Test for equality. Two pair object are equal only if both elements of both objects are equal.
	 * 
	 * @param o Other object to test.
	 *  
	 * @return <code>true</code> if both Pairs have same values, <code>false</code> otherwise. 
	 */
	public boolean equals(Pair< T1, T2 > o) {
		
		return (first.equals(o.getKey())) && (second.equals(o.getValue())); 
	}
	
	/**
	 * Set value of the first element in the pair.
	 * 
	 * @param first New value of the first element in the pair.
	 */
	public void setKey(T1 first) {
		
		this.first = first;
	}
	
	/**
	 * Set value of the second element in the pair.
	 * 
	 * @param second New value of the second element in the pair.
	 */
	public T2 setValue(T2 second) {
		T2 oldSecond;
		
		oldSecond = this.second;
		this.second = second;
		return oldSecond;
	}
}
