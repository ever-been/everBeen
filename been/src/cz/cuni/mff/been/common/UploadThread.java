/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda, Michal Tomcanyi
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
package cz.cuni.mff.been.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

/**
 * General uploading thread able to copy file over network via TCP sockets.
 * Thread creates temporary file and allows descendants to process the temporary file.
 * 
 * @author Michal Tomcanyi
 * @author David Majda
 *
 */
public abstract class UploadThread extends Thread {

	/**
	 * How many times we should try to create a temp file. See
	 * <code>createTempFile</code> method for explanation, why we need this
	 * constant.
	 */
	private static final int MAX_CREATE_TEMP_FILE_TRY_COUNT = 1024;
	
	/** Size of buffer used when uploading packages from clients. */
	private static final int UPLOAD_BUFFER_SIZE = 4096;
	
	/** Upload handle. */ 
	private UploadHandle handle;
	/** IP address of host to download the package from. */ 
	private InetAddress ip;
	/** Port to download the package from. */ 
	private int port;
	/** Status of uploader */
	protected UploadStatus status;
	/** Temporary file where transferred contents are saved */
	private File tempFile;
	/** Error messages */
	protected LinkedList<String> errorMessages = new LinkedList<String>();
	
	/** @return returns the handle */
	public UploadHandle getHandle() {
		return handle;
	}
	
	/** @return returns the IP addresss */
	public InetAddress getIp() {
		return ip;
	}
	
	/** @return returns the port */
	public int getPort() {
		return port;
	}
	
	/**
	 * @return error messages possibly created during upload/file handling
	 * 
	 * @see #addErrorMessage(String)
	 * @see #uploadFailed()
	 */
	public String[] getErrorMesssages() {
		return errorMessages.toArray(new String[errorMessages.size()]);
	}
	
	/**
	 * Allocates a new <code>PackageUploadThread</code> object with specified
	 * parameters.
	 * 
	 * @param handle upload handle
	 * @param ip IP address of host to download the package from
	 * @param port port to download the package from
	 */
	public UploadThread(UploadHandle handle, InetAddress ip, int port) {
		super();
		this.handle = handle;
		this.ip = ip;
		this.port = port;
		setStatus(UploadStatus.INITIALIZING);
	}
	       
	/**
	 * Sets the upload status to given value.
	 * 
	 * @param status upload status
	 */
	protected void setStatus(UploadStatus status) {
		this.status = status;
	}
	
	/**
	 * Adds new error message to the list of error messages
	 * @param error	error message
	 * @see #errorMessages
	 */
	protected void addErrorMessage(String error) {
		errorMessages.add(error);
	}

	/**
	 * @return status of the uploader
	 */
	public UploadStatus getStatus() {
		return status;
	}
	
	/** @return file with uploaded data */
	protected File getTemporaryFile() {
		return tempFile;
	}
	
	/**
	 * @return prefix of created data file, descendants may override
	 * 		   'upload' by default
	 * 
	 * @see File#createTempFile(java.lang.String, java.lang.String, java.io.File)
	 */
	protected String getPrefix() {
		return "upload";
	}
	
	/**
	 * @return suffix of created data file, descendants may override
	 * 			".tmp" by default
	 * @see File#createTempFile(java.lang.String, java.lang.String, java.io.File)
	 */
	protected String getSuffix() {
		return ".tmp";
	}
	
	/**
	 * @return temporary directory where to create data file, descendants may override
	 * 			<code>null</code> by default which means system-dependent temporary directory
	 * 			will be used
	 * @see File#createTempFile(java.lang.String, java.lang.String, java.io.File)
	 */
	protected File getTempDirectory() {
		return null;
	}
	
	/**
	 * Uploads the package from the uploader to the specified file.
	 * 
	 * @param file file to download
	 * @throws IOException if some I/O error occurs
	 */
	private void uploadPackage(File file) throws IOException {
		byte[] buffer = new byte[UPLOAD_BUFFER_SIZE];
		int bytesRead;
		Socket socket = new Socket(ip, port);
		try {
			InputStream inputStream = new BufferedInputStream(
				socket.getInputStream(),
				UPLOAD_BUFFER_SIZE
			);
			OutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(file.getPath()),
				UPLOAD_BUFFER_SIZE
			);
			try {
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			} finally {
				inputStream.close();
				outputStream.close();
			}
		} finally {
			socket.close();
		}                 
	}
	
	/**
	 * Deletes specified file if it exists.
	 * 
	 * @param file file to delete
	 */
	private void deleteFileIfExists(File file) {
		if (file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * Creates teporary file in the <code>tmpDir</code> directory.
	 * 
	 * @return the temporary file
	 * 
	 * @throws IOException if the temporary file can not be created
	 */
	private File createTempFile() throws IOException {
		/*
		 * There is a bug in the Sun's JVM which causes the File.createTempFile
		 * call throw IOException with message "Access is denied" occasionally on
		 * Windows. We work around the bug by simply retrying the call multiple
		 * times. After a while we give up, to avoid hanging-up. 
		 * 
		 * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6325169 for more
		 * information about the bug.
		 */
		File result = null;
		int tryCount = 0;
		boolean failed;
		IOException thrownException = null;
		
		do {
			failed = false;
			try {
				result = File.createTempFile(getPrefix(), getSuffix(), getTempDirectory());
			} catch (IOException e) {
				failed = true;
				thrownException = e;
			}
			tryCount++;
		} while (failed && tryCount < MAX_CREATE_TEMP_FILE_TRY_COUNT);
		
		if (result != null) {
			return result;
		} else {
			throw thrownException == null ? new IOException("Illegal state") : thrownException;		// Ugly hack to suppress warnings.
		}
	}
	
	/**
	 * Called after successfull completition of file transfer, so that descendant can
	 * process the file. 
	 * <p>
	 * <b>WARNING:</b> Note that file denoted by {@link #getTemporaryFile()} method will
	 * be deleted after this method is finished, so descendants should copy/move the file
	 * to a different location
	 * </p>
	 * 
	 * @see #getTemporaryFile()
	 */
	protected abstract void processFile();

	/**
	 * Called when an error occurs during file upload so that descandat can handle the error
	 * Status of the uploader will be set to {@link UploadStatus#ERROR} and {@link #errorMessages}
	 * will contain error message that caused the failure.
	 * 
	 * Temporary file with data may exist but its content is undefined.
	 * 
	 * @see #getStatus()
	 * @see #errorMessages
	 */
	protected abstract void uploadFailed();
	
	/**
	 * Connects to given host and port, downloads the package file form there,
	 * validates its contents and adds the package into the Software Repository.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		setStatus(UploadStatus.UPLOADING);
		try {
			/* Create the temp file. */
			tempFile = createTempFile();
			try {
				/* Download the file form specified host and port to the temp file. */
				uploadPackage(tempFile);
				processFile();
				/* After all operations finish (either succesfully or due to some error),
				 * delete the temporary file if it still exists. 
				 */
			} catch (Exception e) {
				e.printStackTrace();
				setStatus(UploadStatus.ERROR);
			} finally {
				deleteFileIfExists(tempFile);
			}
		} catch (IOException e) {
			setStatus(UploadStatus.ERROR);
			addErrorMessage("Upload failed: " + e.getMessage());
			// call user-method to handle error status
			uploadFailed();
		} 
	}


}
