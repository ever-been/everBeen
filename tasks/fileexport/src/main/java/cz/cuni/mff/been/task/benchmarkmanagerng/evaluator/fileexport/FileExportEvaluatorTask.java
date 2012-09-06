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

package cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.fileexport;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cz.cuni.mff.been.resultsrepositoryng.condition.AlwaysTrueCondition;
import cz.cuni.mff.been.resultsrepositoryng.condition.Condition;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.CommonEvaluatorProperties;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.EvaluatorTask;

/**
 * @author Jiri Tauber
 *
 */
public class FileExportEvaluatorTask extends EvaluatorTask {

	private static final String PATTERN_IDENTIFIER = "[a-zA-Z_][a-zA-Z0-9._-]*";
	private static final String PATTERN_VAR = "\\?"+PATTERN_IDENTIFIER+"\\?";
	private static final String PATTERN_PATH = "([^?*]*|"+PATTERN_VAR+")*";

	/**
	 * Class used for replacing path variables with their values
	 * It's initialized with the path template and fixed variables.
	 * After all variables are replaced, {@link String#replaceAll(String, String)}
	 * is called.
	 * 
	 * @author Jiri Tauber
	 */
	private class PathSolver {
		private String[] parts;
		private String replace_src = null;
		private String replacement = null;

		/**
		 * @param template The path template in form abc?variable?...
		 * @param fixedVars the name-value pairs of variables that never change
		 * @param replace_src Regular expression searched in the "final" path for replacement
		 * @param replacement what should we replace the matches with (also regexp)
		 */
		public PathSolver(String template, HashMap<String, String> fixedVars,
				String replace_src, String replacement) {
			parts = template.split("\\?");
			String var;
			for(int i = 1; i < parts.length; i+=2) {
				var = fixedVars.get(parts[i]);
				if( var != null ){
					parts[i-1] = parts[i-1]+var;
					parts[i] = null;
				}
			}
			this.replace_src = replace_src;
			this.replacement = replacement;
			System.out.println("Path solver initialized: "+template+", "
					+this.replace_src+", "+this.replacement);
		}

		/**
		 * Replaces all variables in the template with their values found in {@code vars}.
		 * After all variables are replaced, {@link String#replaceAll(String, String)}
		 * is called to further modify the path.
		 * 
		 * @param vars the variables in dataHandle format
		 * @return The path string created from template by replacing variable names with their values
		 * @throws cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException if there is error retrieving the data
		 */
		public String solve(DataHandleTuple vars) throws DataHandleException {
			StringBuilder result = new StringBuilder();
			int i = -1;
			for (String str : parts) {
				i++;
				if( str == null ) continue;
				if( i % 2 == 0 ){
					result.append(str);
				} else {
					DataHandle data = vars.get(str);
					result.append(data.getValue(data.getType().getJavaType()));
				}
			}
			if( replace_src != null ){
				return result.toString().replaceAll(replace_src, replacement);
			}
			return result.toString();
		}
	}

	/** condition on the exported data (passed directly to the RR) */
	private Condition dataCondition = null;
	
	/** Class that returns the file paths */
	private PathSolver pathSolver = null;

	/** Name of the tag that contains the file */
	private String fileTagName = null;

	/** Flag that determines whether existing files are overwritten */
	private boolean overwrite = false;


	/**
	 * @throws cz.cuni.mff.been.task.TaskInitializationException
	 */
	public FileExportEvaluatorTask() throws TaskInitializationException {
		super();
	}


	@Override
	protected void checkRequiredProperties() throws TaskException {
		if( getTaskProperty(CommonEvaluatorProperties.TRIGGER_ID) != null ){
			// running as a trigger
			super.checkRequiredProperties();
			return;
		}

		// Running as standalone task
		checkRequiredProperties( new String[]{
				CommonEvaluatorProperties.ANALYSIS_NAME,
				CommonEvaluatorProperties.DATASET_NAME
		});

		doCheckRequiredProperties();
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.EvaluatorTask#doCheckRequiredProperties()
	 */
	@Override
	protected void doCheckRequiredProperties() throws TaskException {
		StringBuilder errors = new StringBuilder();

		String property = getTaskProperty(Properties.FILE_TAG);
		if( property != null ){
			if( !property.matches(PATTERN_IDENTIFIER) ) {
				errors.append("Invalid format of "+Properties.FILE_TAG+";");
			}
		}

		property = getTaskProperty(Properties.DESTINATION_FILE);
		if( property != null ){
			if( !property.matches(PATTERN_PATH) ) {
				errors.append("Invalid format of "+Properties.DESTINATION_FILE+";");
			}
		}

		Serializable prop = getTaskPropertyObject(Properties.PROP_CONDITION);
		if( prop != null ){
			if( !(prop instanceof Condition) ) {
				errors.append("Data condition object has invalid type: "+prop.getClass()+";");
			}
		}

		if(errors.length() > 0){
			throw new TaskException(errors.toString());
		}
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	protected void run() throws TaskException {
		loadProperties();

		// load the data
		List<DataHandleTuple> data;
		if( getBooleanTaskProperty(Properties.DO_PROCESS_NEW_ONLY) ){
			data = loadNewData(dataCondition);
		} else {
			data = loadData(dataCondition, null, null);
		}
		// check the data integrity
		if( data.isEmpty() ){
			logError("No data");
			exitError();
		}
		if( data.get(0).get(fileTagName) == null ){
			throw new TaskException(fileTagName+" is not a tag in this dataset");
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

		if( getTaskProperty(Properties.TRIGGER_ID) != null ){
			notifyDataProcessed(highestDataSerial);
		}
	}


	/**
	 * 
	 */
	private void loadProperties() {
		fileTagName = getTaskProperty(Properties.FILE_TAG);

		Serializable prop = getTaskPropertyObject(Properties.PROP_CONDITION);
		if( prop != null && prop instanceof Condition ) {
			dataCondition = (Condition)prop;
		} else {
			dataCondition = new AlwaysTrueCondition();
		}

		HashMap<String, String> vars = new HashMap<String, String>(4);
		vars.put("analysis.name", getTaskProperty(Properties.ANALYSIS_NAME));
		vars.put("dataset.name", getTaskProperty(Properties.DATASET_NAME));
		pathSolver = new PathSolver(
				getTaskProperty(Properties.DESTINATION_FILE),
				vars,
				getTaskProperty(Properties.REPLACEMENT_SRC),
				getTaskProperty(Properties.REPLACEMENT_DEST, ""));

		overwrite = getBooleanTaskProperty(Properties.DO_OVERWRITE);
	}


	/**
	 * @param data
	 */
	private void createDataFiles(List<DataHandleTuple> data) throws TaskException {
		int count = 0;
		try {
			FileStoreClient repository = getResultsRepository().getFileStoreClient();
			for (DataHandleTuple tuple : data) {
				UUID fileId = tuple.get(fileTagName).getValue(UUID.class);  // NULL pointer exc.
				String fileName = pathSolver.solve(tuple);
				File localFile = new File(fileName);
				if( localFile.exists() ){
					if( overwrite ){
						logWarning("Overwriting file "+localFile);
						repository.downloadFile(fileId, localFile);
						count++;
					} else {
						logWarning("File "+localFile+" already exists (no action)");
					}
				} else {
					String dirName = fileName.substring(0, fileName.lastIndexOf(File.separatorChar));
					File dir = new File(dirName);
					if( !dir.exists() ){
						logInfo("Creating directory "+dirName);
						if( !dir.mkdirs() ){
							throw new TaskException("Can't create path to file "+localFile.getAbsolutePath());
						}
					}
					repository.downloadFile(fileId, localFile);
					count++;
				}
				System.out.println(localFile);
			}
		} catch (DataHandleException e) {
			logError("Can't read data from dataset"+" because error occured: "+e.getMessage());
			throw new TaskException("Can't read data from dataset",e);
		} catch (IOException e) {
			logError("Can't save file"+" because error occured: "+e.getMessage());
			throw new TaskException("Can't save file",e);
		}
		logInfo("Successfully downloaded "+count+" file(s)");
		System.out.println();
	}

}
