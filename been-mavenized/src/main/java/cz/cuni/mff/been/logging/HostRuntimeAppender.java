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

import java.rmi.RemoteException;
import java.util.Date;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import cz.cuni.mff.been.hostruntime.TasksPortInterface;
import cz.cuni.mff.been.task.Task;

/**
 * Appender sends log events to Host Runtime.
 * @author Jaroslav Urban
 */
public class HostRuntimeAppender extends AppenderSkeleton {
	private TasksPortInterface tasksPort;

	/**
	 * 
	 * Allocates a new <code>HostRuntimeAppender</code> object.
	 *
	 * @param task
	 */
	public HostRuntimeAppender(Task task) {
		this.tasksPort = task.getTasksPort();
	}

	@Override
	protected void append(LoggingEvent event) {
		try {
			tasksPort.log(LogLevel.getInstance(
					event.getLevel()), 
					new Date(event.timeStamp), 
					event.getRenderedMessage());
		}
		catch (RemoteException e) {
			System.err.println("Cannot send a log message to Host Runtime: "
					+ event.getLevel() 
					+ " " + new Date(event.timeStamp)
					+ " " + event.getRenderedMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}
}
