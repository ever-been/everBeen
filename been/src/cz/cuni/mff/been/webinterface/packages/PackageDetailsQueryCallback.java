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
package cz.cuni.mff.been.webinterface.packages;

import java.io.Serializable;

import cz.cuni.mff.been.softwarerepository.PackageMetadata;
import cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface;

/**
 * Package query callback implementation, used to view data about specified
 * package. 
 * 
 * @author David Majda
 */
public class PackageDetailsQueryCallback implements
	PackageQueryCallbackInterface, Serializable {
    
	private static final long	serialVersionUID	= 5170213183653560935L;

	/** Package filename to match. */
	private String packageFilename;
    
	/** @return Returns the packageFilename. */
	public String getPackageFilename() {
		return packageFilename;
	}
    
	/**
	 * Allocates a new <code>PackageDetailsQueryCallback</code> object.
	 * 
	 * @param packageFilename package filename to match
	 */
	public PackageDetailsQueryCallback(String packageFilename) {
		super();
		this.packageFilename = packageFilename;
	}
    
	/** 
	 * @see cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface#match(cz.cuni.mff.been.softwarerepository.PackageMetadata)
	 */
	public boolean match(PackageMetadata metadata) {
		return metadata.getFilename().equals(packageFilename);
	}
}
