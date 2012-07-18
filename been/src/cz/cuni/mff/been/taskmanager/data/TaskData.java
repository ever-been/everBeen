/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager.data;

import cz.cuni.mff.been.hostruntime.TaskInterface;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;

/**
 * Class storing some things associated with task.
 * 
 * @author Antonin Tomecek
 */
public class TaskData implements Cloneable {
	
	/** TaskDescriptor of task. */
	private TaskDescriptor taskDescriptor = null;
	
	/** Interface for HostRuntime's representation of running task. */
	private TaskInterface taskInterface = null;
	
	/**
	 * Constructs new <code>TaskData</code> object and sets link to
	 * taskDescriptor.
	 * 
	 * @param taskDescriptor <code>TaskDescriptor</code> of task.
	 */
	public TaskData(TaskDescriptor taskDescriptor) {
		this.taskDescriptor = taskDescriptor;
	}
	
	/**
	 * Set HostRuntime's <code>TaskInterface</code> for running task.
	 * 
	 * @param taskInterface HostRuntime's <code>TaskInterface</code> for running
	 * 	task.
	 */
	public void setTaskInterface(TaskInterface taskInterface) {
		this.taskInterface = taskInterface;
	}
	
	/**
	 * Get <code>TaskDescriptor</code> of task.
	 *  
	 * @return <code>TaskDescriptor</code> of task.
	 */
	public TaskDescriptor getTaskDescriptor() {
		return this.taskDescriptor;
	}
	
	/**
	 * Get HostRuntime's <code>TaskInterface</code> for running task.
	 * 
	 * @return HostRuntime's <code>TaskInterface</code> for running task.
	 */
	public TaskInterface getTaskInterface() {
		return this.taskInterface;
	}
	
	/**
	 * Creates and returns a copy of this object.
	 */
	@Override
	public TaskData clone() throws CloneNotSupportedException {
		TaskData taskData = (TaskData) super.clone();
		taskData.taskDescriptor = this.taskDescriptor;
		taskData.taskInterface = this.taskInterface;
		
		return taskData;
	}
}
