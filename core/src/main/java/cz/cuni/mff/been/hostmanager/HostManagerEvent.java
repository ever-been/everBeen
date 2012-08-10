/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents event that is generated every time some important event occurred in the
 * Host Manager (e.g. new host is added to the database, host is removed...).
 * Note that this class cannot be used for events generated by the Load Server. Those events have
 * special class {@link cz.cuni.mff.been.hostmanager.load.LoadMonitorEvent}.
 *
 * @see HostManagerEventListener
 *
 * @author Branislav Repcek
 */
public class HostManagerEvent implements Serializable {

	private static final long	serialVersionUID	= 360211177261836217L;

	/**
	 * Event type.
	 */
	private EventType type;
	
	/**
	 * Status of the operation.
	 */
	private Status status;
	
	/**
	 * Name of the object this message is about.
	 */
	private String objectName;
	
	/**
	 * Message generated by the HM.
	 */
	private String message;
	
	/**
	 * Time when event has been generated.
	 */
	private Date time;
	
	/**
	 * Create new event. Time is set automatically.
	 * 
	 * @param type Type of the event.
	 * @param status Status of the operation that triggered the event.
	 * @param objectName Name of the object this event is about.
	 * @param message Message from the Host Manager.
	 */
	public HostManagerEvent(EventType type, Status status, String objectName, String message) {
		
		this.type = type;
		this.status = status;
		this.objectName = (objectName == null ? "" : objectName);
		this.message = (message == null ? "" : message);
		this.time = new Date();
	}
	
	/**
	 * Create new event. Message will be set to empty and time will be set to current time.
	 * 
	 * @param type Type of the event.
	 * @param status Status of the operation that triggered the event.
	 * @param objectName Name of the host.
	 */
	public HostManagerEvent(EventType type, Status status, String objectName) {

		this.type = type;
		this.status = status;
		this.objectName = (objectName == null ? "" : objectName);
		this.message = "";
		this.time = new Date();
	}
	
	/**
	 * @return Name of the object this event is about.
	 */
	public String getObjectName() {
		
		return objectName;
	}
	
	/**
	 * @return Message from the Host Manager (may be empty but is never <tt>null</tt>).
	 */
	public String getMessage() {
		
		return message;
	}
	
	/**
	 * @return Status of the operation that caused event.
	 */
	public Status getStatus() {
		
		return status;
	}
	
	/**
	 * @return Time of the event creation.
	 */
	public Date getTime() {
		
		return time;
	}
	
	/**
	 * @return Type of the event.
	 */
	public EventType getType() {
		
		return type;
	}
	
	/**
	 * Type of the event.
	 *
	 * @author Branislav Repcek
	 */
	public enum EventType {

		/**
		 * Host has just connected to the environment.
		 */
		HOST_CONNECTED,
		
		/**
		 * Has has just disconnected.
		 */
		HOST_DISCONNECTED,
		
		/**
		 * New host added to the database.
		 */
		HOST_ADD,
		
		/**
		 * Host data have been refreshed.
		 */
		HOST_REFRESH,
		
		/**
		 * Host has been removed from db.
		 */
		HOST_REMOVE,
		
		/**
		 * New group has been created.
		 */
		GROUP_CREATE,
		
		/**
		 * Group has been removed.
		 */
		GROUP_REMOVE,
		
		/**
		 * Group has been modified.
		 */
		GROUP_CHANGE,

		/**
		 * Database has been rebuilt (after alias modification).
		 */
		DATABASE_REBUILDING;
	}

	/**
	 * Status of the operation that has generated this event.
	 *
	 * @author Branislav Repcek
	 */
	public enum Status {

		/**
		 * Operation completed successfully.
		 */
		SUCCEEDED,
		
		/**
		 * Operation failed.
		 */
		FAILED
	}

}