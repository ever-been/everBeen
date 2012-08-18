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
import java.util.Date;

/**
 * Contains a log message and other associated information. The log
 * message was produced by a task.
 * 
 * @author Jaroslav Urban
 */
public class LogRecord implements Serializable {

	private static final long	serialVersionUID	= 1441766218419524913L;

	/**
	 * Enumeration of the log messgae fields. EnumSet&lt;Fields&gt; is used in the
	 * web interface to specify, which columns will be displayed.
	 */
	public enum Fields {
		CONTEXT,
		TASK_ID,
		HOSTNAME,
		TIMESTAMP,
		LEVEL,
		MESSAGE
	}
	
	/** Context of the task */
	private String context;
	/** ID of the task*/
	private String taskID;
	/** Hostname where the task was running. */
	private String hostname;
	/** Time when was the message logged. */
	private Date timestamp;
	/** Log level of the message. */
	private LogLevel level;
	/** The log message. */
	private String message;

	/**
	 * 
	 * Allocates a new <code>LogRecord</code> object.
	 *
	 * @param context context of the task.
	 * @param taskID task's ID.
	 * @param hostname host where the task was running. 
	 * @param timestamp time when the message was logged.
	 * @param level log level of the message.
	 * @param message logged message.
	 */
	public LogRecord(
			String context, 
			String taskID, 
			String hostname, 
			Date timestamp, 
			LogLevel level, 
			String message) {
		
		super();
		this.context = context;
		this.taskID = taskID;
		this.hostname = hostname;
		this.timestamp = timestamp;
		this.level = level;
		this.message = message;
	}

	/**
	 * @return the context
	 */
	public String getContext() {
		return context;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @return the level
	 */
	public LogLevel getLevel() {
		return level;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the taskID
	 */
	public String getTaskID() {
		return taskID;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
}
