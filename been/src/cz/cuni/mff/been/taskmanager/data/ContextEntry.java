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
 * Class representing one entry (context) in {@link
 * cz.cuni.mff.been.taskmanager.TaskManagerImplementation
 * <code>Task Manager</code>}.
 * 
 * @author Antonin Tomecek
 */
public class ContextEntry implements Cloneable, Serializable {
	
	private static final long	serialVersionUID	= -8591690131481196620L;

	/**
	 * Regular expression for match <code>contextId</code>.
	 */
	public static final Pattern REGEXP_CONTEXT_ID = Pattern.compile("^[a-zA-Z_0-9 -]+$");
	
//	/**
//	 * Regular expression for match <code>contextName</code>.
//	 */
//	public static final String REGEXP_CONTEXT_NAME = "^[a-zA-Z_0-9,. ]+$";
//	
//	/**
//	 * Regular expression for match <code>contextDescription</code>.
//	 */
//	public static final String REGEXP_CONTEXT_DESCRIPTION
//		= "^[a-zA-Z_0-9,. ]+$";

	/* Current time on system creating this entry in milliseconds.
	 * Difference between current time and midnight, January 1, 1970 UTC. */
	private long currentTimeMillis;
	
	/* ID of this context. */
	private String contextId = "";
	
	/* Human readable name of this context. */
	private String contextName = "";
	
	/* Human readable description of this context. */
	private String contextDescription = "";
	
	/* Number of finished tasks that will be kept in context without sweeping them
	 * by task manager. -1 means no tasks will be cleaned. */
	private int finishedTasksKept = -1;
	
	/* Flag telling if this context is open (or closed). */
	private boolean open = true;
	
	/* Some magic object from outside... (no one understands to this). */
	private Serializable magicObject = null;
	
	/**
	 * Creates a new (empty - default inicialized) <code>ContextEntry</code>.
	 * This constructor is neded by <code>Serializable</code>.
	 */
	public ContextEntry() {
		this.currentTimeMillis = System.currentTimeMillis();
		
//		this.contextId = "";
//		this.contextName = "";
//		this.contextDescription = "";
//		this.open = true;
//		this.magicObject = null;
	}
	
	/**
	 * Creates a new (and initialized) <code>ContextEntry</code>.
	 * 
	 * @param contextId ID of this context.
	 * @param contextName Human readable name of this context (or
	 * 	<code>null</code>).
	 * @param contextDescription Human readable description of this context (or
	 * 	<code>null</code>).
	 * @param magicObject Some magic object from outside... (no one understands
	 * 	to this).
	 */
	public ContextEntry(String contextId, String contextName,
			String contextDescription, Serializable magicObject) {
		this.currentTimeMillis = System.currentTimeMillis();
		
		this.setContextId(contextId);
		if (contextName != null) {
			this.setContextName(contextName);
		}
		if (contextDescription != null) {
			this.setContextDescription(contextDescription);
		}
//		this.open = true;
		if (magicObject != null) {
			this.setMagicObject(magicObject);
		}
	}
	
	/**
	 * Creates a new (and initialized) <code>ContextEntry</code>.
	 * 
	 * @param contextId ID of this context.
	 * @param contextName Human readable name of this context (or
	 * 	<code>null</code>).
	 * @param contextDescription Human readable description of this context (or
	 * 	<code>null</code>).
	 * @param magicObject Some magic object from outside... (no one understands
	 * 	to this).
	 * @param finishedTasksKept Number of finished tasks that will be kept in context without cleaning them
	 * by task manager. -1 means no tasks will be cleaned.
	 */
	public ContextEntry(String contextId, String contextName,
			String contextDescription, Serializable magicObject, int finishedTasksKept) {
		this.currentTimeMillis = System.currentTimeMillis();
		
		this.setContextId(contextId);
		if (contextName != null) {
			this.setContextName(contextName);
		}
		if (contextDescription != null) {
			this.setContextDescription(contextDescription);
		}
//		this.open = true;
		if (magicObject != null) {
			this.setMagicObject(magicObject);
		}
		
		this.setFinishedTasksKept(finishedTasksKept);
	}
	
	/**
	 * Set ID of this context.
	 * 
	 * @param contextId ID of this context.
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
	 * Set human readable name of this context.
	 * 
	 * @param contextName Human readable name of this context.
	 * @throws IllegalArgumentException If <code>contextName</code> is not
	 * 	valid.
	 */
	public void setContextName(String contextName) {
//		if (Pattern.matches(REGEXP_CONTEXT_NAME, contextName)) {
		if (contextName != null) {
			this.contextName = contextName;
		} else {
			throw new IllegalArgumentException("contextName is not valid");
		}
	}
	
	/**
	 * Set human readable description of this context.
	 * 
	 * @param contextDescription Human readable description of this context.
	 * @throws IllegalArgumentException If <code>contextDescription</code> is
	 * 	not valid.
	 */
	public void setContextDescription(String contextDescription) {
//		if (Pattern.matches(REGEXP_CONTEXT_DESCRIPTION, contextDescription)) {
		if (contextDescription != null) {
			this.contextDescription = contextDescription;
		} else {
			throw new IllegalArgumentException("contextDescription is not "
					+ "valid");
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
	 * Close this context.
	 * 
	 * @throws IllegalStateException If context is already closed.
	 */
	protected void close() {
		if (!this.open) {
			throw new IllegalStateException("Context is already closed");
		}
		this.open = false;
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
	 * Get ID of this context.
	 * 
	 * @return ID of this context.
	 */
	public String getContextId() {
		return this.contextId;
	}
	
	/**
	 * Get human readable name of this context.
	 * 
	 * @return Human readable name of this context.
	 */
	public String getContextName() {
		return this.contextName;
	}
	
	/**
	 * Get human readable description of this context.
	 * 
	 * @return Human readable description of this context.
	 */
	public String getContextDescription() {
		return this.contextDescription;
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
	 * Test if context is still open.
	 * 
	 * @return <code>true</code> if context is still open <code>false</code>
	 * 	otherwise.
	 */
	public boolean isOpen() {
		return this.open;
	}
	
	/**
	 * Creates and returns a copy of this object.
	 * Does not copy <code>currentTime</code> but uses real current time to
	 * inicialize it as used when constructing new object (i.e. clone is not
	 * fully identical with this object!).
	 */
	@Override
	public ContextEntry clone() throws CloneNotSupportedException {
		ContextEntry contextEntry = (ContextEntry) super.clone();
		
		/* Set current time. */
		//contextEntry.currentTimeMillis = System.currentTimeMillis();
		
		/* Clone magicObject. */
		contextEntry.setMagicObject(
				CloneSerializable.cloneSerializable(this.magicObject));
		
		return contextEntry;
	}

	/**
	 * Sets number of finished tasks that will be kept in context without cleaning them
	 * by task manager. -1 means no tasks will be cleaned.
	 * 
	 * @param finishedTasksKept
	 */
	public void setFinishedTasksKept(int finishedTasksKept) {
		this.finishedTasksKept = finishedTasksKept;
	}

	/**
	 * Number of finished tasks that will be kept in context without cleaning them
	 * by task manager. -1 means no tasks will be cleaned.
	 * @return number of finished tasks kept
	 */
	public int getFinishedTasksKept() {
		return finishedTasksKept;
	}
	
}
