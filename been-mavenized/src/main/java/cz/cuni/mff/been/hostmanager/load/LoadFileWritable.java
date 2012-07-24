/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager.load;

import java.io.IOException;

import cz.cuni.mff.been.hostmanager.InvalidArgumentException;

import cz.cuni.mff.been.hostmanager.util.MiscUtils;

/**
 * Handle to the load file. This handle allows writing as well as reading of the file.
 *
 * @author Branislav Repcek
 */
class LoadFileWritable extends LoadFile {
	
	private static final long	serialVersionUID	= 2291845620244137925L;

	/**
	 * Create new instance.
	 * 
	 * @param contextId Id of the context.
	 * @param taskId Id of the task.
	 * @param fullPath Full path to the load file.
	 */
	public LoadFileWritable(String contextId, String taskId, String fullPath) {
		
		super(contextId, taskId, fullPath);
	}
	
	/**
	 * Create new writer for the load file which stores samples.
	 * 
	 * @param fileName Name of the file.
	 * 
	 * @return Writer for given file. If file does not exist, empty file will be created.
	 * 
	 * @throws InvalidArgumentException If fileName is invalid or if an error occurred while
	 *         instantiating correct generic.
	 * @throws IOException If an error occurred while creating new file (if fileName does
	 *         no exist).
	 */
	public static LoadFileParser< LoadSample > createSampleWriter(String fileName) 
		throws InvalidArgumentException, IOException {

		LoadFileParser< LoadSample > parser = 
			new LoadFileParser< LoadSample >(fileName, true, LoadSample.class);

		return parser;
	}
	
	/**
	 * Creates new writer for the load file which stores load samples.
	 * 
	 * @param loadFile Handle to the load file to open. If file specified by the handle does not
	 *        exist, new empty file will be created.
	 * 
	 * @return Writer for the requested load file.
	 * 
	 * @throws InvalidArgumentException If load file has been saved on different host than localhost.
	 * @throws IOException If an error occurred while creating new file (if fileName does
	 *         no exist).
	 */
	public static LoadFileParser< LoadSample > createSampleWriter(LoadFile loadFile) 
		throws InvalidArgumentException, IOException {
	
		String currentHost = MiscUtils.getCanonicalLocalhostName();
		
		if (!currentHost.equals(loadFile.hostName)) {
			throw new InvalidArgumentException("Load file comes from different host.");
		}
		
		return createSampleWriter(loadFile.fullPath);
	}
	
	/**
	 * Creates new writer for the load file which stores load monitor events.
	 * 
	 * @param loadFile Handle to the load file to open. If file specified by the handle does not
	 *        exist, new empty file will be created.
	 * 
	 * @return Writer for the requested load file.
	 * 
	 * @throws InvalidArgumentException If load file has been saved on different host than localhost.
	 * @throws IOException If an error occurred while creating new file (if fileName does
	 *         no exist).
	 */
	public static LoadFileParser< LoadMonitorEvent > createEventWriter(LoadFile loadFile)
		throws InvalidArgumentException, IOException {
		
		String currentHost = MiscUtils.getCanonicalLocalhostName();
		
		if (!currentHost.equals(loadFile.hostName)) {
			throw new InvalidArgumentException("Load file comes from different host.");
		}
		
		return createEventWriter(loadFile.fullPath);
	}

	/**
	 * Create new writer for the load file which stores load monitor events.
	 * 
	 * @param fileName Name of the file.
	 * 
	 * @return Writer for given file. If file does not exist, empty file will be created.
	 * 
	 * @throws InvalidArgumentException If fileName is invalid or if an error occurred while
	 *         instantiating correct generic.
	 * @throws IOException If an error occurred while creating new file (if fileName does
	 *         no exist).
	 */
	public static LoadFileParser< LoadMonitorEvent > createEventWriter(String fileName) 
		throws InvalidArgumentException, IOException {

		LoadFileParser< LoadMonitorEvent > parser = 
			new LoadFileParser< LoadMonitorEvent >(fileName, true, LoadMonitorEvent.class);

		return parser;
	}	
}
