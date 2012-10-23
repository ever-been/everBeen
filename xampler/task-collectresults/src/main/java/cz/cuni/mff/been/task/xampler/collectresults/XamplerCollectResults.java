/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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
package cz.cuni.mff.been.task.xampler.collectresults;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgent;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgentPluggableModule;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

public class XamplerCollectResults extends Job {
    
    /** Name of analysis of dataset where to store results */
    public static final String RESULTS_ANALYSIS_NAME = "rr.results.analysis";
    
    /** Dataset where to store results */
    public static final String RESULTS_DATASET_NAME = "rr.results.dataset";
    
    /** Xampler revision timestamp */
    public static final String XAMPLER_REVISION = "xampler.revision";
    
    /** Xampler version revision */
    public static final String OMNIORB_TIMESTAMP = "omniorb.timestamp";
    
    /** Build number */
    public static final String BUILD_NUMBER = "build.number";
    
    /** Run number */
    public static final String RUN_NUMBER = "run.number";
    
    /** Directory to which to deploy xampler */
    public static final String SUITE_NAME = "suite.name";
    
    /** Directory to which to deploy xampler */
    public static final String RESULTS_DIR = "results.dir";
    
    /** Simulate property doesn't have any effect on this task. */
    public static final String SIMULATE = "simulate";
    
    
    /** RR_RESULTS_ANALYSIS_NAME property value */
    private String resultsAnalysis;
    
    /** RR_RESULTS_DATASET_NAME property value */
    private String resultsDataset;
    
    /** XAMPLER_REVISION property value */
    private Long xamplerRevision;
    
    /** OMNIORB_TIMESTAMP property value */
    private String omniorbTimestamp;
    
    /** BUILD_NUMBER property value */
    private Integer buildNumber;
    
    /** RUN_NUMBER property value */
    private Integer runNumber;
    
    /** SUITE_NAME property value */
    private String suiteName;
    
    /** RESULTS_DIR property value */
    private String resultsDir;

    
	/** file agent pluggable module */
	private FileAgentPluggableModule fileAgentModule;

	private boolean simulate;

    /** dataset's field for storing build_number */
    private static final String BUILD_NUMBER_FIELD_NAME = "build_number";
    
    /** dataset's field for storing run_number */
    private static final String RUN_NUMBER_FIELD_NAME = "run_number";
    
    /** dataset's field for storing omniorb's timestamp */
    private static final String OMNIORB_TIMESTAMP_FIELD_NAME = "omniorb_timestamp";
    
    /** dataset's field for storing xampler's revision */
    private static final String XAMPLER_REVISION_FIELD_NAME = "xampler_revision";

    /** dataset's field for storing revision */
    private static final String SUITE_FIELD_NAME = "suite_name";
    
    /** dataset's field for storing file id */
    private static final String FILE_ID_FIELD_NAME = "fileid";
    
	
	public XamplerCollectResults() throws TaskInitializationException {
		super();
	}
	
	@Override
	protected void run() throws TaskException {
		/* load pluggable modules */
		loadPluggableModules();
		
		/* files */
		File resultsFile = new File(resultsDir,"client.out");
		
		boolean fileExists = resultsFile.exists();
		
		if (!fileExists) {
			logFatal("Desired xampler output file does not exists (run will be marked as failed)");
		}
		
		if(simulate) {
			/* SIMULATE and NORMAL mode do the same work for this job*/
		}
		
		/* upload results to RR */
		try {
			FileAgent agent = fileAgentModule.createRRFileAgent(resultsAnalysis, resultsDataset, FILE_ID_FIELD_NAME);
			
			HashMap<String, Serializable> tags = new HashMap<String, Serializable>();
			tags.put(XAMPLER_REVISION_FIELD_NAME, xamplerRevision);
			tags.put(OMNIORB_TIMESTAMP_FIELD_NAME, omniorbTimestamp);
			tags.put(BUILD_NUMBER_FIELD_NAME, buildNumber);
			tags.put(RUN_NUMBER_FIELD_NAME, runNumber);
			tags.put(SUITE_FIELD_NAME, suiteName);
			
			if (fileExists) {
				agent.storeFile(resultsFile, tags);
			} else {
				logFatal("Xampler results file not present. Null file reference will be stored to RR instead.");
				agent.storeFile(null, tags);
			}
		} catch (IOException e) {
			throw new TaskException("Error uploading Xampler results",e);
		} catch (PluggableModuleException e) {
			throw new TaskException("Error uploading Xampler results",e);
		}
		
		logInfo("Results stored to RR.");
		
	}

	private void loadPluggableModules() throws TaskException {
		logInfo("Loading pluggable modules...");
		
		try {
			fileAgentModule = CurrentTaskSingleton.getTaskHandle().getPluggableModule(
					FileAgentPluggableModule.class, "fileagent", "1.0");
			
		} catch (PluggableModuleException e) {
			throw new TaskException("Error loading pluggable module", e);
		} 
	}
	
	@Override
	protected void checkRequiredProperties() throws TaskException {
		checkRequiredProperties(
				new String[] {
						RESULTS_ANALYSIS_NAME,
						RESULTS_DATASET_NAME,
						XAMPLER_REVISION,
						OMNIORB_TIMESTAMP,
						BUILD_NUMBER,
						RUN_NUMBER,
						SUITE_NAME,
						RESULTS_DIR
				} );
		
		resultsAnalysis = getTaskProperty(RESULTS_ANALYSIS_NAME);
		resultsDataset = getTaskProperty(RESULTS_DATASET_NAME);
		
		try {
			xamplerRevision = Long.valueOf(getTaskProperty(XAMPLER_REVISION));
		} catch(NumberFormatException e) {
			throw new TaskException("Error parsing \""+XAMPLER_REVISION+"\" property");
		}
		
		omniorbTimestamp = getTaskProperty(OMNIORB_TIMESTAMP);
		
		try {
			buildNumber = Integer.valueOf(getTaskProperty(BUILD_NUMBER));
		} catch(NumberFormatException e) {
			throw new TaskException("Error parsing \""+BUILD_NUMBER+"\" property");
		}
		
		try {
			runNumber = Integer.valueOf(getTaskProperty(RUN_NUMBER));
		} catch(NumberFormatException e) {
			throw new TaskException("Error parsing \""+RUN_NUMBER+"\" property");
		}
		
		suiteName = getTaskProperty(SUITE_NAME);
		
		resultsDir = getTaskProperty(RESULTS_DIR);
		
		simulate = ( getTaskProperty(SIMULATE) != null);
	}
}
