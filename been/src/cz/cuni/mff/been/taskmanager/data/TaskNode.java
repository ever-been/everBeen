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

import java.util.LinkedList;

/**
 * Class representing one Task in this <code>DataStructures</code> system.
 * 
 * @author Antonin Tomecek
 */
public class TaskNode {
	
	/* Representation of this node for outside world. */
	private TaskEntry taskEntry = null;
	
	/* Link to Context. */
	private ContextNode context = null;
	/* Link to Host Runtime. */
	private HostRuntimeNode hostRuntime = null;
	/* Link to Check Points. */
	private LinkedList<CheckPointNode> checkPoints
		= new LinkedList<CheckPointNode>();
	
	/* Data object associated with this task... */
	private TaskData taskData = null;
	
	/**
	 * Constructor of this <code>TaskNode</code> based on
	 * <code>TaskEntry</code> (linking with context and hostRuntime).
	 * 
	 * @param taskEntry <code>TaskEntry</code> containing basic description
	 * 	of Task represented by this <code>TaskNode</code>.
	 * @param context <code>ContextNode</code> to link with it.
	 * @param hostRuntime <code>HostRuntimeNode</code> to link with it or
	 * 	<code>null</code> if not known yet.
	 * @throws NullPointerException If some parameter (except hostRuntime) is
	 * 	<code>null</code>.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	protected TaskNode(TaskEntry taskEntry,
			ContextNode context, HostRuntimeNode hostRuntime) {
		/* Check input parameters. */
		if (taskEntry == null) {
			throw new NullPointerException("Parameter taskEntry is null.");
		}
		if (context == null) {
			throw new NullPointerException("Parameter context is null.");
		}
		
		/* Store taskEntry. */
			this.taskEntry = taskEntry;
		
		/* Link with context and HostRutime. */
		this.context = context;
		this.hostRuntime = hostRuntime;
	}
	
	/**
	 * Constructor of this <code>TaskNode</code> based on
	 * <code>TaskEntry</code> (linking with context).
	 * 
	 * @param taskEntry <code>TaskEntry</code> containing basic description
	 * 	of Task represented by this <code>TaskNode</code>.
	 * @param context <code>ContextNode</code> to link with it.
	 * @throws NullPointerException If some parameter is <code>null</code>.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	protected TaskNode(TaskEntry taskEntry,
			ContextNode context) {
		/* Check input parameters. */
		if (taskEntry == null) {
			throw new NullPointerException("Parameter taskEntry is null.");
		}
		if (context == null) {
			throw new NullPointerException("Parameter context is null.");
		}
		
		/* Store taskEntry. */
			this.taskEntry = taskEntry;
		
		/* Link with context. */
		this.context = context;
	}
	
	/**
	 * Get <code>TaskEntry</code> associated with this
	 * <code>TaskNode</code>.
	 * 
	 * @return <code>TaskEntry</code> associated with this
	 * 	<code>TaskNode</code>.
	 */
	protected TaskEntry getTaskEntry() {
		return this.taskEntry;
	}
	
	/**
	 * Get <code>ContextNode</code> of Context containing this Task.
	 * 
	 * @return <code>ContextNode</code> of Context containing this Task.
	 */
	protected ContextNode getContext() {
		return this.context;
	}
	
	/**
	 * Get <code>CheckPointNode</code>s of Check Points reached within this
	 * Task.
	 * 
	 * @return <code>CheckPointNode</code>s of Check Points reached within
	 * 	this Task.
	 */
	protected LinkedList<CheckPointNode> getCheckPoints() {
		return this.checkPoints;
	}
	
	/**
	 * Get <code>HostRuntimeNode</code> of Host Runtime running this Task.
	 * 
	 * @return <code>HostRuntimeNode</code> of Host Runtime running this
	 * 	Task or <code>null</code> if not set yet.
	 */
	protected HostRuntimeNode getHostRuntime() {
		return this.hostRuntime;
	}
	
	/**
	 * Set link to <code>HostRuntimeNode</code>.
	 * 
	 * @param hostRuntimeNode Link to <code>HostRuntimeNode</code>.
	 */
	protected void setHostRuntime(HostRuntimeNode hostRuntimeNode) {
		this.hostRuntime = hostRuntimeNode;
	}
	
	/**
	 * Add one <code>CheckPointNode</code> for new checkPoint of this Task.
	 * 
	 * @param checkPointNode <code>CheckPointNode</code> to add.
	 */
	protected void addCheckPoint(CheckPointNode checkPointNode) {
		this.checkPoints.add(checkPointNode);
	}
	
	/**
	 * Remove one <code>CheckPointNode</code> from this Task.
	 * 
	 * @param checkPointNode <code>CheckPointNode</code> to remove.
	 * @throws DataRuntimeException If internal error occured.
	 */
	protected void removeCheckPoint(CheckPointNode checkPointNode) {
		boolean removed = this.checkPoints.remove(checkPointNode);
		
		if (!removed) {
			throw new DataRuntimeException("CheckPointNode required to remove "
					+ "not found in list of CheckPointNode elements in this "
					+ "Context");
		}
	}
	
	/**
	 * Set <code>TaskData</code> of this task.
	 */
	protected void setTaskData(TaskData taskData) {
		this.taskData = taskData;
	}
	
	/**
	 * Get <code>TaskData</code> of this task.
	 * 
	 * @return <code>TaskData</code> of this task.
	 */
	protected TaskData getTaskData() {
		return this.taskData;
	}
}
