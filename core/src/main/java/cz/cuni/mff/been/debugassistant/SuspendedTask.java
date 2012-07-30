/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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

package cz.cuni.mff.been.debugassistant;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class representing a suspended task in debug assistant service.
 * 
 * @author Jan Tattermusch
 *
 */
public class SuspendedTask implements Serializable {
	
	private static final long serialVersionUID = -8248136296746832163L;

	/**
	 * Id of this suspended task 
	 */
	private UUID id;

	/**
	 * Task context
	 */
	private String context;
	
	/**
	 * Task ID
	 */
	private String taskId;
	
	/**
	 * Task's name
	 */
	private String name;
	
	/**
	 * host task is running on
	 */
	private String host;
	
	/**
	 * debug port
	 */
	private int port;

	/**
	 * Creates a new instance of suspended task
	 * @param context task context
	 * @param taskId task ID
	 * @param name task name
	 * @param host task host
	 * @param port task debug port
	 */
	public SuspendedTask(String context, String taskId, String name,
			String host, int port) {
		super();
		
		this.id = UUID.randomUUID();
		
		this.context = context;
		this.taskId = taskId;
		this.name = name;
		this.host = host;
		this.port = port;
	}

	public UUID getId() {
		return id;
	}

	public String getContext() {
		return context;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return "["+ name +", "+ host +", "+ port +"]"; 
	}

}
