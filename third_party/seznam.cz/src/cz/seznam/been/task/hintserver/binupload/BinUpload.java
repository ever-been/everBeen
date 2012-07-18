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
package cz.seznam.been.task.hintserver.binupload;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgent;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.seznam.been.task.common.FileAgentTask;

/**
 * Task class that uploads compiled binary file to the Results Repository.
 * 
 * @author Jiri Tauber
 */
public class BinUpload extends FileAgentTask {

	//-----------------------------------------------------------------------//
	// Task property names:
	/** The name of the property that tells the binary file name */
	private static final String PROP_BIN_FILENAME = "bin.filename";
	/** The name of the property which tells the compiled revision number */
	private static final String PROP_VERSION = "version";
	/** The name of the property which rells the analysis name */
	private static final String PROP_ANALYSIS = "analysis";

	//-----------------------------------------------------------------------//

	/** The name of the dateset where compiled binaries are stored */
	private static final String DATASET_BINARY = "binary";  // copy from generator
	/** A binary dataset tag */
	public static final String TAG_BINARY_BINARY = "bin_file";  // copy from generator
	/** A binary dataset tag */
	public static final String TAG_BINARY_VERSION = "version";  // copy from generator
	/** A binary dataset tag */
	public static final String TAG_BINARY_BUILDTIME = "build_time";  // copy from generator
	/** A binary dataset tag */
	public static final String TAG_BINARY_RUN_COUNT = "run_count";  // copy from generator

	//-----------------------------------------------------------------------//

	public BinUpload() throws TaskInitializationException {
		super();
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#checkRequiredProperties()
	 */
	@Override
	protected void checkRequiredProperties() throws TaskException {
		StringBuilder err = new StringBuilder();
		String propName;
		String propValue;

		propName = PROP_BIN_FILENAME;
		propValue = getTaskProperty(propName); 
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

		propName = PROP_VERSION;
		propValue = getTaskProperty(propName); 
		if (propValue == null || propValue.isEmpty()) {
			err.append("Property "+propName+" is missing;");
		}

		propName = PROP_ANALYSIS;
		propValue = getTaskProperty(propName); 
		if (propValue == null || propValue.isEmpty()) {
			err.append("Property "+propName+" is missing;");
		}

		if (err.length() > 0) {
			throw new TaskException(err.toString());
		}
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	protected void run() throws TaskException {
		File file = new File(getTaskProperty(PROP_BIN_FILENAME));
		HashMap<String, Serializable> tags = new HashMap<String, Serializable>();

		tags.put(TAG_BINARY_VERSION, getTaskProperty(PROP_VERSION));
		tags.put(TAG_BINARY_BUILDTIME, file.lastModified());
		tags.put(TAG_BINARY_RUN_COUNT, 0);
		try {
			FileAgent agent = getFileAgent().createRRFileAgent(
					getTaskProperty(PROP_ANALYSIS),
					DATASET_BINARY,
					TAG_BINARY_BINARY);
			agent.storeFile(file, tags);
			
		} catch (PluggableModuleException e) {
			throw new TaskException("Error creating File Agent", e);
		} catch (IOException e) {
			throw new TaskException(e);
		}
	}
}
