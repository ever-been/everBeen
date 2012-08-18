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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import cz.cuni.mff.been.common.util.MiscUtils;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;


/**
 * This class represents unique handle to the Load File with detailed mode data on some computer. 
 * Load file is uniquely identified by the name of the host on which data has been sampled, 
 * id of the task that requested detailed mode and id of the context in which that task has been
 * running. 
 *
 * @author Branislav Repcek
 */
public abstract class LoadFile implements Serializable {

	private static final long	serialVersionUID	= -1868906479931084180L;

	/**
	 * Name of the host on which load data have been saved.
	 */
	protected String hostName;
	
	/**
	 * Id of the context.
	 */
	protected String contextId;
	
	/**
	 * Id of the task.
	 */
	protected String taskId;
	
	/**
	 * Full path to the load file.
	 */
	protected String fullPath;
	
	/**
	 * Create new LoadFile handle with hostName set to the canonical name of the localhost.
	 * 
	 * @param contextId Id of the context.
	 * @param taskId Id of the task.
	 * @param fullPath Full path to the load file.
	 */
	protected LoadFile(String contextId, String taskId, String fullPath) {
		
		hostName = MiscUtils.getCanonicalLocalhostName();
		this.contextId = contextId;
		this.taskId = taskId;
		this.fullPath = fullPath;
	}
	
	/**
	 * Creates new parser for the load file which stores load samples. Note that this parser has to
	 * be created on the same computer as detailed mode file.
	 * 
	 * @param loadFile Handle to the load file to open. This handle has to originate from the same
	 *        computer on which this method is called.
	 * 
	 * @return Parser for the requested load file. See {@link LoadFileParserInterface} for more
	 *         details about how to parse load files.
	 * 
	 * @throws FileNotFoundException If load file has not been found.
	 * @throws InvalidArgumentException If load file has been saved on different host than localhost.
	 */
	public static LoadFileParserInterface< LoadSample > createSampleParser(LoadFile loadFile)
		throws FileNotFoundException, InvalidArgumentException {
		
		String currentHost = MiscUtils.getCanonicalLocalhostName();
		
		if (!currentHost.equals(loadFile.hostName)) {
			throw new InvalidArgumentException("Load file comes from different host.");
		}
		
		LoadFileParserInterface< LoadSample > parser = null;
		
		try {
			parser = new LoadFileParser< LoadSample >(loadFile.fullPath, false, LoadSample.class);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Unable to find file for ["
					+ loadFile.hostName + ":"
					+ loadFile.contextId + "/"
					+ loadFile.taskId + "].");
		} catch (IOException e) {
			// this should never happen, since we only read file
		}
		
		return parser;
	}
	
	/**
	 * Creates new parser for the load file which contains load monitor events. Note that this 
	 * parser has to be created on the same computer as detailed mode file.
	 * 
	 * @param loadFile Handle to the load file to open. This handle has to originate from the same
	 *        computer on which this method is called.
	 * 
	 * @return Parser for the requested load file. See {@link LoadFileParserInterface} for more
	 *         details about how to parse load files.
	 * 
	 * @throws FileNotFoundException If load file has not been found.
	 * @throws InvalidArgumentException If load file has been saved on different host than localhost.
	 */
	public static LoadFileParserInterface< LoadMonitorEvent > createEventParser(LoadFile loadFile)
		throws FileNotFoundException, InvalidArgumentException {
		
		String currentHost = MiscUtils.getCanonicalLocalhostName();
		
		if (!currentHost.equals(loadFile.hostName)) {
			throw new InvalidArgumentException("Load file comes from different host.");
		}
		
		LoadFileParserInterface< LoadMonitorEvent > parser = null;
		
		try {
			parser = new LoadFileParser< LoadMonitorEvent >(loadFile.fullPath, false, LoadMonitorEvent.class);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Unable to find file for ["
					+ loadFile.hostName + ":"
					+ loadFile.contextId + "/"
					+ loadFile.taskId + "].");
		} catch (IOException e) {
			// this should never happen, since we only read file
		}
		
		return parser;
	}

	/**
	 * @return Id of the context in which task that requested detailed mode has been running.
	 */
	public String getContextId() {
		
		return contextId;
	}

	/**
	 * @return Canonical name of the host on which task that requested detailed mode has been running.
	 */
	public String getHostName() {
		
		return hostName;
	}

	/**
	 * @return Id of the task that requested detailed mode.
	 */
	public String getTaskId() {
		
		return taskId;
	}
}
