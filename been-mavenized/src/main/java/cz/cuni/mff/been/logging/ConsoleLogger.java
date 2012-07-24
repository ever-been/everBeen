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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * A logger that logs all log messages on stdout.
 * 
 * @author Jaroslav Urban
 */
public class ConsoleLogger extends BeenLogger {
	/**
	 * log4j's logger
	 */
	Logger logger;
	
	/**
	 * Allocates a new ConsoleLogger object, with the given name
	 * 
	 * @param name
	 */
	public ConsoleLogger(String name) {
		logger = Logger.getLogger(name);
		logger.setLevel(Level.INFO);
	}
	
	/**
	 * Allocates a new ConsoleLogger object, and determines it's name from the given reference
	 * 
	 * @param obj
	 */
	public ConsoleLogger(Object obj) {
		logger = Logger.getLogger(obj.getClass().getCanonicalName());
		logger.setLevel(Level.INFO);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.common.BeenLogger#setLevel(cz.cuni.mff.been.common.LogLevel)
	 */
	@Override
	public void setLevel(LogLevel level) {
		logger.setLevel(level.toLog4jLevel());
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.common.BeenLogger#getLevel()
	 */
	@Override
	public LogLevel getLevel() {
		return LogLevel.getInstance(logger.getLevel());
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.common.BeenLogger#logDebug(java.lang.String)
	 */
	@Override
	public void logDebug(String message) {
		logger.debug(message);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.common.BeenLogger#logError(java.lang.String)
	 */
	@Override
	public void logError(String message) {
		logger.error(message);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.common.BeenLogger#logFatal(java.lang.String)
	 */
	@Override
	public void logFatal(String message) {
		logger.fatal(message);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.common.BeenLogger#logInfo(java.lang.String)
	 */
	@Override
	public void logInfo(String message) {
		logger.info(message);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.common.BeenLogger#logTrace(java.lang.String)
	 */
	@Override
	public void logTrace(String message) {
		logger.trace(message);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.common.BeenLogger#logWarn(java.lang.String)
	 */
	@Override
	public void logWarn(String message) {
		logger.warn(message);
	}
}
