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
package cz.cuni.mff.been.pluggablemodule.csvfiles;

import java.io.File;

import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;

/**
 * Pluggable module for working with CSV files.
 *  
 * @author Jan Tattermusch
 *
 */
public interface CSVFilesPluggableModule {

    /**
     * Returns utility object for working with CSV files
     * @param fileStoreClient   RR file store client to be used when downloading/uploading files
     * @param fileUploadDownloadDir Directory to where upload and download files
     * @param separatorChar CSV separator char
     * @param quoteChar CSV quote char
     * @return utility object for working with CSV files
     */
	public ResultRepositoryCSVUtils createResultsRepositoryCSVUtils(FileStoreClient fileStoreClient,
			File fileUploadDownloadDir, char separatorChar, char quoteChar);
	
	
}