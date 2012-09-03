/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Antonin Tomecek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.taskmanager.data;

/**
 * Class representing one entry (hostRuntime) in
 * {@link cz.cuni.mff.been.taskmanager.TaskManagerImplementation
 * <code>Task Manager</code>}.
 * 
 * @author Antonin Tomecek
 */
public class HostRuntimeEntryImplementation implements HostRuntimeEntry {

	private static final long serialVersionUID = 27113070360465936L;

	/*
	 * Current time on system creating this entry in milliseconds. Difference
	 * between current time and midnight, January 1, 1970 UTC.
	 */
	private long currentTimeMillis;

	/* Name of host with this Host Runtime. */
	private String hostName = "";

	/*
	 * ID of context with reservation of this Host Runtime (task's exclusivity)
	 * null ... for any "" ... for none (Host Runtime is running exclusive task)
	 */
	private String reservation = null;

	/** Current load of the host runtime. */
	private int loadUnits = 0;

	/**
	 * Creates a new (empty - default inicialized) <code>HostRuntimeEntry</code>
	 * . This constructor is neded by <code>Serializable</code>.
	 */
	public HostRuntimeEntryImplementation() {
		this.currentTimeMillis = System.currentTimeMillis();

		// this.hostName = "";
	}

	/**
	 * Creates a new (and inicialized) <code>HostRuntimeEntry</code>.
	 * 
	 * @param hostName
	 *            Name of host with this <code>Host Runtime</code>.
	 */
	public HostRuntimeEntryImplementation(String hostName) {
		this.currentTimeMillis = System.currentTimeMillis();

		/* Inicialization of variables with public write access. */
		this.setHostName(hostName);
	}

	@Override
	public void setHostName(String hostName) {
		if (REGEXP_HOST_NAME.matcher(hostName).matches()) {
			this.hostName = hostName;
		} else {
			throw new IllegalArgumentException("hostName is not valid");
		}
	}

	@Override
	public void setReservation(String reservation) {
		if ((reservation == null)
				|| (reservation.equals(""))
				|| (ContextEntry.REGEXP_CONTEXT_ID.matcher(reservation)
						.matches())) {
			this.reservation = reservation;
		}
	}

	@Override
	public long getCurentTime() {
		return this.currentTimeMillis;
	}

	@Override
	public String getHostName() {
		return this.hostName;
	}

	@Override
	public String getReservation() {
		return this.reservation;
	}

	@Override
	public HostRuntimeEntry clone() throws CloneNotSupportedException {
		HostRuntimeEntryImplementation hostRuntimeEntry = (HostRuntimeEntryImplementation) super
				.clone();

		/* Set current time. */
		hostRuntimeEntry.currentTimeMillis = System.currentTimeMillis();

		return hostRuntimeEntry;
	}

	@Override
	public int getLoadUnits() {
		return loadUnits;
	}

	@Override
	public synchronized void removeLoad(int loadUnits) {
		this.loadUnits -= loadUnits;
		if (this.loadUnits < 0) {
			this.loadUnits += loadUnits;
			throw new IllegalStateException(
					"Attempting to decrease host load below zero.");
		}
	}

	@Override
	public synchronized boolean acceptLoad(int loadUnits, int limit) {
		this.loadUnits += loadUnits;
		if (this.loadUnits <= limit) {
			return true;
		} else {
			this.loadUnits -= loadUnits;
			return false;
		}
	}

	@Override
	public synchronized void addLoad(int loadUnits) {
		this.loadUnits += loadUnits;
	}
	/* End of a dirty hack. */
}
