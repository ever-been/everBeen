/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
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

package cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.r;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.condition.AlwaysTrueCondition;
import cz.cuni.mff.been.resultsrepositoryng.condition.Condition;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle.DataType;
import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.EvaluatorTask;

/**
 * @author Jiri Tauber
 *
 */
public class RTask extends EvaluatorTask {

	public static final String DATA_IN_FILE = "input.csv";
	public static final String SCRIPT_FILE = "script.r";
	public static final String DATA_OUT_FILE = "output.csv";
	public static final String R_STDOUT_FILE = "r.out";

	private static final String PATTERN_IDENTIFIER = "^[\\w]+$";
	private static final String PATTERN_IDENTIFIER_LIST = "^[\\w]+(,[\\w]+)*$";

	private static final String SEPARATOR = "----------------------------------------";

	private char separatorChar = ';';
	private char quoteChar = '"';
	private Condition dataCondition = null;

	private FileStoreClient fileStoreClient = null;
	private DatasetDescriptor destinationDatasetDescriptor = null;

	/**
	 * @throws cz.cuni.mff.been.task.TaskInitializationException
	 */
	public RTask() throws TaskInitializationException {
		super();
	}

	//***** Check ************************************************************//
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.EvaluatorTask#doCheckRequiredProperties()
	 */
	@Override
	protected void doCheckRequiredProperties() throws TaskException {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}

		StringBuilder errors = new StringBuilder();

		// R Script existence
		String property = getTaskProperty(Properties.R_SCRIPT);
		if( property == null ){
			errors.append(Properties.R_SCRIPT+" is null;");
		}

		// Source dataset tags validity
		property = getTaskProperty(Properties.SOURCE_TAGS);
		if( property == null ){
			errors.append(Properties.SOURCE_TAGS+" is null;");
		} else if( !property.matches(PATTERN_IDENTIFIER_LIST) ) {
			errors.append(Properties.SOURCE_TAGS+
					" is not a comma separated list of java identifiers;");
		} else {
			DatasetDescriptor descriptor = getDatasetDescriptor(
					getTaskProperty(Properties.DATASET_NAME));
			if( descriptor == null ){
				errors.append("Error getting source dataset descriptor;");
			} else {
				for(String tag : getSourceTags()){
					if( descriptor.get(tag) == null ){
						errors.append("Tag '"+tag+"' doesn't exist in source dataset;");
					}
				}
			}
		}

		// Destination dataset tags validity - mandatory only if dataset is given
		property = getTaskProperty(Properties.DESTINATION_DATASET);
		if( property != null ){
			if( !property.matches(PATTERN_IDENTIFIER) ) {
				errors.append(Properties.DESTINATION_DATASET+" is not a java identifier;");
			} else {

				property = getTaskProperty(Properties.DESTINATION_TAGS);
				if( property == null ){
					errors.append(Properties.DESTINATION_TAGS+" is null;");
				} else {
					destinationDatasetDescriptor = getDatasetDescriptor(
							getTaskProperty(Properties.DESTINATION_DATASET));
					DatasetDescriptor tags = null;
					try{
						tags = getDestinationDatasetDescriptor();
					} catch (TaskException e){
						errors.append(e.getMessage());
						errors.append(";");
					}
					if( tags != null ) for (String tagName : tags.tags()) {
						if( !tags.get(tagName).equals(destinationDatasetDescriptor.get(tagName)) ){
							errors.append("'"+tagName+"' in "+Properties.DESTINATION_TAGS+
									" doesn't have the same type as in the destination dataset;");
						}
					}
				}

			}
		}

		// Miscellaneous optional properties
		property = getTaskProperty(Properties.SEPARATOR_CHAR);
		if( property != null ){
			if( property.length() != 1 ) {
				errors.append(Properties.SEPARATOR_CHAR+" is not a single character;");
			}
		}

		property = getTaskProperty(Properties.QUOTE_CHAR);
		if( property != null ){
			if( property.length() != 1 ) {
				errors.append(Properties.QUOTE_CHAR+" is not a single character;");
			}
		}

		Serializable prop = getTaskPropertyObject(Properties.CONDITION);
		if( prop != null ){
			if( !(prop instanceof Condition) ) {
				errors.append("Data condition object has invalid type: "+prop.getClass()+";");
				dataCondition = new AlwaysTrueCondition();
			}
		} 

		if(errors.length() > 0){
			throw new TaskException(errors.toString());
		}
	}


	/**
	 * Loads the named dataset descriptor from the results repository
	 * @param datasetName
	 * @return The descriptor
	 */
	private DatasetDescriptor getDatasetDescriptor(String datasetName) {
		RRManagerInterface rr = (RRManagerInterface)getResultsRepository();
		DatasetDescriptor result = null;
		try {
			result = rr.getDatasetDescriptor(getTaskProperty(Properties.ANALYSIS_NAME), datasetName);
		} catch (ResultsRepositoryException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * Returns the list of tags contained in the {@code SOURCE_TAGS} task property.
	 * The tag names are kept in in the property as a comma-separated list
	 * @return the source tags
	 */
	private String[] getSourceTags() {
		return getTaskProperty(Properties.SOURCE_TAGS).split(",");
	}


	/**
	 * Parses the {@code DESTINATION_TAGS} task parameters to create the destination dataset.
	 * Destination dataset description is in the format: {@code [tag_name]:[type]}
	 * or {@code [tag_name=[type]} where type is any DataType.
	 * @return The DatasetDescriptor
	 * @throws cz.cuni.mff.been.task.TaskException
	 */
	private DatasetDescriptor getDestinationDatasetDescriptor() throws TaskException {
		String property = getTaskProperty(Properties.DESTINATION_TAGS)+"\n";
		StringBuilder regex = new StringBuilder("^([a-zA-Z_][a-zA-Z0-9_]*[:=](");
		for (DataType type : DataType.values()) {
			regex.append(type.name());
			regex.append('|');
		}
		regex.deleteCharAt(regex.length()-1);
		regex.append(")[\\n\\r]+)+$");
		if(!property.matches(regex.toString())){
			throw new TaskException("Destination tags have invalid format");
		}

		DatasetDescriptor destinationTags = new DatasetDescriptor();
		String[] entries;
		for (String line : property.split("[\\r\\n]+")) {
			entries = line.split("[:=]");
			destinationTags.put(entries[0], DataType.valueOf(entries[1]), false);
		}
		return destinationTags;
	}


	private String[] getDestinationTags(){
		String property = getTaskProperty(Properties.DESTINATION_TAGS);
		ArrayList<String> result = new ArrayList<String>();
		String[] entries;
		for (String line : property.split("\\n")) {
			entries = line.split("[:=]");
			result.add(entries[0]);
		}
		return result.toArray(new String[0]);
	} 


	//***** Run **************************************************************//
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	protected void doRun() throws TaskException {

		loadProperties();

		createScriptFile();


		// load the data
		List<DataHandleTuple> data;
		if( getBooleanTaskProperty(Properties.DO_PROCESS_OLD) ){
			data = loadData(dataCondition, null, null);
		} else {
			data = loadNewData(dataCondition);
		}

		// get the highest serial
		long highestDataSerial = 0;
		for (DataHandleTuple tuple : data) {
			if( tuple.getSerial() > highestDataSerial ){
				highestDataSerial = tuple.getSerial();
			}
		}

		// do the real work
		createDataFiles(data);
		data = null;
		System.gc(); // try to free the data memory
		runR();
		dumpRout();

		saveResults();

		notifyDataProcessed(highestDataSerial);
	}


	/**
	 * 
	 */
	private void loadProperties() {

		String property = getTaskProperty(Properties.QUOTE_CHAR, ""+quoteChar);
		if( property.length() == 1 ){
			quoteChar = property.charAt(0);
		} else if( property.length() > 1 ){
			logWarning(Properties.QUOTE_CHAR+" is more than 1 character long and was ignored");
		}

		property = getTaskProperty(Properties.SEPARATOR_CHAR, ""+separatorChar);
		if( property.length() == 1 ){
			separatorChar = property.charAt(0);
		} else if( property.length() > 1 ){
			logWarning(Properties.SEPARATOR_CHAR+" is more than 1 character long and was ignored");
		}

		Serializable prop = getTaskPropertyObject(Properties.CONDITION);
		if( prop != null && prop instanceof Condition ){
			dataCondition = (Condition)prop;
		} else {
			dataCondition = new AlwaysTrueCondition();
		} 

		try {
			fileStoreClient = getResultsRepository().getFileStoreClient();
		} catch (RemoteException e) {
			logWarning("Unable to get Results Repository file store client");
		}
	}


	/**
	 * @throws cz.cuni.mff.been.task.TaskException
	 */
	private void createScriptFile() throws TaskException {
		File file = new File(getFilePath(SCRIPT_FILE));
		try {
			FileWriter writer;
			writer = new FileWriter(file);
			writer.append(getTaskProperty(Properties.R_SCRIPT));
			writer.close();
		} catch (IOException e) {
			logFatal("Can't write into file "+SCRIPT_FILE+" because error occured: "+e.getMessage());
			throw new TaskException("Can't write into file "+SCRIPT_FILE,e);
		}
	}


	/**
	 * @param data
	 */
	private void createDataFiles(List<DataHandleTuple> data) throws TaskException {
		String fileName = getFilePath(DATA_IN_FILE);
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(fileName), separatorChar, quoteChar);
		} catch (IOException e) {
			logFatal("Can't write into file "+fileName+" because error occured: "+e.getMessage());
			throw new TaskException("Can't write into file "+fileName, e);
		}

		String[] sourceTags = getSourceTags();
		String[] row = new String[sourceTags.length];  // CSV row
		DataHandle dataHandle;  // single data handle from actual DataHandleTuple
		int fileCount = 0;
		try {
			for (DataHandleTuple tuple : data) {
				for (int i = 0; i < sourceTags.length; i++) {
					dataHandle = tuple.get(sourceTags[i]);
					// load data from DataHandle
					row[i] = dataHandle.getValue(dataHandle.getType().getJavaType()).toString();

					// download the file - if current dataHandle contains file
					if( dataHandle.getType().equals(DataType.FILE)){
						// File store operations
						if( fileStoreClient == null ){
							throw new IOException("No FileStoreClient available");
						}
						UUID fileId = dataHandle.getValue(UUID.class);
						row[i] += ".rda";
						fileStoreClient.downloadFile(fileId,
								new File(getFilePath(row[i])));
						fileCount++;
					}
				}
				writer.writeNext(row);
			}
		} catch (DataHandleException e) {
			logError("Can't read data from dataset"+" because error occured: "+e.getMessage());
			throw new TaskException("Can't read data from dataset",e);
		} catch (IOException e) {
			logError("Can't read data from dataset"+" because error occured: "+e.getMessage());
			throw new TaskException("Can't read data from dataset",e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				logError("Can't close file "+fileName+" because error occured: "+e.getMessage());
			}
		}
		logInfo("Successfully created file "+fileName);
		if( fileCount > 0 ){
			logInfo("Successfully downloaded "+fileCount+" data file(s) from the Results Repository");
		}
	}


	/**
	 * 
	 */
	private void runR() throws TaskException {
		ProcessBuilder procBuilder = new ProcessBuilder();

		procBuilder.directory(new File(getFilePath(null)));

		// build the command line
		List<String> cmdArray = new ArrayList<String>();
		cmdArray.add("R");
		cmdArray.add("CMD");
		cmdArray.add("BATCH");
		cmdArray.add("--vanilla");

		cmdArray.add(SCRIPT_FILE);
		cmdArray.add(R_STDOUT_FILE);

		procBuilder.command(cmdArray);

		{
			StringBuilder logStr = new StringBuilder("Running command:>");
			for (String string : cmdArray) {
				logStr.append(string);
				logStr.append(' ');
			}
			logInfo(logStr.toString());
		}

		Process p = null;
		int result = -1;
		try {
			p = procBuilder.start();
			result = p.waitFor();
		} catch (IOException e) {
			throw new TaskException("Error starting R",e);
		} catch (InterruptedException e) {
			if (p != null) { p.destroy(); }															// 'if' avoids stupid warnings.
			throw new TaskException("Waiting for R was interrupted",e);
		}

		if( result == 0){
			logInfo("R exitted with code "+result);
		} else {
			logWarning("R exitted with code "+result);
		}
	}


	/**
	 * @throws cz.cuni.mff.been.task.TaskException
	 * 
	 */
	private void dumpRout() throws TaskException {
		String fileName = getFilePath(R_STDOUT_FILE);
		try {
			FileReader reader = new FileReader(fileName);
			int c;
			while( (c = reader.read()) != -1 ){
				System.out.print((char)c);
			}
		} catch (FileNotFoundException e) {
			throw new TaskException("R output file wasn't found");
		} catch (IOException e) {
			throw new TaskException("Error reading R output file");
		}
		System.out.println(SEPARATOR);
		
	}


	/**
	 * @throws cz.cuni.mff.been.task.TaskException
	 * 
	 */
	private void saveResults() throws TaskException {
		String datasetName = getTaskProperty(Properties.DESTINATION_DATASET);
		if( datasetName == null ){
			logInfo("No destination dataset given - will not save data");
			return;
		}
		String analysisName = getTaskProperty(Properties.ANALYSIS_NAME);

		// Save the output data
		String fileName = getFilePath(DATA_OUT_FILE);
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(fileName), separatorChar, quoteChar);
		} catch (FileNotFoundException e) {
			logError("Couldn't find file "+fileName);
			e.printStackTrace();
			return;
		}

		int counter = 0;
		String[] destinationTags = getDestinationTags();
		String[] nextLine; // CSV line
		DataHandle field;   // currently created DataHandleTuple entry
		String tag;
		DataHandleTuple row = new DataHandleTuple();  // RR representation of nextLine

		logInfo("Reading results from file "+DATA_OUT_FILE);
		try {
			while ((nextLine = reader.readNext()) != null) {
				if( nextLine.length != destinationTags.length ){
					logWarning("Found a result row with size "+nextLine.length+" but it should be "+destinationTags.length);
				}
				for (int i = 0; i < destinationTags.length; i++) {
					tag = destinationTags[i];
					field = createDataHandle(tag, nextLine[i]);  // uploads the file if tag is file
					row.set(tag, field);
				}
				getResultsRepository().saveData(analysisName, datasetName, row);
				counter++;
			}
		} catch (IOException e) {
			logError("Error reading a file: "+e.getMessage());
			throw new TaskException("Can't read a file", e);
		} catch (ResultsRepositoryException e) {
			logError("Error saving data to the results repository: "+e.getMessage());
			throw new TaskException("Can't save data to the results repository", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				logError("Can't close file "+fileName+" because error occured: "+e.getMessage());
			}
		}
		logInfo("Successfully saved "+counter+" entries to the Results Repository");
	}


	/**
	 * Converts string value to appropriate DataHandle if possible.
	 * Uses {@code destinationDatasetDescriptor} to find out the required type of the DataHandle.
	 * @param tagName The tag name
	 * @param rawValue String representation of the data.
	 * @return created data handle
	 * @throws java.io.IOException
	 * @throws cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException
	 */
	private DataHandle createDataHandle(String tagName, String rawValue) throws IOException, ResultsRepositoryException {
		if( destinationDatasetDescriptor == null ){
			destinationDatasetDescriptor = getDatasetDescriptor(
					getTaskProperty(Properties.DESTINATION_DATASET));
		}
		DataHandle result = null;
		DataType type = destinationDatasetDescriptor.get(tagName);
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
				throw new ResultsRepositoryException("No FileStoreClient available");
			}
			UUID uuid = fileStoreClient.uploadFile(new File(getFilePath(rawValue)));
			result = DataHandle.create(type, uuid);
		} else {
			// STRING, UUID, ... unknown
			result = DataHandle.create(type, rawValue);
		}
		return result;
	}

	/**
	 * @param fileName
	 * @return path to a file in the working directory or the working directory if fileName is null
	 */
	private String getFilePath(String fileName) {
		if( fileName == null ){
			return getWorkingDirectory();
		} else {
			return getWorkingDirectory()+File.separator+fileName;
		}
	}

}
