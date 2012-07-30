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

package cz.cuni.mff.been.hostmanager.database;

/**
 * This interface should be used in classes which are serialised to slower media (e.g. disk) and need 
 * a method to tell whether it is needed to save the data or the current save operation can be skipped.
 * Note that interface has no way of keeping track of modifications since last save automatically. 
 * Therefore programmer of class implementing this interface has to keep and set flags according to 
 * the modifying operations and save operations.
 *
 * @author Branislav Repcek
 */
interface ModifiableInterface {

	/**
	 * Test if instance has been modified. It is programmers responsibility to keep track of modifications
	 * and set/reset flags accordingly.
	 * 
	 * @return <code>true</code> if some modifications has been done since last save, <code>false</code>
	 *         otherwise.
	 */
	boolean isModified();
	
	/**
	 * Test if instance of the class has been modified.
	 * 
	 * @param reset If set to <code>true</code> modification flag will be set to false after calling 
	 *        this method. If set to <code>false</code> modification flag is left intact.
	 *
	 * @return <code>true</code> if instance of the class has been modified since last flag reset.
	 *         <code>false</code> if data has not been modified.
	 */
	boolean isModified(boolean reset);
}
