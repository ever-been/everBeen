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
package cz.cuni.mff.been.common.ustream;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The internal interface used for RMI callbacks in RemoteOutputStream.
 * 
 * @author Andrej Podzimek
 */
interface InternalOutputStream extends Remote {
	
	/**
	 * The standard write() method.
	 * 
	 * @param data The byte array to write.
	 * @throws IOException When it rains.
	 */
	void write( byte[] data ) throws IOException, RemoteException;
	
	/**
	 * The extended write() method.
	 * 
	 * @param data The byte array to write.
	 * @param offset Position of the byte array to start from.
	 * @param length Number of bytes to write.
	 * @throws IOException When it rains.
	 */
	void write( byte[] data, int offset, int length ) throws IOException, RemoteException;
}
