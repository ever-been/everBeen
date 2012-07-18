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
package cz.cuni.mff.been.pluggablemodule.derby.client.implementation;

import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.client.DerbyClient;
import cz.cuni.mff.been.pluggablemodule.derby.client.DerbyClientPluggableModule;


/**
 * BEEN's pluggable module which provides Derby client functionality 
 * (connecting to Derby server over network).
 * 
 * @author Jan Tattermusch
 */
public class DerbyClientPluggableModuleImpl extends PluggableModule implements DerbyClientPluggableModule {

	/** 
	 * Creates new instance of class.
	 * @param manager pluggable module manager.
	 */
	public DerbyClientPluggableModuleImpl(PluggableModuleManager manager) {
		super(manager);
	}
	
	/**
	 * Name of Derby client driver class. 
	 */
	private static final String DERBY_CLIENT_DRIVER = "org.apache.derby.jdbc.ClientDriver";
	
	@Override
	public void doStart() throws PluggableModuleException {
		/* Load Derby client driver */
		try {
			Class.forName(DERBY_CLIENT_DRIVER).newInstance();
		} catch (Exception e) {
			throw new PluggableModuleException("Error loading derby client driver.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.client.implementation.DerbyClientPluggableModule#getClient(java.lang.String)
	 */
	public DerbyClient getClient(String url) {
		return new DerbyClientImpl(url);
	}
}
