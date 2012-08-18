/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Andrej Podzimek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.clinterface.writers;

import java.io.IOException;
import java.rmi.RemoteException;

import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;

/**
 * A Writer that outputs data from TaskEntry instances.
 * 
 * @author Andrej Podzimek
 */
public final class TaskEntryWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response
	 *            The response to which all the write operations will be
	 *            relayed.
	 */
	public TaskEntryWriter(CommandLineResponse response) {
		super(response);
	}

	/**
	 * Outputs data from a task entry with a description.
	 * 
	 * @param task
	 *            The task entry to read from.
	 * @throws IOException
	 *             When it rains.
	 */
	public void sendLineDescr(TaskEntry task) throws IOException {
		sendCommon(task);
		builder().append(' ').append(quotedLiteral(task.getTaskDescription()))
				.append('\n');
		sendOut();
	}

	/**
	 * Outputs data from a task entry in a short form with no description.
	 * 
	 * @param task
	 *            The task entry to read from.
	 * @throws IOException
	 *             When it rains.
	 */
	public void sendLinePlain(TaskEntry task) throws IOException {
		sendCommon(task);
		builder().append('\n');
		sendOut();
	}

	/**
	 * Outputs common parts of the task entry.
	 * 
	 * @param task
	 *            The task entry to read from.
	 * @throws RemoteException
	 *             When it rains.
	 */
	private void sendCommon(TaskEntry task) throws RemoteException {
		builder().append(task.getTaskName()).append(' ')
				.append(task.getTaskId()).append(' ')
				.append(literal(task.getState())).append(' ')
				.append(literal(task.getHostName())).append(' ')
				.append(task.getServiceFlag() ? "SERVICE" : "JOB").append(' ')
				.append(quotedLiteral(task.getTreePath())).append(' ')
				.append(task.getContextId()).append(' ')
				.append(task.getPackageName()).append(' ')
				.append(task.getExclusivity()).append(' ')
				.append(task.getTimeSubmitted()).append(' ')
				.append(task.getTimeScheduled()).append(' ')
				.append(task.getTimeStarted()).append(' ')
				.append(task.getTimeFinished());
	}
}
