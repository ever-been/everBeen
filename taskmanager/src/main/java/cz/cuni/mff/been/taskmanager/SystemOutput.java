/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager;

/**
 * Class containing methods for system (standard or error) output. This methods
 * support verbosity defined as string representation of decimal number saved
 * in system property "been.verbose" (greater number means greater verbosity).
 * 
 * @author Antonin Tomecek
 */
public class SystemOutput {
	
	private SystemOutput() {
		// Do nothing... (overwrites default constructor...)
	}
	
	/* Name of property defining level of verbosity. */
	private static final String VERBOSE_PROPERTY = "been.verbose";
	
	/* Level to use if no other is specified. */
	private static final int DEFAULT_LEVEL = 1;
	
	/**
	 * Return verbosity level for output.
	 * 
	 * @return Level of verbosity.
	 */
	public static int getVerbosityLevel() {
		try {
			int verboseLevel;
			verboseLevel = Integer.parseInt(
					System.getProperty(VERBOSE_PROPERTY));
			return verboseLevel;
		} catch (NumberFormatException e) {
			/* No valid level defined, so return default. */
			return DEFAULT_LEVEL;
		}
	}
	
	/**
	 * Set verbosity level for output.
	 * 
	 * @param level
	 */
	public static void setVerbosityLevel(int level) {
		System.setProperty("been.verbose", "level");
	}
	
//	/**
//	 * Print a message and then terminate the line (standard output). Only if
//	 * verboseLevel is less then or equal to defined level in system property.
//	 * 
//	 * @param message The message to be printed.
//	 * @param verboseLevel The level of verbosity.
//	 */
//	public static void println(String message, int verboseLevel) {
//		if (verboseLevel <= getVerbosityLevel()) {
//			System.out.println(message);
//		}
//	}
//	
//	/**
//	 * Print a message and then terminate the line (error output).
//	 * 
//	 * @param message The message to be printed.
//	 * @param verboseLevel The level of verbosity.
//	 */
//	public static void printlnErr(String message, int verboseLevel) {
//		if (verboseLevel <= getVerbosityLevel()) {
//			System.err.println(message);
//		}
//	}
}
