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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.UUID;

import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;

import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;

/**
 * 
 * Implementation of file store client.
 * 
 * @author Jan Tattermsuch
 *
 */
public class FileStoreClientImpl implements FileStoreClient {

    /**
     * buffer size for streams.
     */
	private static final int BUFFER_SIZE = 8192;
	
	private static final long serialVersionUID = -2418239439295655925L;
	
	/**
	 * file store to work with 
	 */
	private FileStore fileStore;

	/**
	 * Creates new instance of client using given file store
	 * @param fileStore file store to use
	 */
	public FileStoreClientImpl(FileStore fileStore) {
		this.fileStore = fileStore;
	}
	
	@Override
	public void dismissFile(UUID fileId) throws IOException {
		try {
			fileStore.dismiss(fileId);
		} catch(RemoteException e) {
			throw new IOException("Error deleting file.", e);
		}
	}

	@Override
	public void downloadFile(UUID fileId, File localFile) throws IOException {
		try {	
			InputStream is;
			FileOutputStream os;
			
			is = RemoteInputStreamClient.wrap(fileStore.get(fileId));
			os = new FileOutputStream(localFile);
			copyAndClose( is, os );
		} catch(RemoteException e) {
			throw new IOException("Error downloading file.", e);
		}
	}
	
	@Override
	public void downloadFile(UUID fileId, OutputStream localStream) throws IOException {
		try {	
			InputStream is;
			
			is = RemoteInputStreamClient.wrap(fileStore.get(fileId));
			copyAndClose( is, localStream );
		} catch(RemoteException e) {
			throw new IOException("Error downloading file.", e);
		}
	}

	@Override
	public boolean fileExists(UUID fileId) throws IOException {
		try {
			return fileStore.exists(fileId);
		} catch(RemoteException e) {
			throw new IOException("Error looking up file.", e);
		}
	}

	@Override
	public UUID uploadFile(File localFile) throws IOException {
		try {
			InputStream is = new FileInputStream(localFile);
			return fileStore.upload(new SimpleRemoteInputStream(is));
		} catch(RemoteException e) {
			throw new IOException("Error uploading file.", e);
		}
	}
	
	@Override
	public UUID uploadFile(InputStream localStream) throws IOException {
		try {
			return fileStore.upload(new SimpleRemoteInputStream(localStream));
		} catch(RemoteException e) {
			throw new IOException("Error uploading file.", e);
		}
	}
	
	/**
	 * Copies a file and closes streams. 
	 * @param is input stream
	 * @param os output stream
	 * @throws IOException
	 */
	private void copyAndClose( InputStream is, OutputStream os ) throws IOException {
		byte[] buffer;
		int bytesRead;
		
		buffer = new byte[ BUFFER_SIZE ];
		try {
			while ( -1 != ( bytesRead = is.read( buffer ) ) ) {
				os.write( buffer, 0, bytesRead );
			}
		} finally {
			is.close();
			os.close();
		}
	}
}
