/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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

/**
 * Provides some miscelaneous utility methods for arrays.
 * 
 * @author Jaroslav Urban
 */
public class ArrayUtils {
	
	private ArrayUtils() {
		// instantiation not available
	}
	
	
	/**
	 * Creates assembles String representations of members of an array into
	 * one big String, separated by a token.
	 * @param token
	 * @param objects
	 * @return one big string containing String representations of the members
	 * of the array separated by the token.
	 */
	public static String join( String token, Object[] objects ) {
		StringBuilder sb = new StringBuilder();
		
		if (objects.length > 0) {
			sb.append(objects[0]);
			for (int i = 1; i < objects.length; ++i) {
				sb.append(token);
				sb.append(objects[i]);
			}
		}
		return sb.toString();
	}

	/**
	 * Reverts order of arrays elements
	 * @param <T>
	 * @param b
	 */
	public static <T> void reverse(T[] b) {
		T	temp;
		int left = 0;
		int right = 0;
		for (left = 0, right = b.length - 1; left < right; ++left, --right) {
			temp = b[left]; 
			b[left] = b[right]; 
			b[right] = temp;
		}
	}
}
