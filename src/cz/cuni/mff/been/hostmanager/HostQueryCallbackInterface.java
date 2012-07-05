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

import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;

/**
 * Interface which provides user with means to create callback function for host queries which can't be
 * performed using interface of Host Manager.
 *
 * @author Branislav Repcek
 *
 */
public interface HostQueryCallbackInterface extends Serializable {
	
	/**
	 * Method which decides whether given host meets specified criteria.
	 * 
	 * @param hi <code>HostInfoInterface</code> class containing info about host to test.
	 * 
	 * @return <code>true</code> if host meets given criteria, <code>false</code> otherwise.
	 * 
	 * @throws Exception If an error occurred.
	 */
	boolean match(HostInfoInterface hi) throws Exception;
}
