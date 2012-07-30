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

/**
 * Callback interface for querying the Software Repository.
 * 
 * @author David Majda
 */
public interface PackageQueryCallbackInterface extends Serializable {
	/**
	 * Decides, whether given package matches the query.
	 * 
	 * @param metadata metadata of queried package
	 * @return <code>true</code> if the package matches the query;
	 *          <code>false</code> otherwise
	 * @throws MatchException if some error in matching occurs
	 */
	boolean match(PackageMetadata metadata) throws MatchException;
}
