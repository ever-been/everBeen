/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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
package cz.cuni.mff.been.resultsrepositoryng.filestore.implementation;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import com.healthmarketscience.rmiio.RemoteInputStream;

/**
 * RR File Store interface. Allows to work with remote files 
 * in RR's central storage.
 *  
 * @author Jan Tattermusch
 *
 */
public interface FileStore extends Remote {
	
    /**
     * Uploads a file read from remote stream
     * @param fileData  source of file data
     * @return ID assigned to newly created file
     * @throws IOException
     * @throws RemoteException
     */
	UUID upload(RemoteInputStream fileData) throws IOException, RemoteException;
	
	/**
	 * Returns remote stream for file stored in RR.
	 * @param fileId ID of file to read
	 * @return remote stream with file's data
	 * @throws IOException
	 * @throws RemoteException
	 */
	RemoteInputStream get(UUID fileId) throws IOException, RemoteException;
	
	/**
	 * Checks whether file exists in file store.
	 * @param fileId ID of file to check
	 * @return true if file with given id exists in file store.
	 * @throws IOException
	 * @throws RemoteException
	 */
	boolean exists(UUID fileId) throws IOException, RemoteException; 
	
	/**
	 * Deletes a file from file store
	 * @param fileId ID of file to remove
	 * @throws IOException
	 * @throws RemoteException
	 */
	void dismiss(UUID fileId) throws IOException, RemoteException;
	
}
