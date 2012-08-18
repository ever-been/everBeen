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

import java.rmi.Naming;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;

/**
 * Does some simple tests on the log storage.
 * 
 * @author Jaroslav Urban
 */
public class LogStorageTest {
	private static final boolean GET_LOGS = true;
	private static final boolean GET_OUTPUT = true;
	private static final String TASK_ID = "task-2";
	private static final String CONTEXT = TaskManagerInterface.SYSTEM_CONTEXT_ID;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TaskManagerInterface taskManager = 
				(TaskManagerInterface) Naming.lookup(RMI.URL_PREFIX + "/been/taskmanager");
			
			if (GET_LOGS) {
				// get the logs of some task

				System.out.println("LOGS: ");
				System.out.println();
				
				long count = taskManager.getLogCountForTask(CONTEXT, TASK_ID);
				System.out.println("LOG MESSAGE COUNT: " + count);

				LogRecord[] logRecords = taskManager.getLogsForTask(CONTEXT, 
						TASK_ID,
						40000,
						42000);
				
				for (LogRecord logRecord : logRecords) {
					System.out.println();
					System.out.println("Context: " + logRecord.getContext());
					System.out.println("Task ID: " + logRecord.getTaskID());
					System.out.println("Hostname: " + logRecord.getHostname());

					SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateTimeInstance();
					format.applyPattern("dd.MM.yyyy HH:mm:ss.SSS");
					System.out.println("Timestamp: " + format.format(logRecord.getTimestamp()));

					System.out.println("Log level: " + logRecord.getLevel());
					System.out.println("Message: " + logRecord.getMessage());
				}
				
				System.out.println("LENGTH: " + logRecords.length);
				
//
//				// check registration of some tasks and contexts
//
//				System.out.println();
//				System.out.println("\"log-tester-1\" task registered: " 
//						+ taskManager.isTaskRegistered(CONTEXT, TASK_ID));
//				System.out.println("\"funky-task\" task registered: " 
//						+ taskManager.isTaskRegistered(CONTEXT, "funky-task"));
//				System.out.println("\"system\" context registered: " 
//						+ taskManager.isContextRegistered(CONTEXT));
//				System.out.println("\"funky\" context registered: " 
//						+ taskManager.isContextRegistered("funky"));
//				
//				System.out.println();
			}
			if (GET_OUTPUT) {
				System.out.println("STDOUT: ");
				System.out.println();
				
				OutputHandle handle = taskManager.getStandardOutput(CONTEXT, TASK_ID);
				System.out.println("STDOUT LENGTH: " + handle.getLineCount());
				
				
//				System.out.println();
//
//				System.out.println("STDERR: ");
//				System.out.println();
//
//				handle = taskManager.getErrorOutput(CONTEXT, TASK_ID);
//				while ((output = handle.getNextLines(LogStorage.OUTPUT_TRANSFER_LINECOUNT))
//						!= null) {
//					System.out.print(output);
//				}
				
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
