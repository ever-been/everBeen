/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.softwarerepository;

/**
 * General exception for use in pacakge upload.
 * 
 * @author David Majda
 */
public class PackageUploadException extends SoftwareRepositoryException {

	private static final long	serialVersionUID	= -2313191741822239957L;

	/** Detailed error messages describing the error in the package upload. */
	private String[] errorMessages;
	
	/** @return detailed error messages */
	public String[] getErrorMessages() {
		return errorMessages.clone();
	}
	
	/**
	 * Allocates a new <code>PackageUploadException</code> object
	 * with specified message and detailed error messages.
	 * 
	 * @param message exception message
	 * @param errorMessages detailed error messages
	 */
	public PackageUploadException(String message, String[] errorMessages) {
		super(message);
		this.errorMessages = errorMessages;
	}
}
