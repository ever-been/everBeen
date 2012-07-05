/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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

package cz.cuni.mff.been.pluggablemodule.fileagent.implementation;

import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgent;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgentPluggableModule;

/**
 * Implementation of file agent pluggable module.
 * @author Jan Tattermusch
 */
public class FileAgentPluggableModuleImpl extends PluggableModule implements FileAgentPluggableModule {
	
	/** 
	 * Creates new instance of class.
	 * @param manager pluggable module manager.
	 */
	public FileAgentPluggableModuleImpl(PluggableModuleManager manager) {
		super(manager);
	}

	/**
	 * Creates file agent that manipulates files stored in given dataset in RR.
	 * @param analysisName analysis name to which RR dataset belongs to
	 * @param datasetName name of RR dataset
	 * @param dataFieldName name of dataset's field to which the file content will be stored
	 * @return file agent for manipulating files
	 * @throws PluggableModuleException
	 */
	@Override
	public FileAgent createRRFileAgent(String analysisName, String datasetName, String dataFieldName)
			throws PluggableModuleException {
		
		return new RRFileAgentImpl(analysisName, datasetName, dataFieldName);
		
	}
	
	

    
}
