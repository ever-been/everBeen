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
package cz.cuni.mff.been.resultsrepositoryng.filestore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;

/**
 * Interface of client object that can access RR's file store.
 * @author Jan Tattermusch
 *
 */
public interface FileStoreClient extends Serializable {
		
    
        /**
         * Uploads a local file to RR.
         * @param localFile file to upload
         * @return  ID assigned to the new file by RR
         * @throws IOException
         */
		UUID uploadFile(File localFile) throws IOException;
		
		/**
		 * Uploads file read from an input stream to RR.
		 * @param localStream stream to upload
		 * @return    ID assigned to the new file by RR 
		 * @throws IOException
		 */
		UUID uploadFile(InputStream localStream) throws IOException;
		
		/**
		 * Downloads a file from RR and stores it to a local file
		 * @param fileId ID of file to download
		 * @param localFile destination file
		 * @throws IOException
		 */
		void downloadFile(UUID fileId, File localFile) throws IOException;
		
		/**
		 * Download a file from RR and writes it to an output stream.
		 * @param fileId ID of file to download
		 * @param localStream destination stream
		 * @throws IOException
		 */
		void downloadFile(UUID fileId, OutputStream localStream) throws IOException;
		
		/**
		 * Delete's a file from RR file store.
		 * @param fileId ID of file to dismiss
		 * @throws IOException
		 */
		void dismissFile(UUID fileId) throws IOException;
		
		/**
		 * Tells whether requested file exists.
		 * @param fileId  file ID
		 * @return true if file exists in file store
		 * @throws IOException
		 */
		boolean fileExists(UUID fileId) throws IOException;
}
