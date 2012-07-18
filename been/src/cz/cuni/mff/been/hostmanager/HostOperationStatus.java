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

/**
 * This class stores data about one operation with hosts in Host Manager. Is is used for all asynchronous
 * operation that may take longer to complete (eg. add host, refresh host).
 * Status also includes message which can be used to detail operation progress.
 *
 * @author Branislav Repcek
 */
public class HostOperationStatus implements Serializable {

	private static final long	serialVersionUID	= 5119275049870637358L;

	/**
	 * Status of the host operation.
	 *
	 * Note: Do not change order of declaration in this enum.
	 * 
	 * @author Branislav Repcek
	 */
	public enum Status {
		/**
		 * Unknown operation status
		 */
		UNKNOWN("unknown"),
		
		/**
		 * Operation is still pending.
		 */
		PENDING("pending"),
		
		/**
		 * Operation finished successfully.
		 */
		SUCCESS("success"),
		
		/**
		 * Operation has failed.
		 */
		FAILED("failed");
		
		/**
		 * Status.
		 */
		private String status;

		/**
		 * Construct new <code>Status</code> from <code>String</code>.
		 * 
		 * @param status String containing status.
		 */
		private Status(String status) {
			
			this.status = status;
		}
		
		/*
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			
			return status;
		}
	}
	
	/**
	 * Status code.
	 */
	private Status status;

	/**
	 * Message set by the performer of the operation.
	 */
	private String message;
	
	/**
	 * Name of host on which operation has been performed.
	 */
	private String hostName;
	
	/**
	 * Create operation status with specified values.
	 * 
	 * @param status Status of the operation.
	 * @param message Message.
	 * @param host Host name.
	 */
	public HostOperationStatus(Status status, String message, String host) {
		
		this.status = status;
		this.message = message;
		hostName = host;
	}

	/**
	 * Get name of the host for which this operation has been performed.
	 * 
	 * @return Name of host for which operation has been performed.
	 */
	public String getHostName() {

		return hostName;
	}

	/**
	 * Get message attached to the operation by its performer. This message should contain more info
	 * in case of failure. For successful or pending operations it can be empty.
	 * 
	 * @return Get message attached to the operation.
	 */
	public String getMessage() {

		return message;
	}

	/**
	 * Get status of the operation.
	 * 
	 * @return Status of the operation.
	 */
	public Status getStatus() {

		return status;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return status.toString() + ", message: " + message;
	}
}
