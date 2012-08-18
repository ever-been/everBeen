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

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import cz.cuni.mff.been.task.Task;


/**
 * Abstract logging class. Performs initialization of log4j.
 * 
 * @author Jaroslav Urban
 */
abstract public class BeenLogger {
	{
		Logger rootLogger = Logger.getRootLogger();
		rootLogger.addAppender(new ConsoleAppender(new PatternLayout(Task.STDOUT_LOG_FORMAT) ));
	}
	
	/**
	 * Sets the log level
	 * @param level
	 */
	abstract public void setLevel(LogLevel level);
	
	/**
	 * Returns the log level of this logger
	 * @return log level
	 */
	abstract public LogLevel getLevel();

	/**
	 * Logs a message with the DEBUG log level
	 * @param message
	 */
	abstract public void logDebug(String message);

	/**
	 * Logs a message with the ERROR log level
	 * @param message
	 */
	abstract public void logError(String message);

	/**
	 * Logs a message with the FATAL log level
	 * @param message
	 */
	abstract public void logFatal(String message);

	/**
	 * Logs a message with the INFO log level
	 * @param message
	 */
	abstract public void logInfo(String message);
	
	/**
	 * Logs a message with the TRACE log level
	 * @param message
	 */
	abstract public void logTrace(String message);

	/**
	 * Logs a message with the WARN log level
	 * @param message
	 */
	abstract public void logWarn(String message);
}
