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

import java.util.Date;

/** 
 * Interface for a log storage facility. The logs are produced by tasks.
 * 
 * @author Jaroslav Urban
 */
public interface LogStorage {
	/** 
	 * The recommended number of lines that should be sent to the log storage
	 * or retrieved from it at once.
	 */ 
	static final int OUTPUT_TRANSFER_LINECOUNT = 100;
	
	/**
	 * Registers a context so that log messages of it's tasks can be stored. Must
	 * be called before the tasks of this context are registered by 
	 * <code>addTask()</code>. The same context can be registered repeatadly, in
	 * that case the old content of the context must be preserved.
	 * 
	 * @param name name of the context; cannot be null or an empty string.
	 * @throws LogStorageException if an error occured while registering the context.
	 * @throws IllegalArgumentException if the name of the context name is empty
	 * @throws NullPointerException  if the name of the context is <tt>null</tt>.
	 * @see #addTask(String, String)
	 */
	void addContext(String name)
	throws LogStorageException, IllegalArgumentException, NullPointerException;
	
	/** 
	 * Removes the context and it's logs from the storage, no newer messages 
	 * can be logged in this context. The context must've been registered in the 
	 * log storage with <code>addContext()</code> before.
	 * 
	 * @param name name of the context; cannot be null or an empty string.
	 * @throws LogStorageException if an error occured while removing the context.
	 * @throws IllegalArgumentException if the name of the context is empty,
	 * or if the context wasn't registered yet.
	 * @throws NullPointerException  if the name of the context is <tt>null</tt>.
	 * @see #addContext(String)
	 */
	void removeContext(String name) 
	throws LogStorageException, IllegalArgumentException, NullPointerException;
	
	/**
	 * Checks whether the context was already registered in the log storage.
	 * 
	 * @param name context name; cannot be null or an empty string.
	 * @throws LogStorageException if an error occured while checking the registration.
	 * @throws IllegalArgumentException if the name of the context is empty.
	 * @throws NullPointerException  if the name of the context is <tt>null</tt>.
	 * @return true if the context was already registered, false otherwise.
	 */
	boolean isContextRegistered(String name)
	throws LogStorageException, IllegalArgumentException, NullPointerException;

	/** 
	 * Registers a task so that it's log messages can be stored. Must
	 * be called before the first message of this task is logged. The context
	 * of the task must be registerd by <code>addContext()</code> before callind
	 * this method. A task can be registered repeatadly, in that case old logs
	 * of the task and other stored info must be preserved.
	 * 
	 * @param context context of the task; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @throws LogStorageException if an error occured while registering the task.
	 * @throws IllegalArgumentException if the name of the context or the task's ID
	 * are empty or if the context wasn't registered yet.
	 * @throws NullPointerException  if the name of the context or the task's ID 
	 * is <tt>null</tt>.
	 * @see #addContext(String)
	 */
	void addTask(String context, String taskID) 
	throws LogStorageException, IllegalArgumentException, NullPointerException;

	
	/**
	 * Removes specific task from log storage and removes all its logs.
	 * @param context context of task
	 * @param taskId task ID 
	 * @throws LogStorageException
	 */
	void removeTask(String context, String taskId) throws LogStorageException;
	
	/**
	 * Checks whether the task was already registered in the log storage.
	 * 
	 * @param context context of the task; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @return true if the task was already registered, false otherwise.
	 * @throws LogStorageException if an error occured checking the registration.
	 * @throws IllegalArgumentException if the name of the context or the task's ID
	 * are empty or if the context wasn't registered yet.
	 * @throws NullPointerException if the name of the context or the task's ID 
	 * is <tt>null</tt>.
	 * @see #addContext(String)
	 */
	boolean isTaskRegistered(String context, String taskID)
	throws LogStorageException, IllegalArgumentException, NullPointerException;

	/**
	 * Sets the hostname where a task is running. The task must be registered in 
	 * the log storage with <code>addTask()</code> before calling this method.
	 * 
	 * @param context context of the taskl; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @param hostname hostname where is the task running; cannot be null or an empty
	 * string.
	 * @throws LogStorageException if an error occured while storing the hostname
	 * of the task.
	 * @throws IllegalArgumentException if any argument is an empty string or if 
	 * the task wasn't registered in the log storage with <code>addTask</code> yet.
	 * @throws NullPointerException if any argument is an empty string.
	 * @see #addTask(String, String)
	 */
	void setTaskHostname(String context, String taskID, String hostname)
	throws LogStorageException, IllegalArgumentException, NullPointerException;
	
	/**
	 * Stores a log message for a task. The task and it's context must be registered
	 * by <code>addTask()</code> and <code>addContext</code> before the first call 
	 * of this method.
	 * 
	 * @param context task's context; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @param timestamp timestamp of the log event; cannot be null.
	 * @param level log level of the message; cannot be null.
	 * @param message the log message; can be multiline; cannot be null or contain
	 * "\t\t\t".
	 * @throws LogStorageException if an error occured while storing the log
	 * message.
	 * @throws IllegalArgumentException if context name or task's ID are empty or
	 * if the task wasn't registered in the log storage yet.
	 * @throws NullPointerException if any argument is <tt>null</tt>.
	 * @see #addTask(String, String)
	 * @see #addContext(String) 
	 */
	void log(
			String context, 
			String taskID, 
			Date timestamp,
			LogLevel level,
			String message) 
	throws LogStorageException, IllegalArgumentException, NullPointerException;
	
	/**
	 * Gets all log messages produced by a task. The task must've been registered
	 * in the log storage with <code>addTask</code> before.
	 * 
	 * @param context task's context; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @return logs of the task.
	 * @throws LogStorageException if an error occured while retrieving the logs.
	 * @throws IllegalArgumentException if the the context name or task's ID are empty 
	 * strings or if the task wasn't registered by <code>addTask()</code> yet.
	 * @throws NullPointerException if any argument is <tt>null</tt>.
	 * @see #addTask(String, String)
	 */
	LogRecord[] getLogsForTask(String context, String taskID) 
	throws LogStorageException, IllegalArgumentException, NullPointerException;

	/**
	 * Gets task's log messages which belong to an interval. The interval is
	 * specified by log message indexes. To get the total number of log messages
	 * of a task, use <code>getLogCountForTask</code>.
	 * The task must've been registered in the log storage with 
	 * <code>addTask</code> before.
	 * 
	 * @param context task's context; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @param first the index of the first log message; it specifies the 
	 * beginning of the interval of log messages that will be returned. The
	 * index begins from 0.
	 * @param last the index of the last log message; it specifies the 
	 * end of the interval of log messages that will be returned. The
	 * index begins from 0.
	 * @return logs of the task.
	 * @throws LogStorageException if an error occured while retrieving the logs.
	 * @throws IllegalArgumentException if the the context name or task's ID are empty 
	 * strings or if the task wasn't registered by <code>addTask()</code> yet.
	 * @throws NullPointerException if any argument is <tt>null</tt>.
	 * @see #addTask(String, String)
	 * @see #getLogCountForTask(String, String)
	 */
	LogRecord[] getLogsForTask(String context, String taskID, long first, long last) 
	throws LogStorageException, IllegalArgumentException, NullPointerException;

	
	/**
	 * Returns the number of log messages stored for a task. The task must've 
	 * been registered in the log storage with <code>addTask</code> before.
	 * 
	 * @param context task's context; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @return number of log messages.
	 * @throws LogStorageException if an error occured while counting the log
	 * messages.
	 * @throws IllegalArgumentException if the the context name or task's ID are empty 
	 * strings or if the task wasn't registered by <code>addTask()</code> yet.
	 * @throws NullPointerException if any argument is <tt>null</tt>.
	 * @see #addTask(String, String)
	 */
	long getLogCountForTask(String context, String taskID)
	throws LogStorageException, IllegalArgumentException, NullPointerException;
	
	
	/**
	 * Stores additional standard output of the task in the log storage. This 
	 * method can be called many times to store the output of the task in smaller
	 * batches. The task must've been registered
	 * in the log storage with <code>addTask</code> before.
	 * 
	 * @param context task's context; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @param output part of the task's standard output; cannot be null.
	 * @throws LogStorageException if an error occured while storing the output.
	 * @throws IllegalArgumentException if the the context name or task's ID are empty 
	 * strings or if the task wasn't registered by <code>addTask()</code> yet.
	 * @throws NullPointerException if any argument is <tt>null</tt>.
	 * @see #addTask(String, String)
	 */
	void addStandardOutput(String context, String taskID, String output)
	throws LogStorageException, IllegalArgumentException, NullPointerException;
	
	/**
	 * Stores additional error output of the task in the log storage. This 
	 * method can be called many times to store the output of the task in smaller
	 * batches. The task must've been registered
	 * in the log storage with <code>addTask</code> before.
	 * 
	 * @param context task's context; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @param output part of the task's error output; cannot be null.
	 * @throws LogStorageException if an error occured while storing the output.
	 * @throws IllegalArgumentException if the the context name or task's ID are empty 
	 * strings or if the task wasn't registered by <code>addTask()</code> yet.
	 * @throws NullPointerException if any argument is <tt>null</tt>.
	 * @see #addTask(String, String)
	 */
	void addErrorOutput(String context, String taskID, String output)
	throws LogStorageException, IllegalArgumentException, NullPointerException;
	
	/**
	 * Creates a handle for receiving the standard output of a task. 
	 * The task must've been registered
	 * in the log storage with <code>addTask</code> before.
	 * 
	 * @param context task's context; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @return handle for retrievind the standard output.
	 * @throws LogStorageException if an error occured while creating the handle.
	 * @throws IllegalArgumentException if the the context name or task's ID are empty 
	 * strings or if the task wasn't registered by <code>addTask()</code> yet.
	 * @throws NullPointerException if any argument is <tt>null</tt>.
	 * @see #addTask(String, String)
	 */
	OutputHandle getStandardOutput(String context, String taskID)
	throws LogStorageException, IllegalArgumentException, NullPointerException;

	/**
	 * Creates a handle for receiving the error output of a task. 
	 * The task must've been registered
	 * in the log storage with <code>addTask</code> before.
	 * 
	 * @param context task's context; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @return handle for retrievind the error output.
	 * @throws LogStorageException if an error occured while creating the handle.
	 * @throws IllegalArgumentException if the the context name or task's ID are empty 
	 * strings or if the task wasn't registered by <code>addTask()</code> yet.
	 * @throws NullPointerException if any argument is <tt>null</tt>.
	 * @see #addTask(String, String)
	 */
	OutputHandle getErrorOutput(String context, String taskID)
	throws LogStorageException, IllegalArgumentException, NullPointerException;
}
