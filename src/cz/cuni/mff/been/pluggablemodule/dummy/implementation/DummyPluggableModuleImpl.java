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

package cz.cuni.mff.been.pluggablemodule.dummy.implementation;

import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.dummy.DummyPluggableModule;

/**
 * Example implementation of dummy pluggable module.
 * @author Jan Tattermusch
 */
public class DummyPluggableModuleImpl extends PluggableModule implements DummyPluggableModule {

	/** 
	 * Creates new instance of class.
	 * @param manager pluggable module manager.
	 */
	public DummyPluggableModuleImpl(PluggableModuleManager manager) {
		super(manager);
	}
	
    /* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.dummy.implementation.DummyPluggableModule#HelloWorld(java.lang.String)
	 */
    public String HelloWorld(String str) {
        return "Hello world:" + str;
    }

    @Override
    protected void doStart() throws PluggableModuleException {
        System.out.print("DummyPluggableModule: doStart method executed.");
    }
}
