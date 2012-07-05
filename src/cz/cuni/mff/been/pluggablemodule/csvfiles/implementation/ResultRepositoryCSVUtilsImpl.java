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

package cz.cuni.mff.been.pluggablemodule.csvfiles.implementation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import cz.cuni.mff.been.pluggablemodule.csvfiles.ResultRepositoryCSVUtils;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle.DataType;
import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;

/**
 * Results Repository CSV utility
 * @author Jan Tattermusch
 *
 */
public class ResultRepositoryCSVUtilsImpl implements ResultRepositoryCSVUtils {
	
	/**
	 * file store client to use
	 */
	private FileStoreClient fileStoreClient;
	
	/**
	 * where to upload/download files from RR
	 */
	private File fileUploadDownloadDir;
	
	/**
	 * CSV separator char
	 */
	private char separatorChar;
	
	/**
	 * CSV quote char
	 */
	private char quoteChar;
	

	/**
	 * Creates new instance of ResultsRepositoryCSVUtilsImpl
	 * @param fileStoreClient  file store client to use
	 * @param fileUploadDownloadDir    dir where to upload/download files from RR
	 * @param separatorChar    CSV separator char
	 * @param quoteChar CSV quote char
	 */
	public ResultRepositoryCSVUtilsImpl(FileStoreClient fileStoreClient,
			File fileUploadDownloadDir, char separatorChar, char quoteChar) {
		super();
		this.fileStoreClient = fileStoreClient;
		this.fileUploadDownloadDir = fileUploadDownloadDir;
		this.separatorChar = separatorChar;
		this.quoteChar = quoteChar;
	}

	@Override
	public void writeCSVFile(File destFile, String[] outputColumns, Collection<DataHandleTuple> data, boolean downloadFiles) throws IOException {
		
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(destFile), separatorChar, quoteChar);
		} catch (IOException e) {
			throw new IOException("Can't write into file "+destFile.getAbsolutePath(), e);
		}
		
		String[] row = new String[outputColumns.length];  // CSV row
		DataHandle dataHandle;  // single data handle from actual DataHandleTuple
		int fileCount = 0;
		try {
			for (DataHandleTuple tuple : data) {
				for (int i = 0; i < outputColumns.length; i++) {
					dataHandle = tuple.get(outputColumns[i]);
					// load data from DataHandle
					row[i] = dataHandle.getValue(dataHandle.getType().getJavaType()).toString();
					
					// download the file - if current dataHandle contains file and downloading files is enabled
					if( dataHandle.getType().equals(DataHandle.DataType.FILE)){
						// 	File store operations
						if( fileStoreClient == null ){
							throw new IOException("No FileStoreClient available");
						}

						UUID fileId = dataHandle.getValue(UUID.class);
						File f = getFileLocation(fileId);

						row[i] = f.getName();

						if (downloadFiles) {
							fileStoreClient.downloadFile(fileId, f);
							fileCount++;
						}
					}
				}
				writer.writeNext(row);
			}
		} catch (DataHandleException e) {
			throw new IOException("Can't read data from dataset",e);
		} catch (IOException e) {
			throw new IOException("Can't read data from dataset",e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				throw new IOException("Can't close file "+destFile.getAbsolutePath()+" because error occured.",e);
			}
		}
		//logInfo("Successfully created file "+destFile.getAbsolutePath());
		//if( fileCount > 0 ){
			//logInfo("Successfully downloaded "+fileCount+" data file(s) from the Results Repository");
		//}
	}
	
	
	@Override
	public List<DataHandleTuple> readCSVFile(File file, String[] inputColumns, DatasetDescriptor destDatasetDesc, boolean uploadFiles) throws IOException {
		List<DataHandleTuple> result = new ArrayList<DataHandleTuple>();
		
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file), separatorChar, quoteChar);
		} catch (FileNotFoundException e) {
			throw new IOException("Couldn't find file "+file.getAbsolutePath(), e);
		}

		int counter = 0;
		String[] nextLine; // CSV line
		DataHandle field;   // currently created DataHandleTuple entry
		String tag;
		DataHandleTuple row = new DataHandleTuple();  // RR representation of nextLine

		//logInfo("Reading results from file "+file.getAbsolutePath());
		try {
			while ((nextLine = reader.readNext()) != null) {
				if( nextLine.length != inputColumns.length ){
					throw new IOException("Error reading CSV file: Found a result row with size "+nextLine.length+" but it should be "+inputColumns.length);
				}
				for (int i = 0; i < inputColumns.length; i++) {
					tag = inputColumns[i];
					field = createDataHandle(tag, nextLine[i], destDatasetDesc, uploadFiles);  // uploads the file if tag is file
					row.set(tag, field);
				}
				result.add(row);
				counter++;
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				throw new IOException("Can't close file "+ file.getAbsolutePath()+" because error occured.",e);
			}
		}
		return result;
	}

	
	/**
	 * Creates data handle from raw value.
	 * 
	 * @param tagName name of tag  
	 * @param rawValue raw value
	 * @param datasetDescriptor dataset descriptor
	 * @param uploadFiles whether to upload files
	 * @return data handle
	 * @throws IOException 
	 * @throws ResultsRepositoryException 
	 */
	private DataHandle createDataHandle(String tagName, String rawValue, DatasetDescriptor datasetDescriptor, boolean uploadFiles) throws IOException {
		DataHandle result;
		DataType type = datasetDescriptor.get(tagName);
		if( type == DataType.DOUBLE){
			result = DataHandle.create(type, Double.valueOf(rawValue));
		} else if( type == DataType.FLOAT ){
			result = DataHandle.create(type, Float.valueOf(rawValue));
		} else if( type == DataType.INT ){
			result = DataHandle.create(type, Integer.valueOf(rawValue));
		} else if( type == DataType.LONG ){
			result = DataHandle.create(type, Long.valueOf(rawValue));
		} else if( type == DataType.SMALL_BINARY ){
			result = DataHandle.create(type, rawValue.getBytes());
		} else if( type == DataType.FILE ){
			if( fileStoreClient == null ){
				throw new IOException("No FileStoreClient available");
			}
			if (uploadFiles) {
				UUID uuid = fileStoreClient.uploadFile(new File(fileUploadDownloadDir, rawValue));
				result = DataHandle.create(type, uuid);
			} else {
				result = DataHandle.create(type, rawValue);
			}
		} else {
			// STRING, UUID, ... unknown
			result = DataHandle.create(type, rawValue);
		}
		return result;
	}

	@Override
	public File getFileLocation(UUID fileId) {
		
		String fileName = fileId.toString() + ".rda";
		
		return new File(fileUploadDownloadDir, fileName);
		
	}
}
