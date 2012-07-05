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
 * Class representing one Host Runtime in this <code>DataStructures</code>
 * system.
 * 
 * @author Antonin Tomecek
 */
class HostRuntimeNode {
	
	/* Representation of this node for outside world. */
	private HostRuntimeEntry hostRuntimeEntry = null;
	
	/* Link to Tasks. */
	private LinkedList<TaskNode> tasks = new LinkedList<TaskNode>();
	/* Link to Check Points. */
	private LinkedList<CheckPointNode> checkPoints
		= new LinkedList<CheckPointNode>();
	
	/**
	 * Constructor of this <code>HostRuntimeNode</code> based on
	 * <code>HostRuntimeEntry</code>.
	 * 
	 * @param hostRuntimeEntry <code>HostRuntimeEntry</code> containing basic
	 * 	description of HostRuntime represented by this
	 * 	<code>HostRuntimeNode</code>.
	 * @throws NullPointerException If some parameter is <code>null</code>.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	protected HostRuntimeNode(HostRuntimeEntry hostRuntimeEntry) {
		/* Check input parameters. */
		if (hostRuntimeEntry == null) {
			throw new NullPointerException("Parameter hostRuntimeEntry is "
					+ "null.");
		}
		
		/* Store hostRuntimeEntry. */
			this.hostRuntimeEntry = hostRuntimeEntry;
	}
	
	/**
	 * Test if this hostRuntime is empty (contains no element).
	 * 
	 * @return <code>true</code> when hostRuntime is empty, <code>false</code>
	 * 	otherwise.
	 */
	protected boolean isEmpty() {
		return (tasks.size() == 0);
	}
	
	/**
	 * Get <code>HostRuntimeEntry</code> associated with this
	 * <code>HostRuntimeNode</code>.
	 * 
	 * @return <code>HostRuntimeEntry</code> associated with this
	 * 	<code>HostRuntimeNode</code>.
	 */
	protected HostRuntimeEntry getHostRuntimeEntry() {
		return this.hostRuntimeEntry;
	}
	
	/**
	 * Get <code>TaskNode</code>s of Tasks running on this Host Runtime.
	 * 
	 * @return <code>TaskNode</code>s of Tasks running on this Host
	 * 	Runtime.
	 */
	protected LinkedList<TaskNode> getTasks() {
		return this.tasks;
	}
	
	/**
	 * Get <code>CheckPointNode</code>s of Check Points reached within this
	 * Host Runtime.
	 * 
	 * @return <code>CheckPointNode</code>s of Check Points reached within
	 * 	this Host Runtime.
	 */
	protected LinkedList<CheckPointNode> getCheckPoints() {
		return this.checkPoints;
	}
	
	/**
	 * Add one <code>TaskNode</code> for new task in this Host Runtime.
	 * 
	 * @param taskNode <code>TaskNode</code> to add.
	 */
	protected void addTask(TaskNode taskNode) {
		this.tasks.add(taskNode);
	}
	
	/**
	 * Add one <code>CheckPointNode</code> for new checkPoint on this Host
	 * Runtime.
	 * 
	 * @param checkPointNode <code>CheckPointNode</code> to add.
	 */
	protected void addCheckPoint(CheckPointNode checkPointNode) {
		this.checkPoints.add(checkPointNode);
	}
	
	/**
	 * Remove one <code>TaskNode</code> from this Host Runtime.
	 * 
	 * @param taskNode <code>TaskNode</code> to remove.
	 * @throws IllegalArgumentException In <code>TaskNode</code> required
	 * 	to remove not found in this Host Runtime.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	protected void removeTask(TaskNode taskNode) {
		boolean removed = this.tasks.remove(taskNode);
		
		if (!removed) {
			throw new DataRuntimeException("TaskNode required to remove not "
					+ "found list of TaskNode elements in this Host Runtime");
		}
	}
	
	/**
	 * Remove one <code>CheckPointNode</code> from this Host Runtime.
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
}
