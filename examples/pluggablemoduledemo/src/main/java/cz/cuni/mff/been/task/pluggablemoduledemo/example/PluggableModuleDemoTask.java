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
package cz.cuni.mff.been.task.pluggablemoduledemo.example;

import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModule;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;


/**
 * <p>Demonstrates using of pluggable modules.</p>
 * 
 * @author Jan Tattermusch
 */
public class PluggableModuleDemoTask extends Job {

    /** Default value of module.name task property */
    public static final String DEFAULT_MODULE_NAME = "dummy.bpk";

    /**
     * Allocates a new <code>PluggableModuleDemoTask</code> object.
     * 
     * @throws TaskInitializationException
     */
    public PluggableModuleDemoTask() throws TaskInitializationException {
        super();
    }

    @Override
    protected void checkRequiredProperties() throws TaskException {

    }
	
    @Override
    protected void run() throws TaskException {

        String moduleName = "derby";
        String moduleVersion = "2.1.0";

        logInfo("Loading module \"" + moduleName + "\"...");

        PluggableModuleManager manager = this.getPluggableModuleManager();
        logInfo("Pluggable module manager retrieved.");
        
        
        PluggableModule m;
        try {
            m = manager.getModule( new PluggableModuleDescriptor( moduleName, moduleVersion) );
        } catch (PluggableModuleException ex) {
            throw new TaskException("Error loading pluggable module.", ex);
        }
        logInfo("Module loaded.");
        
        if (m instanceof DerbyPluggableModule) {
            try {
                DerbyPluggableModule dpm = (DerbyPluggableModule) m;
                //logInfo(dpm.HelloWorld("test string by dummy pluggable module"));
                logInfo("pluggable module run.");
                dpm.startEngine("", false);
                logInfo("derby engine started.");
                
                dpm.getConnection("somedatabase");
                logInfo("connection obtained.");
                
                dpm.stopEngine();
                logInfo("derby engine stopped.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
