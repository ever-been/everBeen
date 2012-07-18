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

import java.io.Serializable;

import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.ContainerProperty;
import cz.cuni.mff.been.common.rsl.ParseException;
import cz.cuni.mff.been.common.rsl.ParserWrapper;
import cz.cuni.mff.been.common.rsl.RSLSemanticException;

/**
 * Imlementation of the callback interface for querying the Software
 * Repository, whitch uses RSL for specifying the conditions.
 * 
 * @author David Majda
 */
public class RSLPackageQueryCallback implements
		PackageQueryCallbackInterface, Serializable		 {

	private static final long	serialVersionUID	= 7997000600640084282L;
	/** Parsed RSL condition specifying restrictions for the packages. */
	private Condition condition;
		
	/** @return parsed RSL condition specifying restrictions for the packages */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * Decides, whether given package matches the query.
	 * 
	 * @param metadata metadata of queried package
	 * @return <code>true</code> if the package matches the query;
	 *          <code>false</code> otherwise
	 * @throws MatchException if some RSL error in matching occurs
	 *
	 * @see cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface#match(cz.cuni.mff.been.softwarerepository.PackageMetadata)
	 */
	public boolean match(PackageMetadata metadata) throws MatchException {
		ContainerProperty context = new PackageMetadataPropertyRoot(metadata);
		try {
			condition.check(context);
		} catch (RSLSemanticException e) {
			throw new MatchException(e);
		}
		return condition.evaluate(context);
	}

	/**
	 * Allocates a new <code>RSLPackageQueryCallback</code> object.
	 * 
	 * @param condition RSL condition specifying restrictions for the packages
	 * @throws IllegalArgumentException if the RSL is syntactically invalid
	 */
	public RSLPackageQueryCallback(String condition) {
		try {
			this.condition = ParserWrapper.parseString(condition);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Allocates a new <code>RSLPackageQueryCallback</code> object.
	 * 
	 * @param condition RSL condition specifying restrictions for the packages
	 */
	public RSLPackageQueryCallback(Condition condition) {
		this.condition = condition;
		
	}
	
}
