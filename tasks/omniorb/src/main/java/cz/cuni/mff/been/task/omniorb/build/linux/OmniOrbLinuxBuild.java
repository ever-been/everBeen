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
package cz.cuni.mff.been.task.omniorb.build.linux;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import cz.cuni.mff.been.common.anttasks.AntTaskException;
import cz.cuni.mff.been.common.anttasks.Tar;
import cz.cuni.mff.been.common.anttasks.Untar;
import cz.cuni.mff.been.common.scripting.ScriptEnvironment;
import cz.cuni.mff.been.common.scripting.ScriptException;
import cz.cuni.mff.been.common.scripting.ScriptLauncher;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgent;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgentPluggableModule;
import cz.cuni.mff.been.task.*;

public class OmniOrbLinuxBuild extends Job {
	
	/** Source version timestamp */
    public static final String TIMESTAMP = "timestamp";
    
    /** Build number */
    public static final String BUILD_NUMBER = "build.number";
    
    /** Name of analysis of source dataset */
    public static final String RR_SRC_ANALYSIS_NAME = "rr.src.analysis";
    
    /** Sources dataset to which file reference will be saved */
    public static final String RR_SRC_DATASET_NAME = "rr.src.dataset";
    
    /** Name of analysis of dataset containing binaries */
    public static final String RR_BIN_ANALYSIS_NAME = "rr.bin.analysis";
    
    /** Binaries dataset to which file reference will be saved */
    public static final String RR_BIN_DATASET_NAME = "rr.bin.dataset";
    
    /** If set, task will be run in dry mode (not building anything) */
    public static final String SIMULATE = "simulate";
    
    /** If set, omniorb sources patch script will be run before build starts */
    public static final String PATCH = "patch";
    
    /** Location of diff file that is used by omniORB patch script */
    public static final String OMNIORB_PATCH_FILENAME = "omniorb_patch.diff";
    
    
	/** TIMESTAMP property value */
    private String timestamp;
    
    /** BUILD_NUMBER property value */
    private Integer buildNumber;
    
    /** RR_SRC_ANALYSIS_NAME property value */
    private String srcAnalysis;
    
    /** RR_SRC_DATASET_NAME property value */
    private String srcDataset;
    
    /** RR_BIN_ANALYSIS_NAME property value */
    private String binAnalysis;
    
    /** RR_BIN_DATASET_NAME property value */
    private String binDataset;

	/** file agent pluggable module */
	private FileAgentPluggableModule fileAgentModule;

	/** simulated run flag */
	private boolean simulate;
	
	/** try patching omniORB sources flag */
	private boolean patch;
	
	/** dataset's field for storing timestamp */
    private static final String SOURCE_TIMESTAMP_FIELD_NAME = "timestamp";
    
    /** dataset's field for storing timestamp */
    private static final String BINARY_TIMESTAMP_FIELD_NAME = "omniorb_timestamp";
    
    /** dataset's field for storing timestamp */
    private static final String BUILD_NUMBER_FIELD_NAME = "build_number";
    
    /** dataset's field for storing file id */
    private static final String FILE_ID_FIELD_NAME = "fileid";
    
	
	public OmniOrbLinuxBuild() throws TaskInitializationException {
		super();
	}
	
	@Override
	protected void run() throws TaskException {
		/* load pluggable modules */
		loadPluggableModules();
		
		/* files */
		File srcArchive = new File(getTempDirectory(),"source.tar.gz");
		File binArchive = new File(getWorkingDirectory(),"binary.tar.gz");
		File sourceRoot = new File(getTempDirectory(),"omni-src");
		File binaryRoot = new File(getWorkingDirectory(),"omniorb");
		
		/* load tarred sources from RR */
		try {
			FileAgent agent = fileAgentModule.createRRFileAgent(srcAnalysis, srcDataset, FILE_ID_FIELD_NAME);
			
			HashMap<String, Serializable> tags = new HashMap<String, Serializable>();
			tags.put(SOURCE_TIMESTAMP_FIELD_NAME, timestamp);
			agent.loadFile(srcArchive, tags);
		} catch (IOException e) {
			throw new TaskException("Error loading sources",e);
		} catch (PluggableModuleException e) {
			throw new TaskException("Error loading sources",e);
		}
		
		logInfo("Sources loaded from RR.");
	
		try {
			Untar.untar(srcArchive.getAbsolutePath(), sourceRoot.getAbsolutePath(), "gzip");
		} catch (AntTaskException e) {
			throw new TaskException("Error decompressing source archive", e);
		}
		
		boolean buildFailed = false;
		
		if (!simulate) {
						
			if(patch) {
				File omniorbPatch = new File(getTaskDirectory(), OMNIORB_PATCH_FILENAME );
				
				try {
					ScriptLauncher launcher = new ScriptLauncher();
					
					ScriptEnvironment env = new ScriptEnvironment();
					env.setDirectory(sourceRoot);
					env.putEnv("OMNIORB_PATCH_FILE", omniorbPatch.getAbsolutePath());

					String[] script = new String[] {
							"#!/bin/bash",
							"patch -N -p1 -i $OMNIORB_PATCH_FILE",
							"if [ \"${?}\" -ne \"0\" ]; then echo 'Patch already applied or error when patching' >&2; exit 1; fi",

							"echo 'omniORB sources successfully patched.' >&2"
					};

					logInfo("Running omniORB patch script.");
					int result = launcher.runShellScript(script, env);

					if (result != 0) {
						logInfo("omniORB patch ended with non-zero errorcode. This can mean patch is either not needed or an error occured. We will see whether omniORB build succeeds.");
					} else {
						logInfo("omniORB sources were successfully patched");
					}
				} catch (ScriptException e) {
					throw new TaskException("Error patching omniORB sources", e);
				}
			}
			
			try {
				ScriptEnvironment env = new ScriptEnvironment();
				env.setDirectory(sourceRoot);
				env.putEnv("PREFIX", binaryRoot.getAbsolutePath());
				env.putEnv("SRC_ARCHIVE", srcArchive.getAbsolutePath());

				ScriptLauncher launcher = new ScriptLauncher();

				String[] script = new String[] {
						"#!/bin/bash",
						
						"chmod -R 'ugo+rx' bin",
						"if [ \"${?}\" -ne \"0\" ]; then echo 'Error chmod \"bin\" directory' >&2; exit 1; fi",

						"echo 'Running configure...' >&2",
						"chmod 'ugo+rx' configure",
						"if [ \"${?}\" -ne \"0\" ]; then echo 'Error running ./configure' >&2; exit 1; fi",

						"./configure --prefix=\"$PREFIX\" --with-omniORB-config=omniORB.cfg --with-omniNames-logdir=log",
						"if [ \"${?}\" -ne \"0\" ]; then echo 'Error running ./configure' >&2; exit 1; fi",


						"echo 'Running make...' >&2",
						"make ",
						"if [ \"${?}\" -ne \"0\" ]; then echo 'Error running \"make\"' >&2; exit 1; fi",

						"echo 'Running make install...' >&2",
						"make install",
						"if [ \"${?}\" -ne \"0\" ]; then echo 'Error running \"make install\"' >&2; exit 1; fi",

						"echo 'omniORB built successfully' >&2"
				};	

				logInfo("Running build script.");
				int result = launcher.runShellScript(script, env);

				if (result != 0) {
					buildFailed = true;
					logFatal("Error occured when running build script. See stderr.");
				}	
			} catch (ScriptException e) {
				buildFailed = true;
				e.printStackTrace();
				logFatal("Error running build script:" + e.getMessage());
			} 

			if (!buildFailed) logInfo("omniORB sucessfully built.");
		} else {
			binaryRoot.mkdirs();
		}
		
		/* tar binary */
		
		if (!buildFailed) {
			try {
				Tar.tar(binaryRoot.getAbsolutePath(),binArchive.getAbsolutePath(),"gzip");
			} catch (AntTaskException e) {
				throw new TaskException("Can't create binary archive",e);
			}
			logInfo("Binary tarred.");
		}
		
		/* save tarred binary to RR */
		try {			
			
			FileAgent agent = fileAgentModule.createRRFileAgent(binAnalysis, binDataset, FILE_ID_FIELD_NAME);
			
			HashMap<String, Serializable> tags = new HashMap<String, Serializable>();
			tags.put(BINARY_TIMESTAMP_FIELD_NAME, timestamp);
			tags.put(BUILD_NUMBER_FIELD_NAME, buildNumber);
			
			if (!buildFailed) {
				agent.storeFile(binArchive, tags);
			} else {
				logInfo("Build script failed so null binary file reference will be stored to RR.");
				agent.storeFile(null, tags);
				
			}
		} catch (IOException e) {
			throw new TaskException("Error storing binary",e);
		} catch (PluggableModuleException e) {
			throw new TaskException("Error storing binary",e);
		} 
		
		if (buildFailed) {
			throw new TaskException("Error occured during build process.");
		}
		
		logInfo("Binary stored to RR.");
		
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
						TIMESTAMP,
						BUILD_NUMBER,
						RR_SRC_ANALYSIS_NAME,
						RR_SRC_DATASET_NAME,
						RR_BIN_ANALYSIS_NAME,
						RR_BIN_DATASET_NAME,
				} );
		
		timestamp = getTaskProperty(TIMESTAMP);
		
		try {
			buildNumber = Integer.valueOf(getTaskProperty(BUILD_NUMBER));
		} catch(NumberFormatException e) {
			throw new TaskException("Error parsing \""+BUILD_NUMBER+"\" property");
		}
		
		srcAnalysis = getTaskProperty(RR_SRC_ANALYSIS_NAME);
		srcDataset = getTaskProperty(RR_SRC_DATASET_NAME);
		binAnalysis = getTaskProperty(RR_BIN_ANALYSIS_NAME);
		binDataset = getTaskProperty(RR_BIN_DATASET_NAME);
		
		simulate = ( getTaskProperty(SIMULATE) != null);
		
		patch = ( getTaskProperty(PATCH) != null);
	}
}
