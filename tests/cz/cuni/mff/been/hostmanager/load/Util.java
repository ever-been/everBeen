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

package cz.cuni.mff.been.hostmanager.load;

/**
 * Few utility methods for nicer tables.
 *
 * @author Branislav Repcek
 */
public class Util {

	/**
	 * Align value left in column of specified width.
	 * 
	 * @param <T> Type of value to align.
	 * 
	 * @param x Value that will be converted to string and aligned.
	 * @param w Width of the column.
	 * 
	 * @return String with left aligned value. Spaces are appended.
	 */
	public static < T > String alignLeft(T x, int w) {
		
		StringBuilder builder = new StringBuilder(String.valueOf(x));
		
		while (builder.length() < w) {
			builder.append(" ");
		}
		
		return builder.toString();
	}
	
	/**
	 * Align value right in column of specified width.
	 * 
	 * @param <T> Type of value to align.
	 * 
	 * @param x Value that will be converted to string and aligned.
	 * @param w Width of the column.
	 * 
	 * @return String with right aligned value. Spaces are prepended.
	 */
	public static < T > String alignRight(T x, int w) {

		StringBuilder builder = new StringBuilder();
		String s = String.valueOf(x);
		
		while (builder.length() < w - s.length()) {
			builder.append(" ");
		}
		
		return builder.append(s).toString();
	}
	
	/**
	 * Center value in column of specified width.
	 * 
	 * @param <T> Type of value to align.
	 * 
	 * @param x Value that will be converted to string and aligned.
	 * @param w Width of the column.
	 * 
	 * @return String with centered value. Spaces are added.
	 */
	public static < T > String alignCenter(T x, int w) {

		StringBuilder builder = new StringBuilder();
		String s = String.valueOf(x);
		
		while (builder.length() < (w - s.length()) / 2) {
			builder.append(" ");
		}
		
		builder.append(s);
		
		while (builder.length() < w) {
			builder.append(" ");
		}
		
		return builder.toString();
	}
	
	/**
	 * Create string containing only spaces with given length.
	 * 
	 * @param w Number of spaces in string.
	 * 
	 * @return String with given length containing only spaces.
	 */
	public static String fill(int w) {
		
		return fill(w, " ");
	}
	
	/**
	 * Create string containing multiple copies of another string.
	 * 
	 * @param w Number of copies.
	 * @param s String which is copied.
	 * 
	 * @return String containing multiple copies of <tt>s</tt>
	 */
	public static String fill(int w, String s) {
		
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < w; ++i) {
			builder.append(s);
		}
		
		return builder.toString();
	}
	
	/**
	 * Convert values in array to string with given column width and join results into a string with
	 * different values separated by specified separator string,
	 * 
	 * @param arr Array with values.
	 * @param sep Separator string.
	 * @param cw Column width.
	 * 
	 * @return String with right aligned values separated with given separator.
	 */
	public static String join(short []arr, String sep, int cw) {
		
		if (arr == null) {
			return "(null)";
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < arr.length - 1; ++i) {
			builder.append(alignRight(arr[i], cw));
			builder.append(sep);
		}
		
		if (arr.length > 0) {
			builder.append(alignRight(arr[arr.length - 1], cw));
		}
		
		return builder.toString();
	}
	
	/**
	 * Convert values in array to string with given column width and join results into a string with
	 * different values separated by specified separator string,
	 * 
	 * @param arr Array with values.
	 * @param sep Separator string.
	 * @param cw Column width.
	 * 
	 * @return String with right aligned values separated with given separator.
	 */
	public static String join(int []arr, String sep, int cw) {
		
		if (arr == null) {
			return "(null)";
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < arr.length - 1; ++i) {
			builder.append(alignRight(arr[i], cw));
			builder.append(sep);
		}
		
		if (arr.length > 0) {
			builder.append(alignRight(arr[arr.length - 1], cw));
		}
		
		return builder.toString();
	}
	
	/**
	 * Convert values in array to string with given column width and join results into a string with
	 * different values separated by specified separator string,
	 * 
	 * @param arr Array with values.
	 * @param sep Separator string.
	 * @param cw Column width.
	 * 
	 * @return String with right aligned values separated with given separator.
	 */
	public static String join(long []arr, String sep, int cw) {
		
		if (arr == null) {
			return "(null)";
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < arr.length - 1; ++i) {
			builder.append(alignRight(arr[i], cw));
			builder.append(sep);
		}
		
		if (arr.length > 0) {
			builder.append(alignRight(arr[arr.length - 1], cw));
		}
		
		return builder.toString();
	}
	
	/**
	 * Empty ctor so checkstyle does not whine at us.
	 */
	private Util() {
	}
}
