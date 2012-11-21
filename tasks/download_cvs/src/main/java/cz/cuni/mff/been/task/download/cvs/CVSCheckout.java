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
package cz.cuni.mff.been.task.download.cvs;

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
import cz.cuni.mff.been.utils.TarUtils;
import cz.cuni.mff.d3s.been.cvs.Cvs;

public class CVSCheckout extends Job {

	public CVSCheckout() throws TaskInitializationException {
		super();
	}

	/** Property for remote CVS repository */
	public static final String CVS_REPOSITORY = "cvs.repository";
	/** Property for CVS module */
	public static final String CVS_MODULE = "cvs.module";
	/** Property for CVS password */
	public static final String CVS_PASSWORD = "cvs.password";
	/** Property for CVS revision (tag/revision) */
	public static final String CVS_BRANCH = "cvs.branch";

	/** Version timestamp */
	public static final String TIMESTAMP = "timestamp";

	/** Name of analysis of target dataset */
	public static final String RR_ANALYSIS_NAME = "rr.analysis";

	/** Target dataset to which file reference will be saved */
	public static final String RR_DATASET_NAME = "rr.dataset";

	/** If set, task will be run in dry mode (not downloading anything) */
	public static final String SIMULATE = "simulate";

	/** CVS_REPOSITORY property value */
	private String cvsRepository;

	/** CVS_BRANCH property value */
	private String cvsBranch;

	/** CVS_MODULE property value */
	private String cvsModule;

	/** CVS_PASSWORD property value */
	private String cvsPassword;

	/** RR_ANALYSIS_NAME property value */
	private String analysisName;

	/** RR_DATASET_NAME property value */
	private String datasetName;

	/** TIMESTAMP property value (YYYY-MM-dd HH:mm:ss in UTC) */
	private String timestamp;

	/** file agent pluggable module */
	private FileAgentPluggableModule fileAgentModule;

	private boolean simulate;

	/** name of resulting archive */
	private static final String SOURCE_ARCHIVE_NAME = "source-code.tar.gz";

	/** dataset's field for storing timestamp */
	private static final String TIMESTAMP_FIELD_NAME = "timestamp";

	/** dataset's field for storing file id */
	private static final String FILE_ID_FIELD_NAME = "fileid";

	@Override
	protected void run() throws TaskException {
		loadPluggableModules();

		/* files */
		File omniorbSrc = new File(getTempDirectory(), cvsModule);
		File omniorbArchive = new File(getWorkingDirectory(), SOURCE_ARCHIVE_NAME);

		/* do CVS checkout */
		try {
			String checkoutDate = timestamp + " GMT";

			logInfo("Checking out version from " + checkoutDate);

			if (!simulate) {
				/* download from cvs */
				Cvs.checkout(cvsRepository, cvsPassword, null, cvsModule, cvsBranch, checkoutDate, getTempDirectory());
			} else {
				/* we are simulating, just create empty module directory */
				File moduleDir = new File(getTempDirectory(), cvsModule);
				moduleDir.mkdirs();
			}

		} catch (IOException e) {
			throw new TaskException("Can't checkout sources", e);
		}

		logInfo("Tarring sources.");
		/* tar downloaded files */
		try {
			TarUtils.compress(omniorbSrc, omniorbArchive);
		} catch (IOException e) {
			throw new TaskException("Can't create source archive", e);
		}

		logInfo("Saving sources to RR.");

		/* save tarred sources to RR */
		try {

			FileAgent agent = fileAgentModule.createRRFileAgent(analysisName, datasetName, FILE_ID_FIELD_NAME);

			HashMap<String, Serializable> tags = new HashMap<String, Serializable>();
			tags.put(TIMESTAMP_FIELD_NAME, timestamp);

			agent.storeFile(omniorbArchive, tags);
		} catch (IOException e) {
			throw new TaskException("Error storing sources", e);
		} catch (PluggableModuleException e) {
			throw new TaskException("Error storing sources", e);
		}
	}

	@Override
	protected void checkRequiredProperties() throws TaskException {

		checkRequiredProperties(new String[] {
				//CVS_REPOSITORY,
				CVS_MODULE, CVS_BRANCH, TIMESTAMP, RR_ANALYSIS_NAME, RR_DATASET_NAME });

		cvsRepository = getTaskProperty(CVS_REPOSITORY);
		if (cvsRepository == null) {
			cvsRepository = ":pserver:anonymous@omniorb.cvs.sourceforge.net:/cvsroot/omniorb";
			logInfo("Using default CVS repository");
		}

		cvsModule = getTaskProperty(CVS_MODULE);
		cvsBranch = getTaskProperty(CVS_BRANCH);
		cvsPassword = getTaskProperty(CVS_PASSWORD);

		analysisName = getTaskProperty(RR_ANALYSIS_NAME);
		datasetName = getTaskProperty(RR_DATASET_NAME);

		timestamp = getTaskProperty(TIMESTAMP);

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
