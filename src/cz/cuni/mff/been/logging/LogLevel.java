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
package cz.cuni.mff.been.logging;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Level;

/**
 * Class representing logging level of a log message or a logger. The log levels are ordered in this order:
 * OFF, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, ALL. Every logger (like ConsoleLogger) will only log messages
 * with higher or equal log level than the log level of the logger.
 * 
 * The list of levels must be in sync with list in the
 * "logViewLogLevelComparator" function in file /webinterface/scripts/scripts.js.
 * 
 * @author Jaroslav Urban
 */
public enum LogLevel implements Serializable {

	/** Logs all log messages. */
	ALL( Level.ALL ),
	
	/** Logs from debug log messages upwards */
	DEBUG( Level.DEBUG ),

	/** Logs from error log messages upwards */
	ERROR( Level.ERROR ),

	/** Logs from fatal log messages upwards */
	FATAL( Level.FATAL ),

	/** Logs from info log messages upwards */	
	INFO( Level.INFO ),

	/** Logs nothing. */	
	OFF( Level.OFF ),
	
	/** Logs from trace log messages upwards */
	TRACE( Level.TRACE ),
	
	/** Logs from warning log messages upwards */
	WARN( Level.WARN );
	
	private static final HashMap<Level, LogLevel> levels;

//	public static final LogLevel ALL = new LogLevel(Level.ALL);
//	public static final LogLevel DEBUG = new LogLevel(Level.DEBUG);
//	public static final LogLevel ERROR = new LogLevel(Level.ERROR);
//	public static final LogLevel FATAL = new LogLevel(Level.FATAL);
//	public static final LogLevel INFO = new LogLevel(Level.INFO);
//	public static final LogLevel OFF = new LogLevel(Level.OFF);
//	public static final LogLevel TRACE = new LogLevel(Level.TRACE);
//	public static final LogLevel WARN = new LogLevel(Level.WARN);
	
	/* CAUTION!!! Static values are initialized AFTER the "dynamic" ones.
	 * That's quite logical, for children must be initialized before their ancestor.
	 * If you try the other way round, you'll get a NullPointerException.
	 */
	static {
		levels = new HashMap< Level, LogLevel >();
		for ( LogLevel logLevel : LogLevel.values() ) {
			levels.put( logLevel.log4jLevel, logLevel );
		}
	}
	
	private final Level log4jLevel;
	
	private LogLevel( Level level ) {
		log4jLevel = level;
	}
	
	/**
	 * Converts a log4j level to BEEN's log level
	 * 
	 * @param level log4j log  level
	 * @return BEEN's debug level
	 */
	public static LogLevel getInstance(Level level) {
		return levels.get(level);
	}
	
	/**
	 * Returns the value of this log level as log4j's level
	 * @return log4j's level
	 */
	public Level toLog4jLevel() {
		return log4jLevel;
	}

	@Override
	public String toString() {
		return log4jLevel.toString();
	}
	
//	/**
//	 * 
//	 * @param name name of the log level.
//	 * @return LogLevel with the given name.
//	 * @throws IllegalArgumentException if name is not a valid log level. 
//	 */
//	public static LogLevel valueOf(String name) throws IllegalArgumentException {
//		if (name.equals("ALL")) {
//			return ALL;
//		}
//		if (name.equals("DEBUG")) {
//			return DEBUG;
//		}
//		if (name.equals("ERROR")) {
//			return ERROR;
//		}
//		if (name.equals("FATAL")) {
//			return FATAL;
//		}
//		if (name.equals("INFO")) {
//			return INFO;
//		}
//		if (name.equals("OFF")) {
//			return OFF;
//		}
//		if (name.equals("TRACE")) {
//			return TRACE;
//		}
//		if (name.equals("WARN")) {
//			return WARN;
//		}
//
//		throw new IllegalArgumentException("Unknown log level: " + name);
//	}
	
	/**
	 * @param level
	 * @return true if this log level has a higher or equal level than the 
	 * level passed as argument, false otherwise.
	 */
	public boolean isGreaterOrEqual(LogLevel level) {
		return toLog4jLevel().isGreaterOrEqual(level.log4jLevel);
	}
}
