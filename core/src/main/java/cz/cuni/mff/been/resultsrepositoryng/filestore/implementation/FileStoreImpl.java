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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;

/**
 * Implementation of RR File Store
 * 
 * @author Jan Tattermusch
 *
 */
public class FileStoreImpl extends UnicastRemoteObject implements FileStore {
	
    /**
     * Size of buffer for stream operations
     * 
     */
	private static final int BUFFER_SIZE = 8192;
	
	/**
	 * file store home directory
	 */
	private String homeDir;
	
	
	/**
	 * Creates a new instance of file store
	 * @param homeDir home directory of file store
	 * @throws RemoteException
	 */
	public FileStoreImpl(String homeDir) throws RemoteException {
		super();
	
		this.homeDir = homeDir;
	}

	private static final long serialVersionUID = 3080824802796494733L;	
	
	

	@Override
	public void dismiss(UUID fileId) throws IOException, RemoteException {
		File file = getLocalFile(fileId);
		
		if (!file.exists()) {
			throw new IOException("File with ID " + fileId + "does not exist.");
		}
		
		if (!file.delete()) {
			throw new IOException("Failed to delete file " + fileId + " from file store.");
		}
	}

	@Override
	public boolean exists(UUID fileId) throws IOException, RemoteException {
		File file = getLocalFile(fileId);
		
		return file.exists();
	}

	@Override
	public RemoteInputStream get(UUID fileId) throws IOException,
			RemoteException {
		File file = getLocalFile(fileId);
		
		if (!file.exists()) {
			throw new IOException("File with ID " + fileId + "does not exist.");
		}
		
		InputStream is = new FileInputStream(file);
		RemoteInputStream result = new SimpleRemoteInputStream(is); 
		return result;
	}

	@Override
	public UUID upload(RemoteInputStream remoteFileData) throws IOException,
			RemoteException {
		try {
			UUID id = UUID.randomUUID();
		
			InputStream is = null;
			FileOutputStream os = null;
			try {
				is = RemoteInputStreamClient.wrap(remoteFileData);

				File localFile = getLocalFile(id);
				
				if (!localFile.getParentFile().mkdirs()) {
					throw new IOException(
					"Error creating directory hierarchy for storing file.");
				}

				os = new FileOutputStream(localFile);

				byte[] buffer = new byte[BUFFER_SIZE];

				while (true) {
					int bytesRead = is.read(buffer);
					if (bytesRead == -1)
						break;
					os.write(buffer, 0, bytesRead);
				}
			} finally {
				if (os != null) os.close();
				if (is != null) is.close();
			}
			return id;
		} catch(IOException e) {
			throw new IOException("Error uploading file", e);
		}
	}
	
	/**
	 * Gets reference to a file identified by an ID
	 * @param fileId file ID
	 * @return file reference
	 */
	private File getLocalFile(UUID fileId) {
		
		/* get most significant 24 bits */
		long bits = fileId.getMostSignificantBits() >>> 40;
		
		byte b0 = (byte) (bits >>> 16);
		byte b1 = (byte) ((bits >>> 8) & 0xff);
		byte b2 = (byte) (bits & 0xff);
		
		String d0 = byteToHex(b0);
		String d1 = byteToHex(b1);
		String d2 = byteToHex(b2);
	
		String path = homeDir + File.separator +
						d0 + File.separator +
						d1 + File.separator +
						d2 + File.separator +
						fileId.toString();
		
		File file = new File(path);
		
		return file;
	}
	
	/**
	 * converts hex digit into character representation
	 * @param b hex digit
	 * @return hex notation character
	 */
	private String byteToHex(byte b) {
		String[] hexChars = {

			"0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b",
			"c", "d", "e", "f"
			};
		
		return hexChars[(b >>> 4) & 0xf] + hexChars[b & 0xf];
	}
	
}
