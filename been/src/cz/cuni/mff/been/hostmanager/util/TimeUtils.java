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

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * This class provides helper methods to convert between various representations of time. It provides
 * methods to convert between Unix time/Windows time stamp and Java Date class.<br>
 * Windows time measures time in 100 ns intervals since start of the Windows epoch (1. 1. 1601 0:00).
 * Unix time is measured in seconds since the start of the Unix epoch (1. 1. 1970 0:00). Java measures 
 * time in milliseconds since the Unix epoch. From that it is clear, that Windows time is the most 
 * precise one and that's why it is used internally by Load Monitors and Load Server.<br>
 * <br>
 * Note that when converting from Windows time to Unix or Java time, precision is lost. Same goes for
 * converting from Java time to Unix time.
 *
 * @author Branislav Repcek
 */
public class TimeUtils {

	/**
	 * Current time zone.
	 */
	private static TimeZone timeZone;
	
	/**
	 * Date formatter for output messages.
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	
	static {
		GregorianCalendar calendar = new GregorianCalendar();
		timeZone = calendar.getTimeZone();
	}
	
	/**
	 * Total number of seconds between starts of Unix and Windows epochs. Windows epoch starts 
	 * at 1. 1. 1601 0:00, Unix epoch starts at 1. 1. 1970 0:00. 
	 * So the total number of seconds can be calculated like this:
	 *   
	 * There's 369 years between epochs (1970 - 1601 = 369). 89 of those years are leap years.
	 * So there's 89 * 366 + 280 * 365 = 134774 days between epochs. Since each day has 
	 * 24 * 60 * 60 = 86400 seconds, there are 86400 * 134774 = 11644473600.
	 */
	private static final long SECONDS_BETWEEN_EPOCHS = 11644473600L;
	
	/**
	 * Total number of 100 ns intervals in one second (10^7).
	 */
	private static final long SECONDS_TO_100NS = 10000000;
	
	/**
	 * Total number of milliseconds between Windows and Unix epochs.
	 */
	private static final long MILLISECONDS_BETWEEN_EPOCHS = SECONDS_BETWEEN_EPOCHS * 1000;
	
	/**
	 * Total number of 100 ns intervals in one millisecond.
	 */
	private static final long MILLISECONDS_TO_100NS = 10000;
	
	/**
	 * Convert Unix time to the Windows time stamp.
	 *  
	 * @param unixTime Number of seconds since start of the Unix epoch (1. 1. 1970 0:00).
	 * 
	 * @return Number of 100 ns intervals since start of the Windows epoch (1. 1. 1601 0:00).
	 */
	public static long convertUnixTimeToWindowsTime(long unixTime) {
		
		return (unixTime + SECONDS_BETWEEN_EPOCHS) * SECONDS_TO_100NS;
	}
	
	/**
	 * Convert Windows time to the Unix time stamp.
	 * 
	 * @param winTime Number of 100 ns intervals since start of the Windows epoch (1. 1. 1601 0:00).
	 * 
	 * @return Number of seconds since start of the Unix epoch (1. 1. 1970 0:00).
	 */
	public static long convertWindowsTimeToUnixTime(long winTime) {
		
		return winTime / SECONDS_TO_100NS - SECONDS_BETWEEN_EPOCHS;
	}
	
	/**
	 * Convert Unix time to the Java Date.
	 * 
	 * @param time Number of seconds since start of the Unix epoch (1. 1. 1970 0:00).
	 * 
	 * @return Date and time corresponding to the given Unix time.
	 */
	public static Date convertUnixTimeToJavaDate(long time) {
		
		return new Date(time * 1000);
	}
	
	/**
	 * Convert date and time to the Unix time stamp.
	 * 
	 * @param time Date and time to convert.
	 * 
	 * @return Number of seconds since start of the Unix epoch (1. 1. 1970 0:00).
	 */
	public static long convertJavaDateToUnixTime(Date time) {
		
		return time.getTime() / 1000;
	}
	
	/**
	 * Convert date and time to the Windows time stamp.
	 * 
	 * @param time Date and time to convert.
	 * 
	 * @return Number of 100 ns intervals since start of the Windows epoch (1. 1. 1601 0:00).
	 */
	public static long convertJavaDateToWindowsTime(Date time) {
		
		long t = time.getTime();
		
		return (t + MILLISECONDS_BETWEEN_EPOCHS + timeZone.getOffset(t)) * MILLISECONDS_TO_100NS;
	}
	
	/**
	 * Convert Windows time stamp to the date and time.
	 * 
	 * @param time Number of 100 ns intervals since start of the Windows epoch (1. 1. 1601 0:00).
	 * 
	 * @return Date and time corresponding to the given time stamp.
	 */
	public static Date convertWindowsTimeToJavaDate(long time) {
		
		long t = time / MILLISECONDS_TO_100NS;
		
		return new Date(t - MILLISECONDS_BETWEEN_EPOCHS - timeZone.getOffset(t));
	}
	
	/**
	 * Get formatted string with current date and time.
	 * 
	 * @return String with current date and time.
	 */
	public static String nowFormated() {
		
		return DATE_FORMAT.format(new Date());
	}
	
	/**
	 * Return timestamp corresponding to current date and time.
	 * 
	 * @return Timestamp corresponding to current date and time.
	 */
	public static long now() {
		
		return convertJavaDateToWindowsTime(new Date());
	}
	
	/**
	 * Does nothing.
	 */
	private TimeUtils() {
	}
}
