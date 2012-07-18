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

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

import cz.cuni.mff.been.common.DownloadHandle;
import cz.cuni.mff.been.common.DownloadStatus;
import cz.cuni.mff.been.common.UploadHandle;
import cz.cuni.mff.been.common.UploadStatus;

/**
 * RMI interface to the Software Repository.
 * 
 * @author David Majda
 */
public interface SoftwareRepositoryInterface extends Remote {
	/** Package MIME type. */
	String PACKAGE_MIME_TYPE  = "application/vnd.mff.cuni.cz.been-package";
	
	/** Size of buffer used when uploading packages from clients. */
	int UPLOAD_BUFFER_SIZE = 4096; 
	/** Size of buffer used when downloading packages to clients. */
	int DOWNLOAD_BUFFER_SIZE = 4096; 

	/**
	 * Downloads a package form the Software Repository. The client must open
	 * the socket on some port before calling this function. Then it calls the
	 * <code>downloadPackage</code> function.
	 * 
	 * This function work asynchronously and returns immediately.
	 * Before return it starts a new thread that will send the package contents
	 * to the socket open on given host and port.
	 * 
	 * If there is any error when sending the package contents, the socket
	 * is closed. It's client's responsibility to check (via the
	 * <code>getPackageDownloadStatus</code> method whether the package
	 * arrived complete and undamaged.
	 * 
	 * After the download finishes, client should close the returned handle
	 * via the <code>endPackageDownload</code> method.  
	 * 
	 * @param filename filename of the package to download
	 * @param ip IP address of the machine waiting for receiving the packcage
	 *            (not necessairly the caller itself)  
	 * @param port port open for receiving the package contents
	 * @return package download handle
	 * @throws RemoteException when something in RMI goes bad
	 */
	DownloadHandle beginPackageDownload( String filename, InetAddress ip, int port )
	throws RemoteException;
	
	/**
	 * Downloads a package from the Software Repository.
	 * 
	 * This function works asynchronously and returns immediately. Before returning, it starts
	 * a new thread to transmit the package contents through the stream open on the caller.
	 * 
	 * @param fileName File name of the package to download.
	 * @param stream An output stream to accept the contents of the package.
	 * @return A download handle bound to the upload operation.
	 * @throws RemoteException When it rains.
	 */
	DownloadHandle beginPackageDownload( String fileName, OutputStreamInterface stream )
	throws RemoteException;
	
	/**
	 * Returns the status of the package download. Result is one
	 * of the values listed in the <code>DownlaodStatus</code> enumeration.
	 * 
	 * Values <code>INITIALIZING</code> and <code>DOWNLOADING</code> mean that
	 * Software Repository still works on the package download. Values
	 * <code>SUCCEEDED</code> and <code>ERROR</code> mean that Software Repository
	 * has finished its work (either successfully or unsuccessfully) and the download
	 * handle is ready to be closed by the <code>endPackageDownload</code> method.   
	 * 
	 * @param handle package download handle
	 * @return download status
	 * @throws RemoteException when something in RMI goes bad
	 */
	DownloadStatus getPackageDownloadStatus( DownloadHandle handle ) throws RemoteException;

	/**
	 * Closes the download handle. Client should call this method after the downlaod
	 * status returned by <code>getPackageDownloadStatus</code> has value
	 * <code>SUCCEEDED</code> or <code>ERROR</code>.
	 * 
	 * If the client doesn't close the download handle, Software Repository will leak
	 * a little amount of memory. However, no other resources than memory
	 * (such as open files, locks...) are associated with the download handle. 
	 * 
	 * @param handle download handle
	 * @throws RemoteException when something in RMI goes bad
	 */
	void endPackageDownload( DownloadHandle handle ) throws RemoteException;
    
	/**
	 * Uploads a package to the Software Repository. The client must open
	 * the socket on some port before calling this function. Then it calls the
	 * <code>uploadPackage</code> function.
	 * 
	 * This function work asynchronously and returns immediately.
	 * Before return it starts a new thread that will receive the package contents
	 * from the socket open on given host and port.
	 * 
	 * If there is any error when sending the package contents, the socket
	 * is closed. 
	 * 
	 * Calling this function doesn't guarantee that this package will be added
	 * to the Software Repository - if it arrives damaged, it is invalid,
	 * or some error during processing occurs, Software Repository rejects
	 * the package. Client has an opportunity to check (via the
	 * <code>getPackageUploadStatus</code> method whether the package
	 * arrived complete and undamaged.
	 *  
	 * After the upload finishes, client should close the returned handle
	 * via the <code>endPackageUpload</code> method.  
	 *  
	 * @param ip IP address of the machine prepared for sending the packcage
	 *            (not necessairly the caller itself)
	 * @param port port open for sending the package contents
	 * @throws RemoteException when something in RMI goes bad
	 */
	UploadHandle beginPackageUpload( InetAddress ip, int port ) throws RemoteException;
	
	/**
	 * Uploads a package to the Software Repository.
	 * 
	 * This function works asynchronously and returns immediately. Before returning, it starts
	 * a new thread to receive the package contents from the stream open on the caller.
	 * 
	 * @param stream An input stream with the package contents.
	 * @return An upload handle bound to the upload operation.
	 * @throws RemoteException When it rains.
	 */
	UploadHandle beginPackageUpload( InputStreamInterface stream ) throws RemoteException;
	
	/**
	 * Returns the status of the package upload. Result is one
	 * of the values listed in the <code>UploadStatus</code> enumeration.
	 * 
	 * Values <code>INITIALIZING</code> and <code>UPLOADING</code> mean that
	 * Software Repository still works on the package upload. Values
	 * <code>ACCEPTED</code>, <code>REJECTED</code> and <code>ERROR</code>
	 * mean that Software Repository has finished its work (either successfully
	 * or unsuccessfully) and the download handle is ready to be closed by the
	 * <code>endPackageUpload</code> method.   
	 * 
	 * @param handle package upload handle
	 * @return upload status
	 * @throws RemoteException when something in RMI goes bad
	 */
	UploadStatus getPackageUploadStatus( UploadHandle handle ) throws RemoteException;

	/**
	 * Returns the error messages associated with rejected package upload. These
	 * messages contain human-readable messages describing the reason of the
	 * rejection and are suitable for displaying to the user.
	 * 
	 * @param handle package upload handle
	 * @return error messages associated with rejected package upload
	 * @throws IllegalStateException if the package upload status is different
	 *          from <code>REJECTED</code>
	 * @throws RemoteException when something in RMI goes bad
	 */
	String[] getUploadErrorMessages( UploadHandle handle ) throws RemoteException;

	/**
	 * Closes the upload handle. Client should call this method after the upload
	 * status returned by <code>getPackageUploadStatus</code> has value
	 * <code>ACCEPTED</code>, <code>REJECTED</code> or <code>ERROR</code>.
	 * 
	 * If the client doesn't close the upload handle, Software Repository will leak
	 * a little amount of memory. However, no other resources than memory
	 * (such as open files, locks...) are associated with the upload handle. 
	 * 
	 * @param handle upload handle
	 * @throws RemoteException when something in RMI goes bad
	 */
	void endPackageUpload( UploadHandle handle ) throws RemoteException;

	/**
	 * Deletes the package form the Software Repository.
	 * 
	 * @param filename filename of the deleted package
	 * @return <code>true</code> if the deletion was successfull;
	 *          <code>false</code> otherwise
	 * @throws RemoteException when something in RMI goes bad
	 */
	boolean deletePackage( String filename ) throws RemoteException;
	
	/**
	 * Queries the Software Repository by the package metadata.
	 * 
	 * Queries are made in a very general manner - caller will pass an instance of
	 * a class implementing <code>PackageQueryCallbackInterface</code> interface. This interface
	 * has a method <code>match</code>, that decides wheteher given package stored
	 * in the repository matches the caller's query conditions.
	 * 
	 * The <code>queryPackages</code> method returns metadata of those packages,
	 * for which the <code>match</code> method returned true. 
	 *  
	 * @param callback query callback interface
	 * @return metadata of packages matching the query
	 * @throws RemoteException when something in RMI goes bad
	 * @throws MatchException if some error occurs when matching packages in the
	 *                         PackageQueryCallbackInterface
	 */
	PackageMetadata[] queryPackages( PackageQueryCallbackInterface callback )
	throws RemoteException, MatchException;
}
