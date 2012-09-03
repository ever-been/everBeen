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
package cz.cuni.mff.been.taskmanager;

import java.io.Serializable;

public class Dependency implements Serializable {
	
	private static final long	serialVersionUID	= 8642687662809497471L;

	/* TaskID of prerequisite task. */
	private String taskId = null;
	
	/* Type of reached check point in prerequisite task. */
	private String type = null;
	
	/* Value of reached check point in prerequisite task. */
	private Serializable value = null;
	
	/**
	 * Constructor needed by Serializable.
	 */
	public Dependency() {
	}
	
	/**
	 * Creates a new instance of Dependency class.
	 * 
	 * @param taskID TaskID of prerequisite task.
	 * @param type Type of reached check point in prerequisite task.
	 * @param value Value of reached check point in prerequisite task.
	 */
	public Dependency(String taskID, String type, Serializable value) {
		this.taskId = taskID;
		this.type = type;
		this.value = value;
	}

	/**
	 * Constructs new dependency on task identified by <code>taskID</code> and
	 * checkpoint <code>type</code> which may be reached with arbitrary value.
	 * 
	 * @param taskID TaskId of dependency
	 * @param type name of checkpoint
	 */
	public Dependency(String taskID, String type) {
		this(taskID, type, null);
	}

	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @return A clon of this instance.
	 */
	@Override
	public Object clone() {
		return new Dependency(this.taskId, this.type, this.value);
	}

	/**
	 * Sets TaskID of prerequisite task.
	 * 
	 * @param taskID TaskID of prerequisite task.
	 */
	public void setTaskId(String taskID) {
		this.taskId = taskID;
	}

	/**
	 * Sets type of reached check point in prerequisite task.
	 * 
	 * @param type Type of reached check point in prerequisite
	 * 	task.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Sets value of reached check point in prerequisite task.
	 * 
	 * @param value Value of reached check point in prerequisite
	 * 	task.
	 */
	public void setValue(Serializable value) {
		this.value = value;
	}

	/**
	 * Gets TaskID of prerequisite task.
	 * 
	 * @return TaskID of prerequisite task.
	 */
	public String getTaskId() {
		return this.taskId;
	}

	/**
	 * Gets type of reached check point in prerequisite task.
	 * 
	 * @return Type of reached check point in prerequisite task.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Gets value of reached check point in prerequisite task.
	 * 
	 * @return Value of reached check point in prerequisite task.
	 */
	public Serializable getValue() {
		return this.value;
	}
}
