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

/**
 * Class representing checkpoint in BEEN
 * 
 * @author Antonin Tomecek
 */
public class CheckPoint implements Serializable {

	private static final long	serialVersionUID	= 945992460515128288L;

	private String taskId = null;
	private String contextId = null;
	private String name = null;
	private Serializable value = null;
	private String hostName = "localhost"; // OMFG
	
	/**
	 * Allocate new CheckPoint with specified parameters.
	 * 
	 * @param taskId ID of task.
	 * @param contextId ID of context.
	 * @param name Name of checkpoint or null.
	 * @param value Value of checkpoint or null.
	 */
	public CheckPoint(String taskId, String contextId,
			String name, String value) {
		this.taskId = taskId;
		this.contextId = contextId;
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Allocate new CheckPoint with specified parameters.
	 * 
	 * @param taskId ID of task.
	 * @param contextId ID of context.
	 * @param type Type of checkpoint or null.
	 * @param value Value of checkpoint or null.
	 */
	public CheckPoint(String taskId, String contextId,
			String type, Serializable value) {
		this.taskId = taskId;
		this.contextId = contextId;
		this.name = type;
		this.value = value;
	}
	
	/**
	 * Set name of host running Host Runtime.
	 * 
	 * @param hostName Name of host running Host Runtime.
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	/**
	 * Get ID of task.
	 * 
	 * @return ID of task.
	 */
	public String getTaskId() {
		return this.taskId;
	}
	
	/**
	 * Get ID of context.
	 * 
	 * @return ID of context.
	 */
	public String getContextId() {
		return this.contextId;
	}
	
	/**
	 * Get name of checkpoint.
	 * 
	 * @return Name of checkpoint.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get value of checkpoint.
	 * 
	 * @return Value of checkpoint.
	 */
	public Serializable getValue() {
		return this.value;
	}
		
	/**
	 * Get name of host running Host Runtime.
	 * 
	 * @return Name of host running Host Runtime.
	 */
	public String getHostName() {
		return this.hostName;
	}
	
	@Override
	public String toString() {
		return "CheckPoint(" 
			+ getTaskId() + "," + getName() + "," + getValue() + ")";
	}
}

