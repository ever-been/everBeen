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
package cz.cuni.mff.been.webinterface;

import java.text.NumberFormat;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * This class brings pieces of PHP's usefulness into Java :-)
 * It also contains some other useful functions, which do not originate
 * form PHP.
 * 
 * @author David Majda
 */
public class Routines {
	private static final int SMALLEST_TWO_CIPHER_NUMBER = 10;
	private static final int DEC_BASE = 10;
	private static final int HEX_BASE = 0x10;
	private static final String UNIT_PREFIX_CHARS = "kMGTP";
	private static final int MILISECONDS_IN_SECOND = 1000;
	private static final int MINUTES_IN_HOUR = 60;
	private static final int SECONDS_IN_MINUTE = 60;

	/**
	 * Does the same thing as PHP's <code>htmlspecialchars</code> function with
	 * <code>ENT_QUOTES</code> set, i.e. escapes string for output in HTML.
	 * 
	 * @param s string to escape 
	 * @return escaped string
	 * 
	 * @see <a href="http://www.php.net/htmlspecialchars">htmlspecialchars</a> 
	 */
	public static String htmlspecialchars( String s ) {
		if ( null == s ) {
			return null;
		} else {
			StringBuilder builder;
			int end;
			char c;
			
			builder = new StringBuilder( 10 + s.length() );
			end = s.length();
			for ( int i = 0; i < end; ++i ) {
				switch ( c = s.charAt( i ) ) {
					case '&':
						builder.append( "&amp;" );
						break;
					case '"':
						builder.append( "&quot;" );
						break;
					case '\'':
						builder.append( "&#039;" );
						break;
					case '<':
						builder.append( "&lt;" );
						break;
					case '>':
						builder.append( "&gt;" );
						break;
					default:
						builder.append( c );
						break;
				}
			}
			return builder.toString();
		}
	}
    
	/**
	 * Does the same thing as PHP's <code>ucfirst</code> function, i.e. makes
	 * first character of the string uppercase.
	 * 
	 * @param s string to modify 
	 * @return string with uppercased first letter
	 * 
	 * @see <a href="http://www.php.net/ucfirst">ucfirst</a> 
	 */
	public static String ucfirst(String s) {
		if (s != null && s.length() > 0) {
			return Character.toUpperCase(s.charAt(0)) + s.substring(1);
		} else {
			return s;
		}
	}
    
	/**
	 * Returns a string containing string representations of all the array
	 * elements separated with <code>glue</code>. Method <code>toString</code> is
	 * used to get the string representation of the array elements.
	 * 
	 * @param <T> type of the array elements
	 *  
	 * @param glue string inserted between elements 
	 * @param pieces elements to concatenate
	 * @return glued array elements
	 */
	public static <T> String join( String glue, T[] pieces ) {
		StringBuilder builder;
		
		builder = new StringBuilder( 2 * pieces.length * glue.length() );
		if ( pieces.length > 0 ) {
			builder.append( pieces[ 0 ].toString() );
			for ( int i = 1; i < pieces.length; ++i ) {
				builder.append( glue );
				builder.append( pieces[ i ].toString() );
			}
		}
		return builder.toString();
	}
    
	/**
	 * Returns a string containing string representations of all the array
	 * elements separated with <code>glue</code>.
	 * 
	 * This function is needed because int <code>int[]</code> can't be autoboxed
	 * into <code>Integer[]</code> and thus the generic version of this function
	 * can't be used.  
	 * 
	 * @param glue string inserted between elements 
	 * @param pieces elements to concatenate
	 * @return glued array elements
	 */
	public static String join( String glue, int[] pieces ) {
		StringBuilder builder;
		
		builder = new StringBuilder( 2 * pieces.length * glue.length() );
		if ( pieces.length > 0 ) {
			builder.append( pieces[ 0 ] );
			for ( int i = 1; i < pieces.length; ++i ) {
				builder.append( glue );
				builder.append( pieces[ i ] );
			}
		}
		return builder.toString();
	}

	/**
	 * Returns string representation of given number and ensures that numbers
	 * &lt; 10 will be prefixed by leading zero.
	 * 
	 * @param n number
	 * @return string representation of the number with possible "0" prefix
	 */
	public static String addLeadingZero(int n) {
		return n < SMALLEST_TWO_CIPHER_NUMBER ? "0" + n : Integer.toString(n);
	}

	/**
	 * Returns string representation of given number and ensures that numbers
	 * &lt; 10 will be prefixed by leading zero.
	 * 
	 * @param n number
	 * @return string representation of the number with possible "0" prefix
	 */
	public static String addLeadingZero(long n) {
		return n < SMALLEST_TWO_CIPHER_NUMBER ? "0" + n : Long.toString(n);
	}

	/**
	 * Returns string representation of given number and ensures that it
	 * will be prefixed by leading zeroes so its length will be at least
	 * <code>length</code> characters.
	 * 
	 * @param n number
	 * @param length minimal length
	 * @return string representation of the number with possible prefix
	 *          of leading zeroes
	 */
	public static String addLeadingZeroes(int n, int length) {
		String result = Integer.toString(n);
		while (result.length() < length) {
			result = "0" + result;
		}
		return result;
	}

	/**
	 * Checks if the browser which initiated given request is Microsoft Internet Explorer. 
	 * 
	 * @param request checked request
	 * @return <code>true</code> if the initiating browser is Microsoft Internet Explorer;
	 *          <code>false</code> otherwise
	 */
	public static boolean browserIsMSIE(HttpServletRequest request) {
		return request.getHeader("User-Agent").indexOf("MSIE") >= 0;
	}
	
	/**
	 * Escapes string so it can be used inside JavaScript string.
	 * 
	 * More specifically, it prefixes characters 0x0..0x1F, backslash ("\"),
	 * apostrophe ("'") and quote (""") with one backslash. All other characters
	 * aren't touched.
	 * 
	 * @param s string to escape
	 * @return escaped string
	 */
	public static String javaScriptEscape( String s ) {
		if ( null == s ) {
			return null;
		} else {
			StringBuilder builder;
			int end;
			char c;
			
			builder = new StringBuilder( 20 + s.length() );
			end = s.length();
			for ( int i = 0; i < end; ++i ) {
				switch ( c = s.charAt( i ) ) {
					case '\'':
						builder.append( "\\\\'" );
						break;
					case '"':
						builder.append( "\\\\\"" );
						break;
					default:
						if ( c < ' ' ) {
							builder
							.append( "\\\\x" )
							.append( Character.forDigit( c / HEX_BASE, HEX_BASE ) )
							.append( Character.forDigit( c % HEX_BASE, HEX_BASE ) );
						} else {
							builder.append( c );
						}
						break;
				}
			}
			return builder.toString();
		}
	}

	/**
	 * Checks if the string is integer. The check is performed using
	 * <code>Integer.parseInt</code> method.
	 * 
	 * @param s string to check
	 * @return <code>true</code> if the string is integer;
	 *          <code>false</code> otherwise
	 */
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the string is long integer. The check is performed using
	 * <code>Long.parseLong</code> method.
	 * 
	 * @param s string to check
	 * @return <code>true</code> if the string is long integer;
	 *          <code>false</code> otherwise
	 */
	public static boolean isLong(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * String.split works in a bit uncomfortable way - when called on empty
	 * string, it returns an array with one element (empty string). We want it to
	 * return empty array in this case.
	 * 
	 * @param regex the delimiting regular expression
	 * @param s split string
	 * @return the array of strings computed by splitting this string around
	 *          matches of the given regular expression
	 * @throws java.util.regex.PatternSyntaxException if the regular expression's
	 *          syntax is invalid
	 * @throws NullPointerException if <code>s</code> or <code>regex</code>
	 *          is null
	 */
	public static String[] split2(String regex, String s) {
		return !s.equals("") ? s.split(regex) : new String[] {};
	}
	
	/**
	 * Converts a number with unit to suitable <em>prefixed unit</em> and returns it
	 * formatted according to a specified locale.
	 * 
	 * Conversion to prefixed unit means that the function prepends one of the
	 * prefixes to given unit and divides the number by matching value. Supported
	 * prefixes are <code>'k'</code>, <code>'M'</code>, <code>'G'</code>,
	 * <code>'T'</code> and <code>'P'</code>, which mean "divide by 1024", "divide
	 * by 1024<sup>2</sup>" etc. Function uses the largest prefix, which doesn't
	 * make the number smaller than <code>1</code> after the division.
	 * 
	 * @param number number to convert and format
	 * @param unit unit appended to the number
	 * @param locale locale to format the number with
	 * @return converted and formatted number
	 */
	public static String formatNumberWithPrefixedUnit(long number, String unit,
			Locale locale) {
		if (unit == null) {
			throw new NullPointerException("Parameter \"unit\" cannot be null.");
		}
		if (locale == null) {
			throw new NullPointerException("Parameter \"locale\" cannot be null.");
		}
		
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
		numberFormat.setMaximumFractionDigits(1);
		
		for (int i = UNIT_PREFIX_CHARS.length() - 1; i >= 0; i--) {
			long unitSize = (1L << (i + 1) * DEC_BASE);
			if (number >= unitSize) {
				return numberFormat.format((double) number / (double) unitSize)
					+ " "
					+ Character.toString(UNIT_PREFIX_CHARS.charAt(i))
					+ unit; 
			}
		}
		
		/* If no suitable unit was found, or the number is zero, return the number
		 * in the basic unit.
		 */
		return numberFormat.format(number)
			+ (unit.equals("") ? "" : " ")
			+ unit;
	}
	
	/**
	 * Does the same as <code>formatNumberWithPrefixedUnit</code> method, but
	 * supplies <code>Locale.ENGLISH</code> as its <code>locale</code> parameter.
	 * 
	 * @param number number to convert and format
	 * @param unit unit appended to the number
	 * @return converted and formatted number
	 */
	public static String formatNumberWithPrefixedUnit(long number, String unit) {
		return formatNumberWithPrefixedUnit(number, unit, Locale.ENGLISH);
	}
	
	/**
	 * Takes a number of milliseconds representing time and formats it as
	 * a human-readable time (in format [hh:]mm:ss). Seconds are always
	 * zero-padded, minutes are zero-padded if time is big enough to output the
	 * "hh" field.
	 * 
	 * @param millis number of milliseconds representing time
	 * @return time in human readable format 
	 */
	public static String formatMillisAsHMS(long millis) {
		long seconds = millis / MILISECONDS_IN_SECOND;
		
		String result = ":" + Routines.addLeadingZero(seconds % SECONDS_IN_MINUTE);
		long minutes = seconds / SECONDS_IN_MINUTE;
		if (minutes < MINUTES_IN_HOUR) {
			return Long.toString(minutes) + result;
		} else {
			return Long.toString(minutes / MINUTES_IN_HOUR) + ":"
				+ Routines.addLeadingZero(minutes % MINUTES_IN_HOUR) + result;
		}
	}
	
	/**
	 * Does the same thing as PHP's <code>nl2br</code> function, i.e. prepends
	 * each <code>"\n"</code>, <code>"\r"</code> or <code>"\r\n"</code> with
	 * <code>&lt;br /&gt;</code>. In other words, it escapes newlines so they are
	 * correctly displayed in HTML.
	 * 
	 * @param s string to escape 
	 * @return escaped string
	 */
	public static String nl2br(String s) {
		if (s != null) {
			StringBuilder result = new StringBuilder();
			boolean afterCR = false;
			for (int i = 0; i < s.length(); i++) {
				switch (s.charAt(i)) {
					case '\r':
						afterCR = true;
						break;
					case '\n':
						if (afterCR) {
							result.append("<br />\r\n");
							afterCR = false;
						} else {
							result.append("<br />\n");
						}
						break;
					default:
						if (afterCR) {
							result.append("<br />\r").append(s.charAt(i));
							afterCR = false;
						} else {
							result.append(s.charAt(i));
						}
				}
			}
			if (afterCR) {
				result.append("<br />\r");
			}
			return result.toString();
		} else {
			return null;
		}
	}

	/**
	 * Does the same thing as PHP's <code>trim</code> function, i.e. strips
	 * whitespace from the beginning and end of a string. "whitespace" is defined
	 * by the <code>Char.isWhitespace</code> method.
	 * 
	 * @param s string to trim 
	 * @return trimmed string
	 * 
	 * @see <a href="http://www.php.net/trim">trim</a> 
	 */
	public static String trim(String s) {
		if (s == null) {
			throw new NullPointerException("Parameter \"s\" cannot be null.");
		}
		if (s.equals("")) {
			return "";
		}
		
		int begin = 0;
		int end = s.length() - 1;
		
		char ch;
		
		ch = s.charAt(begin);
		while (Character.isWhitespace(ch)) {
			begin++;
			if (begin > s.length() - 1) {
				break;
			}
			ch = s.charAt(begin);
		}
		
		ch = s.charAt(end);
		while (Character.isWhitespace(ch)) {
			end--;
			if (end < 0) {
				break;
			}
			ch = s.charAt(end);
		}
		
		if (begin <= end) {
			return s.substring(begin, end + 1);
		} else {
			return "";
		}
	}

	/**
	 * Returns a value bounded by the minimum and maximum (i.e. if the value is
	 * smaller than the minimum, returns the minimum, otherwise returns the value;
	 * analogically with the maximum).  
	 * 
	 * @param <T> value type
	 * @param value value to bound
	 * @param min minimum bound
	 * @param max maximum bound
	 * @return value bounded by the minimum and maximum
	 * @throws NullPointerException if any argument is null
	 */
	public static <T extends Comparable<T>> T bounded(T value, T min, T max) {
		if (value == null || min == null || max == null) {
			throw new NullPointerException("All parameters must be non-null.");
		}
		if (min.compareTo(max) > 0) {
			// TODO: WHY??? There are many cases when excluding an interval might be useful.
			// BTW, there are multiple implementations of this in BEEN and some allow such a special case.
			throw new IllegalArgumentException("Minimum can not be greater than maximum.");
		}
		
		return value.compareTo(min) < 0
			? min
			: (value.compareTo(max) > 0 ? max : value);
	}
		
	
	/**
	 * Returns given string, or string "(none)" if the given string was empty.
	 * 
	 * @param s string to process
	 * @return given string, or string "(none)" if the given string was empty
	 * @throws NullPointerException if given string is <code>null</code>
	 */
	public static String stringOrNone(String s) {
		if (s == null) {
			throw new NullPointerException("Parameter \"s\" cannot be null.");
		}
		
		return !s.equals("") ? s : "(none)";
	}
	
	/**
	 * Returns the value of a cookie identified by its name. 
	 * 
	 * @param request HTTP request
	 * @param name cookie name
	 * @return cookie value or <code>null</code> if the cookie is not found
	 */
	public static String getCookieByName(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		
		for (Cookie cookie: cookies) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Routines() {
	}
}
