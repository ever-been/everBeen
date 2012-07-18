/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2010 Distributed Systems Research Group,
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
package cz.seznam.been.task.hintserver.logparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import au.com.bytecode.opencsv.CSVWriter;

import cz.cuni.mff.been.common.scripting.ScriptException;
import cz.cuni.mff.been.pluggablemodule.rscripting.RScriptingPluggableModule;
import cz.cuni.mff.been.pluggablemodule.rscripting.implementation.RScriptingPluggableModuleImpl;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.condition.AlwaysTrueCondition;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.EvaluatorTask;

/**
 * Task class that downloads the Hintserver log from the RR, parses it, creates
 * a graph and saves the parsed data back to the RR. This task uses R for
 * statistical computation and for generating the graph. 
 *
 * @author Jiri Tauber
 */
public class LogParser extends EvaluatorTask {

	/** The RR dataset tag that holds the log file */
	private static final String INPUT_TAG_LOGFILE = "log_file";
	/** The RR dataset tag that holds the requested RPS sequence */
	private static final String INPUT_TAG_RPS_SEQUENCE = "rps_sequence";

	/** Temporary file name for downloading the log from the RR */
	private static final String FILE_RAW_LOG = "raw.log";
	/** The R script file name */
	private static final String FILE_LOGPARSER_SCRIPT = "parselog.r";
	/** R input file that holds the RPS sequence in R-readable way */
	private static final String FILE_LOGPARSER_RPS = "requests.csv";  // Hardcoded in R script
	/** R input file that holds the preprocessed log data in R-readable way */
	private static final String FILE_LOGPARSER_DATA = "data.csv";     // Hardcoded in R script
	/** R output file - the graph */
	private static final String FILE_LOGPARSER_GRAPH = "graph.png";   // Hardcoded in R script

	/** The dataset name where to save the statistical results */
	private static final String OUTPUT_DATASET = "parsed_rps";
	/** The tag in output dataset where to save the run id */
	private static final String OUTPUT_TAG_RUN = "run_id";
	/** The tag in output dataset where to save the requested RPS number */
	private static final String OUTPUT_TAG_REQUESTS = "rps_requested";
	/** The tag in output dataset where to save the average served RPS number */
	private static final String OUTPUT_TAG_RPS_SERVED = "rps_served";

	/** Property name which says directory to which we should save the graph */
	private static final String PROP_GRAPH_DESTINATION = "graph.destination"; // copy in evaluator

	private long run_id = 0;

	public LogParser() throws TaskInitializationException {
		super();
	}

	@Override
	protected void doCheckRequiredProperties() throws TaskException {
		StringBuilder err = new StringBuilder();

		String propName = PROP_GRAPH_DESTINATION;
		String propValue = getTaskProperty(propName); 
		if (propValue == null || propValue.isEmpty()) {
			err.append("Property "+propName+" is missing;");
		} else {
			File file = new File(propValue);
			if (!file.exists()) {
				err.append("File "+propValue+" is missing;");
			} else if (!file.canRead()) {
				err.append("File "+propValue+" can not be read;");
			}
		}

		if (err.length() > 0){
			throw new TaskException(err.toString());
		}
	}


	@Override
	protected void run() throws TaskException {
		FileStoreClient fileStore;
		try {
			fileStore = getResultsRepository().getFileStoreClient();
		} catch (RemoteException e) {
			throw new TaskException("Error getting FileStoreClient", e);
		}

		long highestSerial = 0;
		boolean errorHappened = false;
		List<DataHandleTuple> rawData = loadNewData(new AlwaysTrueCondition());
		for (DataHandleTuple data : rawData) {
			run_id = data.getSerial();
			logInfo("Starting to process data with serial "+run_id);

			File rawLog = new File(getRDataFileName(FILE_RAW_LOG));
			String rpsSequence;
			try {
				rpsSequence = data.get(INPUT_TAG_RPS_SEQUENCE).getValue(String.class);
				fileStore.downloadFile(data.get(INPUT_TAG_LOGFILE).getValue(UUID.class), rawLog);
			} catch (DataHandleException e) {
				logError("Error reading DataHandle value");
				errorHappened = true;
				break;
			} catch (IOException e) {
				logError("Error saving log file to local storage");
				errorHappened = true;
				break;
			}

			try {
				List<LogEntry> logEntries = parseLog(rawLog);
				processLog(rpsSequence, logEntries);
				publishGraph();
				collectResults(rpsSequence);
			} catch (TaskException e) {
				logError(e.getMessage());
				errorHappened = true;
				break;
			}

			if (run_id > highestSerial){
				highestSerial = run_id;
			}
		}

		if (highestSerial > 0) {
			notifyDataProcessed(highestSerial);
		}
		if (errorHappened) {
			exitError();
		}
	}

	//------------------------------------------------------------------------//
	/**
	 * Moves the graph created by R to its destination pointed by a property.
	 */
	private void publishGraph() {
		String inputFilename = getRDataDirectory()+File.separator+FILE_LOGPARSER_GRAPH;
		String outputFilename = getTaskProperty(PROP_GRAPH_DESTINATION) + File.separator
				+ run_id + inputFilename.substring(inputFilename.lastIndexOf('.'));
		if (!(new File(inputFilename)).renameTo(new File(outputFilename))) {
			logError("Error renaming "+inputFilename+" to "+outputFilename);
		}
	}

	/**
	 * Collects the result files generated by R and saves them to the RR
	 * 
	 * @param rpsSequence The requested RPS sequence
	 * @throws TaskException
	 */
	private void collectResults(String rpsSequence) throws TaskException {
		// Collect results
		for (String rps : rpsSequence.split("[\\s;]+")){
			String filename = getRDataFileName(rps);
			File file = new File(filename);
			char[] buffer = new char[(int)file.length()];
			try {
				(new FileReader(filename)).read(buffer);
			} catch (FileNotFoundException e) {
				logError("Could not find the result file "+filename);
				continue;
			} catch (IOException e) {
				logError("Could not read the result file "+filename);
				continue;
			}
			// Prepare the data
			DataHandleTuple data = new DataHandleTuple();
			data.set(OUTPUT_TAG_RUN, run_id);
			data.set(OUTPUT_TAG_REQUESTS, Integer.decode(rps));
			data.set(OUTPUT_TAG_RPS_SERVED, new String(buffer));
			// Save data to the repository
			try {
				getResultsRepository().saveData(getAnalysisName(), OUTPUT_DATASET, data);
			} catch (ResultsRepositoryException e) {
				logError("Error while saving data to the repository"+": "+e);
				continue;
			} catch (RemoteException e) {
				throw new TaskException("Error wile saving data to the repository", e);
			}
		}
	}

	/**
	 * Runs R to generate graph and summary results from the log file
	 *  
	 * @param rpsSequence The requested RPS sequence
	 * @param logEntries Parsed log entries
	 * @throws TaskException
	 */
	private void processLog(String rpsSequence, List<LogEntry> logEntries) throws TaskException {
		Collections.sort(logEntries, new LogEntry.dateComparator());

		// Create data file for R
		prepareRData(rpsSequence, logEntries);

		// Run R script
		String scriptFile = getTaskDirectory()+File.separator+FILE_LOGPARSER_SCRIPT;
		RScriptingPluggableModule r = new RScriptingPluggableModuleImpl(getPluggableModuleManager());
		try {
			r.runRScript(new File(scriptFile), getRDataDirectory());
		} catch (ScriptException e) {
			throw new TaskException("Error during script Execution", e);
		}

	}


	/**
	 * Prepares data files for the R script
	 *
	 * @param logEntries The parsed log entries
	 * @throws TaskException 
	 */
	private void prepareRData(String rpsSequence, List<LogEntry> logEntries) throws TaskException {
		long startTime = logEntries.get(0).getDate().getTime();
		String dataFileName = getRDataFileName(FILE_LOGPARSER_DATA);
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(dataFileName));
		} catch (IOException e) {
			throw new TaskException("Error creating data file for R "+dataFileName, e);
		}
		// Write csv header
		writer.writeNext(new String[]{
				"time", "status", "duration", "client", "request"});
		// Write all the entries
		for (LogEntry entry : logEntries) {
			writer.writeNext(new String[]{
				Long.toString(entry.getDate().getTime()-startTime),
				Integer.toString(entry.getStatus()),
				Float.toString(entry.getDuration()),
				entry.getClient(),
				entry.getRequest()
			});
		}
		// Save the file
		try {
			writer.close();
		} catch (IOException e) {
			throw new TaskException("Error saving data file for R "+dataFileName, e);
		}

		// Create the rps sequence data file 
		String rpsFileName = getRDataFileName(FILE_LOGPARSER_RPS);
		try {
			FileWriter rpsWriter = new FileWriter(rpsFileName);
			rpsWriter.write(rpsSequence);
			rpsWriter.close();
		} catch (IOException e) {
			throw new TaskException("Error saving file for R "+rpsFileName, e);
		}
	}

	/**
	 * Parses downloaded Hintserver log file. Finds relevant entries and converts
	 * them into a list of objects.
	 * 
	 * @param rawLog The log file
	 * @return the list of important log entries
	 * @throws TaskException
	 */
	private List<LogEntry> parseLog(File rawLog) throws TaskException {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(rawLog));
		} catch (FileNotFoundException e) {
			throw new TaskException("The downloaded log file was not found", e);
		}
		List<LogEntry> entryList = new ArrayList<LogEntry>();
		LogEntry entry = null;

		String line = null;
		int lineNumber = 1;
		try {
			while ((line = reader.readLine()) != null) {
				entry = LogEntry.decode(line);
				if (entry != null) {
					entryList.add(entry);
				}
				lineNumber++;
			}
		} catch (IOException e) {
			throw new TaskException("Error reading the downloaded log file", e);
		} catch (ParseException e) {
			throw new TaskException("Error in log format on line "+lineNumber+": "+line, e);
		}

		return entryList;
	}

	/**
	 * Convenience function for retrieving the filename of R input
	 * @param nameBase the base name of the file
	 * @return complete file path and name
	 */
	private String getRDataFileName(String nameBase){
		return getRDataDirectory() + File.separator + nameBase;
	}

	/**
	 * Convenience functino for retrieving the directory where R will run
	 * @return The R data directory
	 */
	private String getRDataDirectory(){
		String directory = getTempDirectory()+File.separator+"run_"+run_id;
		File f = new File(directory);
		if (!f.exists()) {
			f.mkdir();
		}
		return directory;
	}

}
