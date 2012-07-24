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

package cz.cuni.mff.been.hostmanager;

import cz.cuni.mff.been.hostmanager.util.TimeUtils;
import cz.cuni.mff.been.logging.LogLevel;
import cz.cuni.mff.been.task.Task;

/**
 * Simple adapter for the logger used in BEEN. This logger can also write messages to the stdout.
 *
 * @author Branislav Repcek
 */
public class HostManagerLogger {

	/**
	 * Default log-level.
	 */
	public static final LogLevels DEFAULT_LOG_LEVEL = LogLevels.INFO;
	
	/**
	 * Task which may be used for logging.
	 */
	private Task task;
	
	/**
	 * Current log-level.
	 */
	private LogLevels level;
	
	/**
	 * Log levels that can be used to control amount of the output.
	 *
	 * @author Branislav Repcek
	 */
	public enum LogLevels {
			
		/**
		 * Output all messages.
		 */
		ALL( null ) {
			@Override
			public String getHeader() {
				throw new IllegalStateException( "getHeader() invoked on LogLevels.ALL." );
			}
		},
		
		/**
		 * Log trace messages and higher.
		 */
		TRACE( "TRACE" ),
		
		/**
		 * Log debug messages and higher (not trace).
		 */
		DEBUG( "DEBUG" ),
		
		/**
		 * Log info messages and higher (not debug and trace).
		 */
		INFO( "INFO" ),
		
		/**
		 * Log warning messages and higher (not info, debug, nor trace).
		 */
		WARNING( "WARNING" ),
		
		/**
		 * Log error messages and higher (not warnings, info, debug, nor tace).
		 */
		ERROR( "ERROR" ),
		
		/**
		 * Log only fatal errors.
		 */
		FATAL( "FATAL" ),

		/**
		 * Do not log anything.
		 */
		NONE( null ) {
			@Override
			public String getHeader() {
				throw new IllegalStateException( "getHeader() invoked on LogLevels.NONE." );
			}
		};
		
		private final String	header;
		
		private LogLevels( String header ) {
			this.header = header;
		}
		
		public String getHeader() {
			return header;
		}
		
		/**
		 * Create from log level specification as used in the default BEEN logger.
		 * 
		 * @param level Log level.
		 * 
		 * @return LogLevels corresponding to the specified level.
		 */
		public static LogLevels fromTaskLogLevel( LogLevel level ) {
			switch ( level ) {
				case INFO:
					return INFO;
				case WARN:
					return WARNING;
				case DEBUG:
					return DEBUG;
				case ERROR:
					return ERROR;
				case FATAL:
					return FATAL;
				case TRACE:
					return TRACE;
				case ALL:
					return ALL;
				default:
					return INFO;
			}
		}
	}
	
	/**
	 * Create new logger which will send its messages to logger used by given task. Log level is
	 * set to that of the task.
	 * 
	 * @param task Task which will receive log messages. If this is <tt>null</tt> messages will
	 *        be written to the stdout.
	 */
	public HostManagerLogger(Task task) {
		
		if (task != null) {
			this.task = task;
			this.level = LogLevels.fromTaskLogLevel(task.getLogLevel());
		} else {
			this.level = DEFAULT_LOG_LEVEL;
		}
	}

	/**
	 * Create console logger with given log level.
	 * 
	 * @param level Log level.
	 */
	public HostManagerLogger(LogLevels level) {
		
		this.level = level;
	}
	
	/**
	 * Create console logger with default log level.
	 *
	 */
	public HostManagerLogger() {
		
		this.level = DEFAULT_LOG_LEVEL;
	}

	/**
	 * Log INFO message.
	 * 
	 * @param message Message to log.
	 */
	public void logInfo(String message) {
		
		if (task != null) {
			task.logInfo(message);
		} else {
			if (level.ordinal() <= LogLevels.INFO.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.INFO.getHeader() + " " + message);
			}
		}
	}

	/**
	 * Log INFO message.
	 * 
	 * @param message Message to log.
	 * @param e Exception to log. Exception's message will be appended after the message.
	 */
	public void logInfo(String message, Exception e) {
		
		if (task != null) {
			task.logInfo(message + " Exception: " + e.getMessage() + ".");
		} else {
			if (level.ordinal() <= LogLevels.INFO.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.INFO.getHeader() + " "
					+ message + " Exception: " + e.getMessage() + ".");
			}
		}
	}

	/**
	 * Log DEBUG message.
	 * 
	 * @param message Message to log.
	 */
	public void logDebug(String message) {
		
		if (task != null) {
			task.logDebug(message);
		} else {
			if (level.ordinal() <= LogLevels.DEBUG.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.DEBUG.getHeader() + " " + message);
			}
		}
	}

	/**
	 * Log DEBUG message.
	 * 
	 * @param message Message to log.
	 * @param e Exception to log. Exception's message will be appended after the message.
	 */
	public void logDebug(String message, Exception e) {
		
		if (task != null) {
			task.logDebug(message + " Exception: " + e.getMessage() + ".");
		} else {
			if (level.ordinal() <= LogLevels.DEBUG.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.DEBUG.getHeader() + " "
					+ message + " Exception: " + e.getMessage() + ".");
			}
		}
	}
	
	/**
	 * Log TRACE message.
	 * 
	 * @param message Message to log.
	 */
	public void logTrace(String message) {
		
		if (task != null) {
			task.logTrace(message);
		} else {
			if (level.ordinal() <= LogLevels.TRACE.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.TRACE.getHeader() + " " + message);
			}
		}
	}

	/**
	 * Log TRACE message.
	 * 
	 * @param message Message to log.
	 * @param e Exception to log. Exception's message will be appended after the message.
	 */
	public void logTrace(String message, Exception e) {
		
		if (task != null) {
			task.logTrace(message + " Exception: " + e.getMessage() + ".");
		} else {
			if (level.ordinal() <= LogLevels.TRACE.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.TRACE.getHeader() + " "
					+ message + " Exception: " + e.getMessage() + ".");
			}
		}
	}
	
	/**
	 * Log WARNING message.
	 * 
	 * @param message Message to log.
	 */
	public void logWarning(String message) {
		
		if (task != null) {
			task.logWarning(message);
		} else {
			if (level.ordinal() <= LogLevels.WARNING.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.WARNING.getHeader() + " " + message);
			}
		}
	}

	/**
	 * Log WARNING message.
	 * 
	 * @param message Message to log.
	 * @param e Exception to log. Exception's message will be appended after the message.
	 */
	public void logWarning(String message, Exception e) {
		
		if (task != null) {
			task.logWarning(message + " Exception: " + e.getMessage() + ".");
		} else {
			if (level.ordinal() <= LogLevels.WARNING.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.WARNING.getHeader() + " "
					+ message + " Exception: " + e.getMessage() + ".");
			}
		}
	}
	
	/**
	 * Log ERROR message.
	 * 
	 * @param message Message to log.
	 */
	public void logError(String message) {
		
		if (task != null) {
			task.logError(message);
		} else {
			if (level.ordinal() <= LogLevels.ERROR.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.ERROR.getHeader() + " " + message);
			}
		}
	}

	/**
	 * Log ERROR message.
	 * 
	 * @param message Message to log.
	 * @param e Exception to log. Exception's message will be appended after the message.
	 */
	public void logError(String message, Exception e) {
		
		if (task != null) {
			task.logError(message + " Exception: " + e.getMessage() + ".");
		} else {
			if (level.ordinal() <= LogLevels.ERROR.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.ERROR.getHeader() + " "
					+ message + " Exception: " + e.getMessage() + ".");
			}
		}
	}
	
	/**
	 * Log FATAL message.
	 * 
	 * @param message Message to log.
	 */
	public void logFatal(String message) {
		
		if (task != null) {
			task.logFatal(message);
		} else {
			if (level.ordinal() <= LogLevels.FATAL.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.FATAL.getHeader() + " " + message);
			}
		}
	}

	/**
	 * Log FATAL message.
	 * 
	 * @param message Message to log.
	 * @param e Exception to log. Exception's message will be appended after the message.
	 */
	public void logFatal(String message, Exception e) {
		
		if (task != null) {
			task.logInfo(message + " Exception: " + e.getMessage() + ".");
		} else {
			if (level.ordinal() <= LogLevels.FATAL.ordinal()) {
				System.out.println(TimeUtils.nowFormated() + " " + LogLevels.FATAL.getHeader() + " "
					+ message + " Exception: " + e.getMessage() + ".");
			}
		}
	}
	
	/**
	 * Log message without header. Message is written on any log level except NONE.
	 * Message is written to stdout only.
	 * 
	 * @param message Message to log.
	 */
	public void log(String message) {
		
		if (level != LogLevels.NONE) {
			System.out.println(message);
		}
	}

	/**
	 * Log message without header. Message is written on any log level except NONE.
	 * Message is written to stdout only.
	 * 
	 * @param message Message to log.
	 * @param e Exception to log. Exception's message will be appended after the message.
	 */
	public void log(String message, Exception e) {
		
		if (level != LogLevels.NONE) {
			System.out.println(message + " Exception: " + e.getMessage() + ".");
		}
	}
	
	/**
	 * @return Current log level.
	 */
	public LogLevels getLogLevel() {
		
		return level;
	}
	
	/**
	 * @param level New log level.
	 */
	public void setLogLevel(LogLevels level) {
		
		this.level = level;
	}
	
	/**
	 * @return <tt>true</tt> if logger outputs its messages to the console.
	 */
	public boolean isConsoleLogger() {
		
		return task == null;
	}
}
