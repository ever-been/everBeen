/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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
package cz.cuni.mff.been.task;


/**
 * A task representing a job. This kind of task does some work, and then ends.
 * 
 * @author Jaroslav Urban
 */
public abstract class Job extends Task {
	
	/**
	 * Allocates a new <code>Job</code> object.
	 *
	 * @throws TaskInitializationException
	 */
	protected Job() throws TaskInitializationException {
		super();
	}

	/**
	 * Does the job's work, the task ends when this method returns.
	 * 
	 * @throws TaskException if the Job failed to do its work.
	 */
	protected abstract void run() throws TaskException;

	/**
	 * Checks required job properties, starts job afterwards.
	 * When the work is done exits the JVM
	 * 
	 * NOTE Urban: it exits the JVM only while we use the 1 JVM per 1 task model 
	 * @throws TaskException if the Job failed to do its work.
	 */
	public void runJob() throws TaskException {
		checkRequiredProperties();
		run();
	}
	
	/**
	 * Checks if all properties from <code>requiredProperties</code>
	 * are set (not <code>null</code>) 
	 * @param requiredProperties list of properties to check
	 * @throws TaskException when any of required properties is not set
	 */
	protected void checkRequiredProperties(String[] requiredProperties) throws TaskException {
		String errorMessage = "Required properties not set: ";
		boolean doThrow = false;
		for (String property : requiredProperties) {
			
			if (getTaskProperty(property) == null) {
				doThrow = true;
				errorMessage += "\n" + property;
			}
		}
		
		if (doThrow) {
			throw new TaskException(errorMessage);
		}
	}
	
	/**
	 * Check if required property is present.
	 * 
	 * @param property name of the property to test.
	 * 
	 * @throws TaskException if the property is not present (set to null).
	 */
	protected void checkRequiredProperty(String property) throws TaskException {
		if (getTaskProperty(property) == null) {
			throw new TaskException("Required property not set: " + property);
		}
	}
	
	/**
	 * Checks if all required properties are present. This method is called automatically before
	 * the Job is started and has to be implemented by the Job.
	 * 
	 * @throws TaskException if some of the required properties are not set. All properties are 
	 *         reported in one go in the message.
	 */
	protected abstract void checkRequiredProperties() throws TaskException;
}
