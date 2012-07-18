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

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;

/**
 * Class containing utility functions for working with logs. These functions are
 * used in several modules of the web interface.
 * 
 * @author David Majda
 */
public class LogUtils {
	/**
	 * Takes a list of <code>LogRecord</code> arrays and aggregates them into one
	 * <code>LogRecord</code> array, sorted by timestamp.
	 * 
	 * @param logRecordsList list of <code>LogRecord</code> arrays to aggregate
	 * @return aggregated logs
	 */
	public static LogRecord[] aggregateLogs(List<LogRecord[]> logRecordsList) {
		/* The algorithm for generating the aggregate logs is simple: put all logs
		 * in one array and sort that array by timestamp. If performance will be
		 * a problem, merge-based approach could be used instead in the future.
		 */ 
		
		/* Get the size of result array. */
		int resultSize = 0;
		for (LogRecord[] logRecords: logRecordsList) {
			resultSize += logRecords.length;
		}
		
		/* Put the records in one array. */
		LogRecord[] result = new LogRecord[resultSize];
		int index = 0;
		for (LogRecord[] logRecords: logRecordsList) {
			for (LogRecord logRecord: logRecords) {
				result[index++] = logRecord;
			}
		}
		
		/* Sort the resulting array by timestamp. */
		Arrays.sort(result, new Comparator<LogRecord>() {
			public int compare(LogRecord a, LogRecord b) {
				return a.getTimestamp().compareTo(b.getTimestamp());
			}
		});
		
		return result;
	}
	
	/**
	 * Retrieves logs for given tasks from the Task Manager and aggregates them
	 * into one <code>LogRecord</code> array, sorted by timestamp.  
	 * 
	 * @param tasks tasks whose logs will be retrieved 
	 * @return aggregated logs
	 * @throws RemoteException when something in RMI goes bad
	 * @throws LogStorageException if an error occurred while retrieving the logs
	 */
	public static LogRecord[] getLogRecordsForTasks(TaskManagerInterface taskManager,
			TaskEntry[] tasks) throws RemoteException, LogStorageException {
		List<LogRecord[]> logRecordsList = new LinkedList<LogRecord[]>();
		for (TaskEntry task: tasks) {
			logRecordsList.add(taskManager.getLogsForTask(
					task.getContextId(),
					task.getTaskId()
			));
		}
		
		return aggregateLogs(logRecordsList);
	}
	
	/**
	 * Private constructor so than no instances can be created.
	 */
	private LogUtils() {
	}
}
