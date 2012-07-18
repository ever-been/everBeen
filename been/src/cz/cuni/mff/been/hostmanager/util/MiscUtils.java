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

package cz.cuni.mff.been.hostmanager.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.GregorianCalendar;

import cz.cuni.mff.been.hostmanager.InvalidArgumentException;

/**
 * Various useful function used in Host Manager. 
 *
 * @author Branislav Repcek
 */
public class MiscUtils {

	/**
	 * Default format for date formatting.
	 * 
	 *  @see #formatDate(Date)
	 */
	public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";
	
	/**
	 * Concatenate two paths. It will correctly concatenate paths with or without separators.
	 * 
	 * @param path1 Start of the path sting.
	 * @param path2 End of the path string.
	 * 
	 * @return Concatenation of two path strings (path1 + path2) in correct form.
	 */
	public static String concatenatePath(String path1, String path2) {
		
		if (path1.endsWith(File.separator)) {
			if (path2.charAt(0) == File.separatorChar) {
				// xxx\ + \yyy
				return path1 + path2.substring(1);
			} else {
				// xxx\ + yyy
				return path1 + path2;
			}
		} else {
			if (path2.charAt(0) == File.separatorChar) {
				// xxx + \yyy
				return path1 + path2;
			} else {
				// xxx + yyy
				return path1 + File.separator + path2;
			}
		}
	}

	/**
	 * Extract name of the file from the full path.
	 * 
	 * @param fullPath String with full path.
	 * 
	 * @return Last element of the full path.
	 */
	public static String extractFileName(String fullPath) {
		
		File f = new File(fullPath);
		
		return f.getName();
	}
	
	/**
	 * Format date with given format.
	 * 
	 * @param date Date to format.
	 * @param formatStr Format string. Same rules as for java.text.SimpleDateFormat apply here.
	 * 
	 * @return String representation of given date. If requested format is invalid, resulting string
	 *         will be constructed by hand (it will be the same as string obtained with 
	 *         <code>yyyy/MM/dd HH:mm.ss</code> format string).
	 */
	public static String formatDate(Date date, String formatStr) {
		
		try {
			SimpleDateFormat formater = new SimpleDateFormat(formatStr);
		
			return formater.format(date);
		} catch (Exception e) {
			GregorianCalendar cal = new GregorianCalendar();
			
			cal.setTime(date);
			
			return String.valueOf(cal.get(GregorianCalendar.YEAR)) + "/"
			     + String.valueOf(cal.get(GregorianCalendar.MONTH)) + "/"
			     + String.valueOf(cal.get(GregorianCalendar.DAY_OF_MONTH)) + " "
			     + String.valueOf(cal.get(GregorianCalendar.HOUR_OF_DAY)) + ":"
			     + String.valueOf(cal.get(GregorianCalendar.MINUTE)) + "."
			     + String.valueOf(cal.get(GregorianCalendar.SECOND));
		}
	}
	
	/**
	 * Format date using default format.
	 * 
	 * @param date Date to format.
	 * 
	 * @return String with formatted date.
	 */
	public static String formatDate(Date date) {
		
		return formatDate(date, DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * Open specified file with specified encoding for writing.
	 * 
	 * @param fileName Name of the file to open.
	 * @param encoding Encoding of the resulting file.
	 * 
	 * @return Writer which will write to the file using specified encoding.  
	 * 
	 * @throws FileNotFoundException If file cannot be created (name is a directory, insufficient permissions...)
	 * 
	 * @throws UnsupportedEncodingException If the encoding is not supported.
	 */
	public static BufferedWriter openFileForWritingWithEncoding(String fileName, String encoding) 
		throws FileNotFoundException, UnsupportedEncodingException {
		
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), encoding));
	}

	/**
	 * Open specified file with specified encoding for reading.
	 * 
	 * @param fileName Name of the file to open.
	 * @param encoding Encoding of the file.
	 * 
	 * @return Reader which will read specified file using specified encoding.
	 * 
	 * @throws UnsupportedEncodingException If the encoding is not supported.
	 * 
	 * @throws FileNotFoundException File was not found or it is a directory.
	 */
	public static BufferedReader openFileForReadingWithEncoding(String fileName, String encoding) 
		throws UnsupportedEncodingException, FileNotFoundException {
		
		return new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
	}
	
	/**
	 * Verify that given object is not <code>null</code>.
	 * 
	 * @param x Object to test.
	 * @param paramName Name of the parameter which will appear in the exception in case it is <code>null</code>.
	 * 
	 * @throws InvalidArgumentException If parameter is <code>null</code>.
	 */
	public static void verifyParameterIsNotNull(Object x, String paramName) 
		throws InvalidArgumentException {
		
		if (x == null) {
			throw new InvalidArgumentException("Parameter " + paramName + " has to be non-null");
		}
	}
	
	/**
	 * Test if string is not empty.
	 * 
	 * @param s String to test. Note that parameter cannot be <code>null</code>.
	 * @param paramName Name of the parameter that will appear in exception.
	 * 
	 * @throws InvalidArgumentException If given string is empty.
	 */
	public static void verifyStringParameterNotEmpty(String s, String paramName)
		throws InvalidArgumentException {

		if (s.length() == 0) {
			throw new InvalidArgumentException("String " + paramName + " cannot be empty.");
		}
	}
	
	/**
	 * Verify that given string is not empty and it is not <code>null</code>.
	 *    
	 * @param s String to test.
	 * @param paramName Name of the parameter that will appear in the exception,
	 * 
	 * @throws InvalidArgumentException If string is empty or if it is <code>null</code>.
	 */
	public static void verifyStringParameterBoth(String s, String paramName) 
		throws InvalidArgumentException {
		
		verifyParameterIsNotNull(s, paramName);
		verifyStringParameterNotEmpty(s, paramName);
	}
	
	/**
	 * Verify that given parameter is greater that or equal to zero (=non-negative).
	 * 
	 * @param value Value to test.
	 * @param name Name of the parameter.
	 * 
	 * @throws InvalidArgumentException If parameter value is negative.
	 */
	public static void verifyIntParameterGEZero(long value, String name)
		throws InvalidArgumentException {
		
		if (value < 0) {
			throw new InvalidArgumentException("Parameter \"" + name + "\" is negative.");
		}
	}
	
	/**
	 * Verify that given parameter has positive value.
	 * 
	 * @param value Value to test.
	 * @param name Name of parameter that will appear in exception.
	 * 
	 * @throws InvalidArgumentException If given value is zero or negative.
	 */
	public static void verifyIntParameterGZero(long value, String name)
		throws InvalidArgumentException {
		
		if (value <= 0) {
			throw new InvalidArgumentException("Parameter \"" + name + "\" is negative or zero.");
		}
	}
	
	/**
	 * Get canonical name of the host. Names returned by this methods are used in the database.
	 * 
	 * @param hostName Name of the host to resolve.
	 * 
	 * @return Canonical name of the host if available.
	 * 
	 * @throws UnknownHostException If host was not found on the network.
	 */
	public static String getCanonicalHostName(String hostName) throws UnknownHostException {

		// Oh boy, this is soo stupid...
		
		String canonicalName;
		
		if (hostName.equalsIgnoreCase("localhost")) {
			canonicalName = InetAddress.getLocalHost().getCanonicalHostName();
		} else {
			canonicalName = InetAddress.getByName(hostName).getCanonicalHostName();
		}
		
		if (canonicalName.equalsIgnoreCase("localhost")
			|| canonicalName.equalsIgnoreCase("127.0.0.1")
			|| canonicalName.equalsIgnoreCase("::1")) {
			canonicalName = InetAddress.getLocalHost().getCanonicalHostName();
		}
		
		return canonicalName;
	}
	
	/**
	 * Get canonical name of the localhost.
	 * 
	 * @return Canonical name of the localhost.
	 */
	public static String getCanonicalLocalhostName() {
		
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (Exception e) {
			return "localhost";
		}
	}
	
	/**
	 * Remove file.
	 * 
	 * @param fileName Name of the file to remove.
	 * 
	 * @return <tt>true</tt> if file or directory has been successfully removed, <tt>false</tt>
	 *         otherwise.
	 * 
	 * @throws SecurityException
	 */
	public static boolean removeFile(String fileName) throws SecurityException {
		
		File file = new File(fileName);
		
		return file.delete();
	}
	
	/**
	 * Does nothing.
	 */
	private MiscUtils() {
	}
}
