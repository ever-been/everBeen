/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Michal Tomcanyi
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtils {
    
    public static boolean isEmpty(String s) {
    	// return s == null || s.length() == 0;
    	return s == null ? true : ( s.length() == 0 );
    }
    public static boolean isTrimedEmpty(String s) {
    	// return s == null || s.trim().length() == 0;
    	return s == null ? true : ( s.trim().length() == 0 );
    }
    /**
     * @param value
     */
    public static String toString(Object value) {
        return value != null ? value.toString() : "null";
    }
    /**
     * @param value
     */
    public static String toNullString(Object value) {
        return value != null ? value.toString() : null;
    }
    /**
     * @param value
     */
    public static String toNotNullString(Object value) {
        return value != null ? value.toString() : "";
    }

    /**
     * Fills String object with contents retrieved from input stream
     * @param is	InputStream with result contents
     * @return string filled in by contents of <code>is</code>
     */
    public static String streamToString(InputStream is) {
    	try {
    		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    		StringBuffer sb = new StringBuffer(is.available());
    		while (rd.ready()) {
    			sb.append(rd.readLine());
    		}
    		return sb.toString();
    	} catch (IOException e) {
    		return null;
    	}
    }
    
    /**
     * Method strips leading path separator from the input <code>path</code>.
     * If no leading separator is found the string is kept intact.
     * Method does ignores current platform, removes "\\" as well as "/" separators.
     * @param path - Input path string to strip
     * @return 
     * 		stripped path	if leading separator is found
     * 		original path	if path is empty/null or separator is not found		
     */
    public static String stripPathSeparator(String path) {
    	
    	// empty string
    	if (isEmpty(path)) {
    		return path;
    	}
    	
    	// unix-style 
    	if (path.charAt(0) == '/') {
    		if (path.length() == 1) {
    			return "";
    		} else {
    			return path.substring(1);
    		}
    	}
    	
    	if (path.charAt(0) == '\\') {
    		if (path.length() == 1) {
    			return "";
    		} else {
    			return path.substring(1);
    		}
    	}
    	
    	// no path separator found
    	return path;
    	
    }

    /**
     * Converts first character of string to uppercase, the rest is left unchanged.
     * If string is empty, it is unchanged.
     * @param s	String to modify
     * @return Converted string
     */
    public static String firstCharToUpper(String s) {
    	if (isEmpty(s)) {
    		return s;
    	}
    	String rest = s.length() > 1 ? s.substring(1) : "";
    	return Character.toUpperCase(s.charAt(0)) + rest;
    }
    
    /**
	 * String.split works in a bit uncomfortable way - when called on empty or
	 * string, it returns an array with one element (empty string). 
	 * 
	 * This method returns empty array if <code>isEmpty(s)</code> is <code>true</code>.
	 * 
	 * @param s splitted string
	 * @param regex the delimiting regular expression
	 * @return the array of strings computed by splitting this string around
	 *          matches of the given regular expression
	 * @throws java.util.regex.PatternSyntaxException if the regular expression's
	 *          syntax is invalid
	 * @throws NullPointerException <code>regex</code> is null
	 * @see #isEmpty(String)
	 */
	public static String[] split(String s, String regex) {
		return isEmpty(s) ?  new String[] {} : s.split(regex);
	}
    
    /**
	 * @param stringToQuote string to quote
     * @return "quoted" string
     */
    public static String quote(String stringToQuote) {
    	return "\"" + stringToQuote + "\"";
    }
    
}
