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
package cz.cuni.mff.been.task.xampler.deploy;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import cz.cuni.mff.been.common.anttasks.AntTaskException;
import cz.cuni.mff.been.common.anttasks.Untar;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgent;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgentPluggableModule;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

public class XamplerDeploy extends Job {
    
    /** Name of analysis of dataset containing omniorb binary */
    public static final String RR_OMNIORB_ANALYSIS_NAME = "rr.omniorb.analysis";
    
    /** Dataset from which to load omniorb */
    public static final String RR_OMNIORB_DATASET_NAME = "rr.omniorb.dataset";
    
    /** Name of analysis of dataset containing xampler binary */
    public static final String RR_XAMPLER_ANALYSIS_NAME = "rr.xampler.analysis";
    
    /** Dataset from which to load xampler */
    public static final String RR_XAMPLER_DATASET_NAME = "rr.xampler.dataset";
    
    /** Omniorb version timestamp */
    public static final String OMNIORB_TIMESTAMP = "omniorb.timestamp";
    
    /** Xampler version revision */
    public static final String XAMPLER_REVISION = "xampler.revision";
    
    /** Build number */
    public static final String BUILD_NUMBER = "build.number";
    
    /** Directory to which to deploy xampler */
    public static final String OMNIORB_DIR = "omniorb.dir";
    
    /** Directory to which to deploy xampler */
    public static final String XAMPLER_DIR = "xampler.dir";
    
    /** Simulate property doesn't have any effect on this task. */
    public static final String SIMULATE = "simulate";
    
    /** RR_OMNIORB_ANALYSIS_NAME property value */
    private String omniorbAnalysis;
    
    /** RR_OMNIORB_DATASET_NAME property value */
    private String omniorbDataset;
    
    /** RR_XAMPLER_ANALYSIS_NAME property value */
    private String xamplerAnalysis;
    
    /** RR_XAMPLER_DATASET_NAME property value */
    private String xamplerDataset;
    
    /** OMNIORB_TIMESTAMP property value */
    private String omniorbTimestamp;
    
    /** XAMPLER_REVISION property value */
    private Long xamplerRevision;
    
    /** BUILD_NUMBER property value */
    private Integer buildNumber;
    
    /** OMNIORB_DIR property value */
    private String omniorbDir;
    
    /** XAMPLER_DIR property value */
    private String xamplerDir;

    
	/** file agent pluggable module */
	private FileAgentPluggableModule fileAgentModule;

	private boolean simulate;
	
	/** dataset's field for storing timestamp */
    //private static final String TIMESTAMP_FIELD_NAME = "timestamp";
    
    /** dataset's field for storing build_number */
    private static final String BUILD_NUMBER_FIELD_NAME = "build_number";
    
    /** dataset's field for storing omniorb's timestamp */
    private static final String OMNIORB_TIMESTAMP_FIELD_NAME = "omniorb_timestamp";
    
    /** dataset's field for storing xampler's revision */
    private static final String XAMPLER_REVISION_FIELD_NAME = "xampler_revision";
    
    /** dataset's field for storing file id */
    private static final String FILE_ID_FIELD_NAME = "fileid";
    
	
	public XamplerDeploy() throws TaskInitializationException {
		super();
	}
	
	@Override
	protected void run() throws TaskException {
		/* load pluggable modules */
		loadPluggableModules();
		
		if(simulate) {
			/* SIMULATE and NORMAL mode do the same work for this job*/
		}
		
		/* files */
		File omniorbArchive = new File(getTempDirectory(),"omniorb.tar.gz");
		File xamplerArchive = new File(getTempDirectory(),"xampler.tar.gz");
		File omniorbRoot = new File(getWorkingDirectory(),omniorbDir);
		File xamplerRoot = new File(getWorkingDirectory(),xamplerDir);
		
		/* download omniORB from RR */
		try {
			FileAgent agent = fileAgentModule.createRRFileAgent(omniorbAnalysis, omniorbDataset, FILE_ID_FIELD_NAME);
			
			HashMap<String, Serializable> tags = new HashMap<String, Serializable>();
			tags.put(OMNIORB_TIMESTAMP_FIELD_NAME, omniorbTimestamp);
			tags.put(BUILD_NUMBER_FIELD_NAME, buildNumber);
			agent.loadFile(omniorbArchive, tags);
		} catch (IOException e) {
			throw new TaskException("Error downloading omniORB",e);
		} catch (PluggableModuleException e) {
			throw new TaskException("Error downloading omniORB",e);
		}
		
		logInfo("omniORB downloaded from RR.");
		
		/* download Xampler from RR */
		try {
			FileAgent agent = fileAgentModule.createRRFileAgent(xamplerAnalysis, xamplerDataset, FILE_ID_FIELD_NAME);
			
			HashMap<String, Serializable> tags = new HashMap<String, Serializable>();
			tags.put(OMNIORB_TIMESTAMP_FIELD_NAME, omniorbTimestamp);
			tags.put(BUILD_NUMBER_FIELD_NAME, buildNumber);
			tags.put(XAMPLER_REVISION_FIELD_NAME, xamplerRevision);
			agent.loadFile(xamplerArchive, tags);
		} catch (IOException e) {
			throw new TaskException("Error downloading Xampler",e);
		} catch (PluggableModuleException e) {
			throw new TaskException("Error downloading Xampler",e);
		}
		
		logInfo("Xampler downloaded from RR.");
	
		try {
			Untar.untar(omniorbArchive.getAbsolutePath(), omniorbRoot.getAbsolutePath(), "gzip");
			Untar.untar(xamplerArchive.getAbsolutePath(), xamplerRoot.getAbsolutePath(), "gzip");
		} catch (AntTaskException e) {
			throw new TaskException("Error decompressing source archives", e);
		}
		
		logInfo("omniORB and Xampler deployed successfully.");
		
	}

	private void loadPluggableModules() throws TaskException {
		logInfo("Loading pluggable modules...");
		
		try {
			fileAgentModule = Task.getTaskHandle().getPluggableModule(
					FileAgentPluggableModule.class, "fileagent", "1.0");
			
		} catch (PluggableModuleException e) {
			throw new TaskException("Error loading pluggable module", e);
		} 
	}
	
	@Override
	protected void checkRequiredProperties() throws TaskException {
		checkRequiredProperties(
				new String[] {
						RR_OMNIORB_ANALYSIS_NAME,
						RR_OMNIORB_DATASET_NAME,
						RR_XAMPLER_ANALYSIS_NAME,
						RR_XAMPLER_DATASET_NAME,
						OMNIORB_TIMESTAMP,
						XAMPLER_REVISION,
						BUILD_NUMBER,
						OMNIORB_DIR,
						XAMPLER_DIR
				} );
		
		omniorbAnalysis = getTaskProperty(RR_OMNIORB_ANALYSIS_NAME);
		omniorbDataset = getTaskProperty(RR_OMNIORB_DATASET_NAME);
		xamplerAnalysis = getTaskProperty(RR_XAMPLER_ANALYSIS_NAME);
		xamplerDataset = getTaskProperty(RR_XAMPLER_DATASET_NAME);
		
		omniorbDir = getTaskProperty(OMNIORB_DIR);
		xamplerDir = getTaskProperty(XAMPLER_DIR);
		
		omniorbTimestamp = getTaskProperty(OMNIORB_TIMESTAMP);
		
		try {
			buildNumber = Integer.valueOf(getTaskProperty(BUILD_NUMBER));
		} catch(NumberFormatException e) {
			throw new TaskException("Error parsing \""+BUILD_NUMBER+"\" property");
		}
		
		try {
			xamplerRevision = Long.valueOf(getTaskProperty(XAMPLER_REVISION));
		} catch(NumberFormatException e) {
			throw new TaskException("Error parsing \""+XAMPLER_REVISION+"\" property");
		}
		
		simulate = ( getTaskProperty(SIMULATE) != null);
	}
}
