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
 * Class representing one Context in this <code>DataStructures</code>
 * system.
 * 
 * @author Antonin Tomecek
 */
class ContextNode {
	
	/* Representation of this node for outside world. */
	private ContextEntry contextEntry = null;
	
	/* Link to Tasks. */
	private LinkedList<TaskNode> tasks = new LinkedList<TaskNode>();
	/* Link to Check Points. */
	private LinkedList<CheckPointNode> checkPoints
		= new LinkedList<CheckPointNode>();
	
	/**
	 * Constructor of this <code>ContextNode</code> based on
	 * <code>ContextEntry</code>.
	 * 
	 * @param contextEntry <code>ContextEntry</code> containing basic
	 * 	description of Context represented by this
	 * 	<code>ContextNode</code>.
	 * @throws NullPointerException If some parameter is <code>null</code>.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	protected ContextNode(ContextEntry contextEntry) {
		/* Check input parameters. */
		if (contextEntry == null) {
			throw new NullPointerException("Parameter contextEntry is null.");
		}
		
		/* Store contextEntry. */
		this.contextEntry = contextEntry;
	}
	
	/**
	 * Test if this context is empty (contains no element).
	 * 
	 * @return <code>true</code> when context is empty, <code>false</code>
	 * 	otherwise.
	 */
	protected boolean isEmpty() {
		return (tasks.size() == 0);
	}
	
	/**
	 * Get <code>ContextEntry</code> associated with this
	 * <code>ContextNode</code>.
	 * 
	 * @return <code>ContextEntry</code> associated with this
	 * 	<code>ContextNode</code>.
	 */
	protected ContextEntry getContextEntry() {
		return this.contextEntry;
	}
	
	/**
	 * Get <code>TaskNode</code>s of Tasks contained within this Context.
	 * 
	 * @return <code>TaskNode</code>s of Tasks contained within this
	 * 	Context.
	 */
	protected LinkedList<TaskNode> getTasks() {
		return this.tasks;
	}
	
	/**
	 * Get <code>CheckPointNode</code>s of Check Points reached within this
	 * Context.
	 * 
	 * @return <code>CheckPointNode</code>s of Check Points reached within
	 * 	this Context.
	 */
	protected LinkedList<CheckPointNode> getCheckPoints() {
		return this.checkPoints;
	}
	
	/**
	 * Add one <code>TaskNode</code> for new task in this Context.
	 * 
	 * @param taskNode <code>TaskNode</code> to add.
	 */
	protected void addTask(TaskNode taskNode) {
		this.tasks.add(taskNode);
	}
	
	/**
	 * Add one <code>CheckPointNode</code> for new checkPoint in this Context.
	 * 
	 * @param checkPointNode <code>CheckPointNode</code> to add.
	 */
	protected void addCheckPoint(CheckPointNode checkPointNode) {
		this.checkPoints.add(checkPointNode);
	}
	
	/**
	 * Remove one <code>TaskNode</code> from this Context.
	 * 
	 * @param taskNode <code>TaskNode</code> to remove.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	protected void removeTask(TaskNode taskNode) {
		boolean removed = this.tasks.remove(taskNode);
		
		if (!removed) {
			throw new DataRuntimeException("TaskNode required to remove not "
					+ "found in list of TaskNode elements in this Context");
		}
	}
	
	/**
	 * Remove one <code>CheckPointNode</code> from this Context.
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
