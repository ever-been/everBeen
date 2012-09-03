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

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Class representing one entry (checkPoint) in {@link
 * cz.cuni.mff.been.taskmanager.TaskManagerImplementation
 * <code>Task Manager</code>}.
 * 
 * @author Antonin Tomecek
 */
public class CheckPointEntry implements Cloneable, Serializable {
	
	private static final long	serialVersionUID	= 6955519730399495754L;

	/**
	 * Regular expression for match <code>name</code>.
	 */
	public static final String REGEXP_NAME = "^[a-zA-Z_0-9. -]+$";
	
	/**
	 * Regular expression for match <code>taskId</code>.
	 */
	public static final Pattern REGEXP_TASK_ID = TaskEntryImplementation.REGEXP_TASK_ID;
	
	/**
	 * Regular expression for match <code>contextId</code>.
	 */
	public static final Pattern REGEXP_CONTEXT_ID = ContextEntry.REGEXP_CONTEXT_ID;
	
	/**
	 * Regular expression for match <code>hostName</code>.
	 */
	public static final Pattern REGEXP_HOST_NAME = HostRuntimeEntry.REGEXP_HOST_NAME;
	
	/* Current time on system creating this entry in milliseconds.
	 * Difference between current time and midnight, January 1, 1970 UTC. */
	private long currentTimeMillis;
	
	/* Time (in milliseconds) when checkPoint was reached. */
	private long timeReached = 0;
	
	/* Name of this checkPoint. */
	private String name = "";
	
	/* ID of task which reached this checkPoint. */
	private String taskId = "";
	
	/* ID of context containing this checkPoint. */
	private String contextId = "";
	
	/* Name of host on which this checkPoint was reached. */
	private String hostName = "";
	
	/* Some magic object from outside... (no one understands to this). */
	private Serializable magicObject = null;
	
	/**
	 * Creates a new (empty - default inicialized)
	 * <code>CheckPointEntry</code>.
	 * This constructor is neded by <code>Serializable</code>.
	 */
	public CheckPointEntry() {
		this.currentTimeMillis = System.currentTimeMillis();
	}
	
	/**
	 * Creates a new (and inicialized) <code>CheckPointEntry</code>.
	 * 
	 * @param taskId ID of task which reached this checkPoint.
	 * @param contextId ID of context containing this checkPoint.
	 * @param hostName Name of host on which this checkPoint was reached.
	 * @param name Name of this checkPoint.
	 * @param magicObject Some magic object from outside... (no one understands
	 * 	to this).
	 */
	public CheckPointEntry(String name, String taskId, String contextId,
			String hostName, Serializable magicObject) {
		this.currentTimeMillis = System.currentTimeMillis();
		
		/* Inicialization of following (protected write access) variables with
		 * default values:
		 * 	timeReached.
		 */
		this.initInternals(0);
		
		/* Inicialization of variables with public write access. */
		this.setName(name);
		this.setTaskId(taskId);
		this.setContextId(contextId);
		this.setHostName(hostName);
		this.setMagicObject(magicObject);
	}
	
	/**
	 * Initialization of variables with protected write access.
	 * 
	 * @param timeReached Time (in milliseconds) when checkPoint was reached.
	 */
	protected void initInternals(long timeReached) {
		this.setTimeReached(timeReached);
	}
	
	/**
	 * Set time (in milliseconds) when checkPoint was reached.
	 * 
	 * @param timeReached Time (in milliseconds) when checkPoint was reached.
	 */
	protected void setTimeReached(long timeReached) {
		this.timeReached = timeReached;
	}
	
	/**
	 * Set name of this checkPoint.
	 * 
	 * @param type Type of this checkPoint.
	 * @throws IllegalArgumentException If <code>type</code> is not valid.
	 */
	public void setName(String type) {
		if (Pattern.matches(REGEXP_NAME, type)) {
			this.name = type;
		} else {
			throw new IllegalArgumentException("type is not valid");
		}
	}
	
//	/**
//	 * Set value of this checkPoing.
//	 * 
//	 * @param value Value of this checkPoint.
//	 * @throws IllegalArgumentException If <code>value</code> is not valid.
//	 */
//	public void setValue(Serializable value) {
//		this.magicObject = value;
//	}
	
	/**
	 * Set ID of task which reached this checkPoint.
	 * 
	 * @param taskId ID of task which reached this checkpoint.
	 * @throws IllegalArgumentException If <code>taskId</code> is not valid.
	 */
	public void setTaskId(String taskId) {
		if (REGEXP_TASK_ID.matcher(taskId).matches()) {
			this.taskId = taskId;
		} else {
			throw new IllegalArgumentException("taskId is not valid");
		}
	}
	
	/**
	 * Set ID of context containing this checkPoint.
	 * 
	 * @param contextId ID of context containing this checkPoint.
	 * @throws IllegalArgumentException If <code>contextId</code> is not valid.
	 */
	public void setContextId(String contextId) {
		if (REGEXP_CONTEXT_ID.matcher(contextId).matches()) {
			this.contextId = contextId;
		} else {
			throw new IllegalArgumentException("contextId is not valid");
		}
	}
	
	/**
	 * Set name of host on which this checkPoint was reached.
	 * 
	 * @param hostName Name of host on which this checkPoint was reached.
	 * @throws IllegalArgumentException If <code>hostName</code> is not valid.
	 */
	public void setHostName(String hostName) {
		if (REGEXP_HOST_NAME.matcher(hostName).matches()) {
			this.hostName = hostName;
		} else {
			throw new IllegalArgumentException("hostName is not valid");
		}
	}
	
	/**
	 * Set some magic object from outside... (no one understands to this).
	 * 
	 * @param magicObject Some magic object from outside... (no one understands
	 * 	to this).
	 */
	public void setMagicObject(Serializable magicObject) {
		this.magicObject = magicObject;
	}
	
	/**
	 * Get time of creation of this object (according with system creating this
	 * entry) in milliseconds. (Difference between current time and midnight,
	 * January 1, 1970 UTC.
	 * 
	 * @return Time of creation of this object.
	 */
	public long getCurentTime() {
		return this.currentTimeMillis;
	}
	
	/**
	 * Get time (in milliseconds) when checkPoint was reached.
	 * 
	 * @return Time (in milliseconds) when checkPoint was reached.
	 */
	public long getTimeReached() {
		return this.timeReached;
	}
	
	/**
	 * Get name of this checkPoint.
	 * 
	 * @return Type of this checkPoint.
	 */
	public String getName() {
		return this.name;
	}
	
//	/**
//	 * Get value of this checkPoint.
//	 * 
//	 * @return Value of this checkPoint.
//	 */
//	public Serializable getValue() {
//		return this.magicObject;
//	}
	
	/**
	 * Get ID of task which reached this checkPoint.
	 * 
	 * @return ID of task which reached this checkPoint.
	 */
	public String getTaskId() {
		return this.taskId;
	}
	
	/**
	 * Get ID of context containing this checkPoint.
	 * 
	 * @return ID of context containing this checkPoint.
	 */
	public String getContextId() {
		return this.contextId;
	}
	
	/**
	 * Get name of host on which this checkPoint was reached.
	 * 
	 * @return Name of host on which this checkPoint was reached.
	 */
	public String getHostName() {
		return this.hostName;
	}
	
	/**
	 * Get some magic object from outside... (no one understands to this).
	 * 
	 * @return Some magic object from outside... (no one understands to this).
	 */
	public Serializable getMagicObject() {
		return this.magicObject;
	}
	
	/**
	 * Creates and returns a copy of this object.
	 * Does not copy <code>currentTime</code> but uses real current time to
	 * initialise it as used when constructing new object (i.e. clone is not
	 * fully identical with this object!).
	 */
	@Override
	public CheckPointEntry clone() throws CloneNotSupportedException {
		CheckPointEntry checkPointEntry = (CheckPointEntry) super.clone();
		
		/* Set current time. */
		checkPointEntry.currentTimeMillis = System.currentTimeMillis();
		
		/* Clone magicObject. */
		checkPointEntry.setMagicObject(
				CloneSerializable.cloneSerializable(this.magicObject));
		
		return checkPointEntry;
	}
}
