/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.idng;

import java.rmi.Remote;

/**
 * This interface provides remote access to the identifier generator.
 * TODO This interface should be moved outside the results repository. The identifier hierarchy
 * should be redesigned.
 * 
 * @author Andrej Podzimek
 */
interface IDGeneratorInterface extends Remote {
	
	/**
	 * This method generates a new unique identifier of the requested type.
	 * 
	 * @param <T> Type of identifier to use. This type must extend the standard UID.
	 * 
	 * @param clazz The class (meta)object of the requested type.
	 * @return A new instance of the identifier with a unique number inside.
	 * @throws IDInstantiationException When something weird happens.
	 */
	public < T extends UID > T getUniqueIdentifier( Class< T > clazz )
	throws IDInstantiationException; 
}
