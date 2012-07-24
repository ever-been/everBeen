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
 * Package query callback implementation, used to query pakages based
 * on their metadata. 
 * 
 * @author David Majda
 */
public class PackageListQueryCallback implements
	PackageQueryCallbackInterface, Serializable {

	private static final long	serialVersionUID	= -2074876501599700678L;

	/** Atomical queries. */
	private Condition[] atoms;
	
	/**
	 * Indicates if the atomical queries are prepared.
	 * For explanation see long comment at <code>Condition</code>
	 * class declaration.
	 */
	private boolean atomsPrepared = false;
	
	/**
	 * Allocates a new <code>PackageListQueryCallback</code> object.
	 * 
	 * @param atoms atomcial queries
	 */
	public PackageListQueryCallback(Condition[] atoms) {
		super();
		this.atoms = atoms;
	}

	/**
	 * @see cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface#match(cz.cuni.mff.been.softwarerepository.PackageMetadata)
	 */
	public boolean match(PackageMetadata metadata) {
		/* If the atoms need to prepare, do it. */ 
		if (!atomsPrepared) {
			for (int i = 0; i < atoms.length; i++) {
				atoms[i].prepare();
			}
		}
        
		/* We apply all atoms in "and" relation (they must all hold true).
		 * Boolean short-circuit evaluation is used for better performance.
		 */
		for (int i = 0; i < atoms.length; i++) {
			if (!atoms[i].execute(metadata)) {
				return false;
			}
		}
		return true;
	}
}
