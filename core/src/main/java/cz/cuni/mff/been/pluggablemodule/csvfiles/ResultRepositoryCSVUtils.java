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
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * Interface for utility object that converts data from RR format to CSV and back.
 * 
 * @author Jan Tattermusch
 *
 */
public interface ResultRepositoryCSVUtils {

    /**
     * Writes a CSV file based on collection of RR records.
     * @param destFile  destination file
     * @param outputColumns  list of output columns
     * @param data  data to write to the file
     * @param downloadFiles if true, files referenced by FILE data handles will be downloaded
     * @throws IOException
     */
	void writeCSVFile(File destFile, String[] outputColumns,
			Collection<DataHandleTuple> data, boolean downloadFiles)
			throws IOException;

	/**
	 * Reads CSV file and creates RR record collection out of it.
	 * @param file file to read
	 * @param inputColumns   input columns 
	 * @param destDatasetDesc  dataset descriptor of destination collection
	 * @param uploadFiles  if true, files referenced by FILE data handles will be uploaded to RR.
	 * @return RR record collection
	 * @throws IOException
	 */
	List<DataHandleTuple> readCSVFile(File file,
			String[] inputColumns, DatasetDescriptor destDatasetDesc,
			boolean uploadFiles) throws IOException;
	
	/**
	 * Gets location of a file that was downloaded by this object.
	 * @param fileId UUID of file.
	 * @return file location.
	 */
	File getFileLocation(UUID fileId);

}