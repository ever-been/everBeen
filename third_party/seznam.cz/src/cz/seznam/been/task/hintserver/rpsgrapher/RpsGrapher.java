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
package cz.seznam.been.task.hintserver.rpsgrapher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

import cz.cuni.mff.been.common.scripting.ScriptException;
import cz.cuni.mff.been.pluggablemodule.rscripting.RScriptingPluggableModule;
import cz.cuni.mff.been.pluggablemodule.rscripting.implementation.RScriptingPluggableModuleImpl;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.EvaluatorTask;

/**
 * Task that takes preprocessed data and creates graph of served RPS based on
 * requested RPS. We use R script to process the data and create the graph. 
 *
 * @author Jiri Tauber
 */
public class RpsGrapher extends EvaluatorTask {

	//-----------------------------------------------------------------------//
	/** The name of dataset tag that holds requested RPS count */
	private static final String TAGNAME_REQUESTS = "rps_requested";  // HintserverEvaluator.OUTPUT_TAG_REQUESTS;
	/** The name of dataset tag that holds served RPS count */
	private static final String TAGNAME_SERVED = "rps_served";  // HintserverEvaluator.OUTPUT_TAG_RPS_SERVED;

	/** The name of the R script file */
	private static final String FILENAME_SCRIPT = "script.r";
	/** The name of the R script input file */
	private static final String FILENAME_R_IN = "data.csv";    // hardcoded in R script
	/** The name of the graph file that R produces*/
	private static final String FILENAME_R_OUT = "graph.png";  // hardcoded in R script

	/** The name of the property that tells where to store the graph permanently */
	private static final String PROP_DESTINATION = "graph.destination";
	//-----------------------------------------------------------------------//

	public RpsGrapher() throws TaskInitializationException {
		super();
	}


	//-----------------------------------------------------------------------//
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.EvaluatorTask#doCheckRequiredProperties()
	 */
	@Override
	protected void doCheckRequiredProperties() throws TaskException {
		StringBuilder err = new StringBuilder();

		String propName = PROP_DESTINATION;
		String propValue = getTaskProperty(propName); 
		if (propValue == null || propValue.isEmpty()) {
			err.append("Property "+propName+" is missing;");
		} else {
			File file = new File(propValue);
			File parent = file.getParentFile();
			if (parent == null) {
				err.append("Couldn't find the parent directory of "+file.getAbsolutePath());
			} else if (parent.exists() && !parent.isDirectory()) {
				err.append(parent.getAbsolutePath()+" is not a directory;");
			} else if (!parent.canWrite()) {
				err.append(parent.getAbsolutePath()+" is not writable;");
			}
		}

		if (err.length() > 0){
			throw new TaskException(err.toString());
		}
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.EvaluatorTask#run()
	 */
	@Override
	protected void run() throws TaskException {

		long highestSerial = 0;
		List<DataHandleTuple> rawData = loadAllData();

		// create the data file
		try {
			highestSerial = createDataFile(rawData);
		} catch (DataHandleException e) {
			throw new TaskException("Error reading loaded data", e);
		} catch (IOException e) {
			throw new TaskException("Error creating the data file", e);
		}

		// run the R script
		String scriptFile = getTaskDirectory() + File.separator + FILENAME_SCRIPT;
		logInfo("Running R script "+scriptFile);
		RScriptingPluggableModule r = new RScriptingPluggableModuleImpl(getPluggableModuleManager());
		try {
			r.runRScript(new File(scriptFile), getRdataDir());
		} catch (ScriptException e) {
			throw new TaskException("Error during script Execution", e);
		}

		// save the output graph to the destination
		String outFileName = getRdataDir() + File.separator + FILENAME_R_OUT;
		String graphDestinationFileName = getTaskProperty(PROP_DESTINATION);
		logInfo("Renaming file "+outFileName+" to file "+graphDestinationFileName);
		File graphDestination = new File(graphDestinationFileName);
		graphDestination.getParentFile().mkdirs();  //  parent checked in parameters check
		(new File(outFileName)).renameTo(graphDestination);

		notifyDataProcessed(highestSerial);
	}


	//-----------------------------------------------------------------------//
	/**
	 * Dumps the data from RR to the R input file so that R can use the data.
	 * For convenience it returns the highest data serial found.
	 * 
	 * @param rawData The data as loaded from the RR 
	 * @return the highest data serial
	 * @throws DataHandleException - error reading or converting the input 
	 * @throws IOException - error writing the file
	 */
	private long createDataFile(List<DataHandleTuple> rawData) throws DataHandleException, IOException {
		long highestSerial = 0;
		String dataFileName = getRdataDir() + File.separator + FILENAME_R_IN;
		logInfo("Saving "+rawData.size()+" rows into file "+dataFileName);

		CSVWriter writer = new CSVWriter(new FileWriter(dataFileName));

		// Write all the entries
		writer.writeNext(new String[]{"reuests", "served"});
		for (DataHandleTuple tuple : rawData) {
			writer.writeNext(new String[]{
					tuple.get(TAGNAME_REQUESTS).getValue(Integer.class).toString(),
					tuple.get(TAGNAME_SERVED).getValue(Integer.class).toString()
			});
			if (tuple.getSerial() > highestSerial){
				highestSerial = tuple.getSerial();
			}
		}
		// Save the file
		writer.close();

		return highestSerial;
	}

	/**
	 * Convenience function for determining where the R script is going to run
	 * @return the R working & data directory
	 */
	private String getRdataDir() {
		return getWorkingDirectory();
	}

}
