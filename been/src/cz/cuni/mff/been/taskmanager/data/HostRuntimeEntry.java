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
 * Class representing one entry (hostRuntime) in {@link
 * cz.cuni.mff.been.taskmanager.TaskManagerImplementation
 * <code>Task Manager</code>}.
 * 
 * @author Antonin Tomecek
 */
public class HostRuntimeEntry implements Cloneable, Serializable {
	
	private static final long	serialVersionUID	= 27113070360465936L;

	/**
	 * Regular expression for match <code>hostName</code>.
	 */
	public static final Pattern REGEXP_HOST_NAME = Pattern.compile("^[a-zA-Z_0-9.-]+$");
	
	/* Current time on system creating this entry in milliseconds.
	 * Difference between current time and midnight, January 1, 1970 UTC. */
	private long currentTimeMillis;
	
	/* Name of host with this Host Runtime. */
	private String hostName = "";
	
	/* ID of context with reservation of this Host Runtime (task's exclusivity)
	 * null ... for any
	 * "" ... for none (Host Runtime is running exclusive task)
	 */
	private String reservation = null;
	
	/** Current load of the host runtime. */
	private int loadUnits = 0;
	
	/**
	 * Creates a new (empty - default inicialized)
	 * <code>HostRuntimeEntry</code>.
	 * This constructor is neded by <code>Serializable</code>.
	 */
	public HostRuntimeEntry() {
		this.currentTimeMillis = System.currentTimeMillis();
		
//		this.hostName = "";
	}
	
	/**
	 * Creates a new (and inicialized) <code>HostRuntimeEntry</code>.
	 * 
	 * @param hostName Name of host with this <code>Host Runtime</code>.
	 */
	public HostRuntimeEntry(String hostName) {
		this.currentTimeMillis = System.currentTimeMillis();
		
		/* Inicialization of variables with public write access. */
		this.setHostName(hostName);
	}
	
	/**
	 * Set name of host with this <code>Host Runtime</code>.
	 * 
	 * @param hostName Name of host with this <code>Host Runtime</code>.
	 */
	public void setHostName(String hostName) {
		if (REGEXP_HOST_NAME.matcher(hostName).matches()) {
			this.hostName = hostName;
		} else {
			throw new IllegalArgumentException("hostName is not valid");
		}
	}
	
	/**
	 * Set ID of context with reservation of this Host Runtime (task's
	 * exclusivity).
	 * 
	 * @param reservation ID of context, <code>null</code> for any,
	 * 	<code>""</code> for none.
	 */
	public void setReservation(String reservation) {
		if (
			(reservation == null) || (reservation.equals("")) ||
			(ContextEntry.REGEXP_CONTEXT_ID.matcher(reservation).matches())
		) {
			this.reservation = reservation;
		}
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
	 * Get name of host with this <code>Host Runtime</code>.
	 * 
	 * @return Name of host with this <code>Host Runtime</code>.
	 */
	public String getHostName() {
		return this.hostName;
	}
	
	/**
	 * Get ID of context with reservation of this Host Runtime (task's
	 * exclusivity).
	 * 
	 * @return ID of context, <code>null</code> if any, <code>""</code> if
	 * 	none.
	 */
	public String getReservation() {
		return this.reservation;
	}
	
	/**
	 * Creates and returns a copy of this object.
	 * Does not copy <code>currentTime</code> but uses real current time to
	 * inicialize it as used when constructing new object (i.e. clone is not
	 * fully identical with this object!).
	 */
	@Override
	public HostRuntimeEntry clone() throws CloneNotSupportedException {
		HostRuntimeEntry hostRuntimeEntry = (HostRuntimeEntry) super.clone();
		
		/* Set current time. */
		hostRuntimeEntry.currentTimeMillis = System.currentTimeMillis();
		
		return hostRuntimeEntry;
	}
	
	/* Beginning of a dirty hack. */
	/**
	 * Load units getter.
	 * 
	 * @return The current number of load units.
	 */
	public int getLoadUnits() {
		return loadUnits;
	}
	
	/**
	 * Decreases the load units counter.
	 * TODO: This method should be neither public, nor implemented in this class. However, this
	 * design flaw can only be removed by merging the Host Manager with the Task Manager.
	 * 
	 * @param loadUnits Number of load units to subtract.
	 * @throws IllegalStateException When the number of units would fall below zero.
	 */
	public synchronized void removeLoad(int loadUnits) {
		this.loadUnits -= loadUnits;
		if (this.loadUnits < 0) {
			this.loadUnits += loadUnits;
			throw new IllegalStateException("Attempting to decrease host load below zero.");
		}
	}
	
	/**
	 * Increases the load units counter.
	 * TODO: This method should be neither public, nor implemented in this class. However, this
	 * design flaw can only be removed by merging the Host Manager with the Task Manager.
	 * 
	 * @param loadUnits Number of load units to add.
	 * @param limit Maximum number of load units this host runtime can handle.
	 * @return {@code true} when successful, {@code false} if the limit would be exceeded.
	 */
	public synchronized boolean acceptLoad(int loadUnits, int limit) {
		this.loadUnits += loadUnits;
		if (this.loadUnits <= limit) {
			return true;
		} else {
			this.loadUnits -= loadUnits;
			return false;
		}
	}
	
	/**
	 * Increases the load units counter in a Quick & Dirty (Microsoft-like) manner.
	 * Useful when the Host Manager is not running and no memory limits are known.
	 * Always use {@code acceptLoad()} instead, unless you know what you're doing.
	 * 
	 * @param loadUnits Number of load units to add.
	 */
	public synchronized void addLoad(int loadUnits) {
		this.loadUnits += loadUnits;
	}
	/* End of a dirty hack. */
}
