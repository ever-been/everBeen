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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import cz.cuni.mff.been.common.UploadHandle;
import cz.cuni.mff.been.common.UploadStatus;

/**
 * Little utility class for Software Repository tests.
 * 
 * @author David Majda
 */
public class Utils {
	
	private Utils() {
	}
	
	public static class UploadResult {
		private UploadStatus status;
		private String[] errorMessages;

		public UploadStatus getStatus() {
			return status;
		}

		public String[] getErrorMessages() {
			return errorMessages.clone();
		}

		public UploadResult(UploadStatus status, String[] errorMessages) {
			this.status = status;
			this.errorMessages = errorMessages;
		}
	}

	/**
	 * Sets up the Software Repository.
	 * 
	 * @param beenHome BEEN home diroectory 
	 * @return initialized Software Repository implementation
	 * @throws IOException if some error in file manipulation occurs
	 * @throws ClassNotFoundException when reading of the metadata index fails
	 */
	public static SoftwareRepositoryImplementation setUpRepository(String beenHome)
			throws IOException, ClassNotFoundException {
		File dataDir = File.createTempFile("softwarerepository-data", null);
		if (!dataDir.delete()) {
			throw new IOException("Can't delete file \"" + dataDir.getPath() + "\".");
		}
		if (!dataDir.mkdir()) {
			throw new IOException("Can't create directory \"" + dataDir.getPath() + "\".");
		}
		dataDir.deleteOnExit();

		File tempDir = File.createTempFile("softwarerepository-temp", null);
		if (!tempDir.delete()) {
			throw new IOException("Can't delete file \"" + tempDir.getPath() + "\".");
		}
		if (!tempDir.mkdir()) {
			throw new IOException("Can't create directory \"" + tempDir.getPath() + "\".");
		}
		tempDir.deleteOnExit();

		SoftwareRepositoryImplementation result = SoftwareRepositoryImplementation
				.getInstance();
		result.initialize(dataDir.getAbsolutePath(), tempDir.getAbsolutePath());
		return result;
	}

	/**
	 * Uploads a testing package to the Software Repository. 
	 * 
	 * @param softwareRepository Software Repository implementation
	 * @param packageName package name
	 * @param beenHome BEEN home directory
	 * @return upload result
	 * @throws IOException if I/O fails somewhere
	 */
	public static UploadResult uploadPackage(
			SoftwareRepositoryImplementation softwareRepository, String packageName,
			String beenHome) throws IOException {
		String fileName = beenHome
			+ File.separator + "resources"
			+ File.separator + "tests"
			+ File.separator + "softwarerepository"
			+ File.separator + packageName;		
		UploadStatus status = UploadStatus.ACCEPTED;
		String[] errorMessages = new String[0];
    
		ServerSocket serverSocket = new ServerSocket(0); // 0 = use any port
		UploadHandle handle = softwareRepository.beginPackageUpload(
				InetAddress.getLocalHost(), 
				serverSocket.getLocalPort());
		byte[] buffer = new byte[SoftwareRepositoryInterface.UPLOAD_BUFFER_SIZE];
		int bytesRead;
		Socket socket = serverSocket.accept();
		try {
			OutputStream outputStream = new BufferedOutputStream(
				socket.getOutputStream(),
				SoftwareRepositoryInterface.UPLOAD_BUFFER_SIZE
			);
			InputStream inputStream = new BufferedInputStream(
				new FileInputStream(fileName),
				SoftwareRepositoryInterface.UPLOAD_BUFFER_SIZE
			);
			try {
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			} finally {
				outputStream.close();
				inputStream.close();
			}
		} finally {
			socket.close();
			serverSocket.close();
	
			/*
			 * Wait until the Software Repository finishes its processing and sets
			 * some meaningful state. Finish the upload then.
			 */
			do {
				status = softwareRepository.getPackageUploadStatus(handle);
			} while (status == UploadStatus.UPLOADING || status == UploadStatus.INITIALIZING);
			if (status == UploadStatus.REJECTED) {
				errorMessages = softwareRepository.getUploadErrorMessages(handle);
			}
			softwareRepository.endPackageUpload(handle);
		}
		return new UploadResult(status, errorMessages);
	}

}
