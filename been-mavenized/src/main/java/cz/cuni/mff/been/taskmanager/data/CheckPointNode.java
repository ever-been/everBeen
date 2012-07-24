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


/**
 * Class representing one Check Point in this <code>DataStructures</code>
 * system.
 * 
 * @author Antonin Tomecek
 */
public class CheckPointNode {
	
	/* Representation of this node for outside world. */
	private CheckPointEntry checkPointEntry = null;
	
	/* Link to Task. */
	private TaskNode task = null;
	/* Link to Context. */
	private ContextNode context = null;
	/* Link to Host Runtime. */
	private HostRuntimeNode hostRuntime = null;
	
	/**
	 * Constructor of this <code>CheckPointNode</code> based on
	 * <code>CheckPointEntry</code> (linking with task, context and
	 * hostRuntime).
	 * 
	 * @param checkPointEntry <code>CheckPointEntry</code> containing basic
	 * 	description of CheckPoint represented by this
	 * 	<code>CheckPointNode</code>.
	 * @param task <code>TaskNode</code> to link with it.
	 * @param context <code>ContextNode</code> to link with it.
	 * @param hostRuntime <code>HostRuntimeNode</code> to link with it.
	 * @throws NullPointerException Is some parameter is <code>null</code>.
	 * @throws DataRuntimeException If internal error occurred.
	 */
	protected CheckPointNode(CheckPointEntry checkPointEntry,
			TaskNode task, ContextNode context, HostRuntimeNode hostRuntime) {
		/* Check input parameters. */
		if (checkPointEntry == null) {
			throw new NullPointerException("Parameter checkPointEntry is "
					+ "null.");
		}
		if (task == null) {
			throw new NullPointerException("Parameter task is null.");
		}
		if (context == null) {
			throw new NullPointerException("Parameter context is null.");
		}
//		if (hostRuntime == null) {
//			throw new NullPointerException("Parameter hostRuntime is null");
//		}
		
		/* Store checkPointEntry. */
			this.checkPointEntry = checkPointEntry;
		
		/* Link with task, context and hostRuntime. */
		this.task = task;
		this.context = context;
		this.hostRuntime = hostRuntime;
	}
	
	/**
	 * Get <code>CheckPointEntry</code> associated with this
	 * <code>CheckPointNode</code>.
	 * 
	 * @return <code>CheckPointEntry</code> associated with this
	 * 	<code>CheckPointNode</code>.
	 */
	protected CheckPointEntry getCheckPointEntry() {
		return this.checkPointEntry;
	}
	
	/**
	 * Get <code>TaskNode</code> of Task which reached this Check Point.
	 * 
	 * @return <code>TaskNode</code> of Task which reached this Check Point.
	 */
	protected TaskNode getTask() {
		return this.task;
	}
	
	/**
	 * Get <code>ContextNode</code> of Context containing this Check Point.
	 * 
	 * @return <code>ContextNode</code> of Context containing this Check Point.
	 */
	protected ContextNode getContext() {
		return this.context;
	}
	
	/**
	 * Get <code>HostRuntimeNode</code> of Host Runtime on which this
	 * Check Point was reached.
	 * 
	 * @return <code>HostRuntimeNode</code> of Host Runtime of which this
	 * 	Check Point was reached.
	 */
	protected HostRuntimeNode getHostRuntime() {
		return this.hostRuntime;
	}
}
