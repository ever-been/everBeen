/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Michal Tomcanyi
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
package cz.cuni.mff.been.task.xampler.build.linux;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import cz.cuni.mff.been.common.scripting.ScriptEnvironment;
import cz.cuni.mff.been.common.scripting.ScriptException;
import cz.cuni.mff.been.common.scripting.ScriptLauncher;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgent;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgentPluggableModule;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.cuni.mff.been.utils.FileUtils;
import cz.cuni.mff.been.utils.TarUtils;

public class XamplerLinuxBuild extends Job {

	/** Name of analysis of source dataset */
	public static final String RR_SRC_ANALYSIS_NAME = "rr.src.analysis";

	/** Sources dataset to which file reference will be saved */
	public static final String RR_SRC_DATASET_NAME = "rr.src.dataset";

	/** Where to get omniorb - RR analysis */
	public static final String RR_OMNIORB_ANALYSIS_NAME = "rr.omniorb.analysis";

	/** Where to get omniorb - RR dataset */
	public static final String RR_OMNIORB_DATASET_NAME = "rr.omniorb.dataset";

	/** Name of analysis of dataset containing binaries */
	public static final String RR_BIN_ANALYSIS_NAME = "rr.bin.analysis";

	/** Binaries dataset to which file reference will be saved */
	public static final String RR_BIN_DATASET_NAME = "rr.bin.dataset";

	/** Suite subdirectory to build */
	public static final String XAMPLER_SUITE_SUBDIR = "xampler.suite.subdir";

	/** timestamp of omniorb binary to use */
	public static final String OMNIORB_TIMESTAMP = "omniorb.timestamp";

	/** revision of xampler source to use */
	public static final String XAMPLER_REVISION = "xampler.revision";

	/** build number */
	public static final String BUILD_NUMBER = "build.number";

	/** If set, task will be run in dry mode (not building anything) */
	public static final String SIMULATE = "simulate";

	/** value of RR_SRC_ANALYSIS_NAME property */
	private String srcAnalysis;

	/** value of RR_SRC_DATASET_NAME property */
	private String srcDataset;

	/** value of RR_BIN_ANALYSIS_NAME property */
	private String binAnalysis;

	/** value of RR_BIN_DATASET_NAME property */
	private String binDataset;

	/** value of RR_OMNIORB_ANALYSIS_NAME property */
	private String omniorbAnalysis;

	/** value of RR_OMNIORB_DATASET_NAME property */
	private String omniorbDataset;

	/** value of XAMPLER_SUITE_SUBDIR property */
	private String xamplerSuiteSubdir;

	/** value of OMNIORB_TIMESTAMP property */
	private String omniorbTimestamp;

	/** value of XAMPLER_REVISION property */
	private Long xamplerRevision;

	/** value of BUILD_NUMBER property */
	private Integer buildNumber;

	private FileAgentPluggableModule fileAgentModule;

	private boolean simulate;

	/** dataset's field for storing revision */
	private static final String SOURCE_REVISION_FIELD_NAME = "revision";

	/** dataset's field for storing omniorb's timestamp */
	private static final String OMNIORB_TIMESTAMP_FIELD_NAME = "omniorb_timestamp";

	/** dataset's field for storing xampler's revision */
	private static final String XAMPLER_REVISION_FIELD_NAME = "xampler_revision";

	/** dataset's field for storing build number */
	private static final String BUILD_NUMBER_FIELD_NAME = "build_number";

	/** dataset's field for storing file id */
	private static final String FILE_ID_FIELD_NAME = "fileid";

	public XamplerLinuxBuild() throws TaskInitializationException {
		super();
	}

	@Override
	protected void run() throws TaskException {

		/* load pluggable modules */
		loadPluggableModules();

		/* files */
		File omniorbRoot = new File(getTempDirectory(), "omniorb-bin");
		File xamplerSrcRoot = new File(getTempDirectory(), "xampler-src");
		File xamplerBinRoot = new File(getWorkingDirectory(), "xampler");
		File omniorbArchive = new File(getTempDirectory(), "omniorb-src.tar.gz");
		//File omniorbArchive = new File("/home/johny/Desktop/been_data/omniorb-binary.tar.gz");
		File xamplerSrcArchive = new File(getTempDirectory(), "xampler-src.tar.gz");
		//File xamplerSrcArchive = new File("/home/johny/Desktop/been_data/xampler-source.tar.gz");
		File xamplerBinArchive = new File(getWorkingDirectory(), "xampler-bin.tar.gz");

		/* download omniorb binary from RR */
		try {
			FileAgent agent = fileAgentModule.createRRFileAgent(omniorbAnalysis, omniorbDataset, FILE_ID_FIELD_NAME);

			HashMap<String, Serializable> tags = new HashMap<String, Serializable>();
			tags.put(OMNIORB_TIMESTAMP_FIELD_NAME, omniorbTimestamp);
			tags.put(BUILD_NUMBER_FIELD_NAME, buildNumber);
			agent.loadFile(omniorbArchive, tags);
		} catch (IOException e) {
			throw new TaskException("Error loading omniORB binary", e);
		} catch (PluggableModuleException e) {
			throw new TaskException("Error loading omniORB binary", e);
		}
		logInfo("omniORB binary loaded from RR.");

		/* download xampler sources from RR */
		try {
			FileAgent agent = fileAgentModule.createRRFileAgent(srcAnalysis, srcDataset, FILE_ID_FIELD_NAME);

			HashMap<String, Serializable> tags = new HashMap<String, Serializable>();
			tags.put(SOURCE_REVISION_FIELD_NAME, xamplerRevision);
			agent.loadFile(xamplerSrcArchive, tags);
		} catch (IOException e) {
			throw new TaskException("Error loading Xampler sources", e);
		} catch (PluggableModuleException e) {
			throw new TaskException("Error loading Xampler sources", e);
		}
		logInfo("Xampler sources loaded from RR.");

		/* untar omniorb binary */
		try {
			TarUtils.extract(omniorbArchive, omniorbRoot);
		} catch (IOException e) {
			throw new TaskException("Error decompressing omniORB binary", e);
		}

		/* untar xampler sources */
		try {
			TarUtils.extract(xamplerSrcArchive, xamplerSrcRoot);
		} catch (IOException e) {
			throw new TaskException("Error decompressing Xampler sources", e);
		}

		boolean buildFailed = false;

		if (!simulate) {
			/* build xampler */
			try {
				String pythonLibs = new File(omniorbRoot, "lib/python2.4/site-packages").getAbsolutePath();
				String platform = xamplerSuiteSubdir;

				ScriptEnvironment env = new ScriptEnvironment();
				env.setDirectory(xamplerSrcRoot);
				env.putEnv("OMNIORB_DIR", omniorbRoot.getAbsolutePath());
				env.putEnv("PYTHONPATH", pythonLibs);
				env.putEnv("PLATFORM", platform);
				env.putEnv("DEST_DIR", xamplerBinRoot.getAbsolutePath());

				String[] script = new String[] {
						"#!/bin/bash",

						"chmod -R 'ugo+rx' $OMNIORB_DIR/bin",
						"if [ \"${?}\" -ne \"0\" ]; then echo 'Error chmod omniORB \"bin\" directory' >&2; exit 1; fi",

						"cd C++/_Suites",
						"if [ \"${?}\" -ne \"0\" ]; then echo 'Error entering C++/_Suite directory.' >&2; exit 1; fi",

						"for SUITE in *",
						"do",
						"	cd $SUITE/$PLATFORM",
						"	if [ \"${?}\" -ne \"0\" ]; then echo 'Error entering suite directory.' >&2; exit 1; fi",

						"	make RAW=YES CORBA=FULL LOCATOR=FILE",
						"	if [ \"${?}\" -ne \"0\" ]; then echo 'Error running \"make\"' >&2; exit 1; fi",

						"	cd ../..", "	echo \"Suite $SUITE built successfully.\" >&2",
						"done",

						"echo 'Xampler built successfully' >&2" };

				ScriptLauncher launcher = new ScriptLauncher();

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
			if (!buildFailed)
				logInfo("Build script returned successfully.");

			try {
				// TODO verify equivalence:
				//Move.filesetMove(xamplerSrcRoot.getAbsolutePath(), "**/Server", xamplerBinRoot.getAbsolutePath());
				for (Iterator<File> folderMatch = FileUtils.findFilesRecursivelyByName(xamplerSrcRoot, "Server").iterator(); folderMatch.hasNext();) {
					FileUtils.moveDirectoryToDirectory(folderMatch.next(), xamplerBinRoot, false);
				}
				// TODO verify equivalence:
				//Move.filesetMove(xamplerSrcRoot.getAbsolutePath(), "**/Client", xamplerBinRoot.getAbsolutePath());
				for (Iterator<File> folderMatch = FileUtils.findFilesRecursivelyByName(xamplerSrcRoot, "Client").iterator(); folderMatch.hasNext();) {
					FileUtils.moveDirectoryToDirectory(folderMatch.next(), xamplerBinRoot, false);
				}
			} catch (IOException e) {
				throw new TaskException("Collecting Xampler binaries failed.", e);
			}

			logInfo("Xampler binaries collected.");

		} else {
			/* don't build anything, just make destination directory */
			xamplerBinRoot.mkdirs();
		}

		if (!buildFailed) {
			try {
				TarUtils.compress(xamplerBinRoot, xamplerBinArchive);
			} catch (IOException e) {
				throw new TaskException("Can't create binary archive", e);
			}
			logInfo("Xampler binaries tarred.");
		}

		/* save tarred binary to RR */
		try {

			FileAgent agent = fileAgentModule.createRRFileAgent(binAnalysis, binDataset, FILE_ID_FIELD_NAME);

			HashMap<String, Serializable> tags = new HashMap<String, Serializable>();

			tags.put(OMNIORB_TIMESTAMP_FIELD_NAME, omniorbTimestamp);
			tags.put(BUILD_NUMBER_FIELD_NAME, buildNumber);
			tags.put(XAMPLER_REVISION_FIELD_NAME, xamplerRevision);

			if (!buildFailed) {
				agent.storeFile(xamplerBinArchive, tags);
			} else {
				logInfo("Build script failed so null binary file reference will be stored to RR.");
				agent.storeFile(null, tags);

			}
		} catch (IOException e) {
			throw new TaskException("Error storing binary", e);
		} catch (PluggableModuleException e) {
			throw new TaskException("Error storing binary", e);
		}

		if (buildFailed) {
			throw new TaskException("Error occured during build process.");
		}

		logInfo("Binary stored to RR.");
	}
	@Override
	protected void checkRequiredProperties() throws TaskException {
		checkRequiredProperties(new String[] { RR_SRC_ANALYSIS_NAME,
				RR_SRC_DATASET_NAME, RR_BIN_ANALYSIS_NAME, RR_BIN_DATASET_NAME,
				RR_OMNIORB_ANALYSIS_NAME, RR_OMNIORB_DATASET_NAME, XAMPLER_REVISION,
				OMNIORB_TIMESTAMP, XAMPLER_SUITE_SUBDIR, BUILD_NUMBER });

		srcAnalysis = getTaskProperty(RR_SRC_ANALYSIS_NAME);
		srcDataset = getTaskProperty(RR_SRC_DATASET_NAME);
		binAnalysis = getTaskProperty(RR_BIN_ANALYSIS_NAME);
		binDataset = getTaskProperty(RR_BIN_DATASET_NAME);
		omniorbAnalysis = getTaskProperty(RR_OMNIORB_ANALYSIS_NAME);
		omniorbDataset = getTaskProperty(RR_OMNIORB_DATASET_NAME);

		xamplerSuiteSubdir = getTaskProperty(XAMPLER_SUITE_SUBDIR);

		omniorbTimestamp = getTaskProperty(OMNIORB_TIMESTAMP);

		try {
			buildNumber = Integer.valueOf(getTaskProperty(BUILD_NUMBER));
		} catch (NumberFormatException e) {
			throw new TaskException("Error parsing \"" + BUILD_NUMBER + "\" property");
		}

		try {
			xamplerRevision = Long.valueOf(getTaskProperty(XAMPLER_REVISION));
		} catch (NumberFormatException e) {
			throw new TaskException("Error parsing \"" + XAMPLER_REVISION + "\" property");
		}

		simulate = (getTaskProperty(SIMULATE) != null);
	}

	private void loadPluggableModules() throws TaskException {
		logInfo("Loading pluggable modules...");

		try {
			fileAgentModule = CurrentTaskSingleton.getTaskHandle().getPluggableModule(FileAgentPluggableModule.class, "fileagent", "1.0");

		} catch (PluggableModuleException e) {
			throw new TaskException("Error loading pluggable module", e);
		}
	}
}
