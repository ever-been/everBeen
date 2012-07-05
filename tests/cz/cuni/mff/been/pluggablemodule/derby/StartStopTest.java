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

package cz.cuni.mff.been.pluggablemodule.derby;

import org.apache.derby.drda.NetworkServerControl;
import org.junit.Test;

import cz.cuni.mff.been.pluggablemodule.MockPluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.implementation.DerbyPluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.derby.implementation.DerbyPluggableModuleImpl;
import cz.cuni.mff.been.pluggablemodule.derby.implementation.DerbyProperties;

/**
 * Test for <code>DerbyPluggableModuleImpl</code> class.
 * 
 * Tries to start and stop derby embedded.
 *
 * @author Jan Tattermusch
 */
public class StartStopTest {

    public StartStopTest() {
    }

    /** 
	 * Returns pluggable module manager's mock
	 * @return pluggable module manager.
	 */
	private static PluggableModuleManager getPluggableModuleManager() {
		return new MockPluggableModuleManager();			
	}
    
    /**
     * Tries to start and stop derby without network access
     * @throws DerbyPluggableModuleException when something goes wrong
     */
    @Test
    public void testDerbyStartStop() throws DerbyPluggableModuleException {
        
        System.err.println(System.getProperty("java.class.path"));


        DerbyPluggableModuleImpl mod = new DerbyPluggableModuleImpl(getPluggableModuleManager());

        // start derby
        DerbyProperties derbyProperties = new DerbyProperties("tmp", false);
        mod.startEngine(derbyProperties);
        
        // stop derby
        mod.stopEngine();

    }

    /** 
     * Tries to start and stop derby with networkaccessible option on.
     * After starting derby, the network server is pinged to test 
     * whether it is really on.
     * @throws Exception when something goes wrong
     */
    @Test
    public void testDerbyStartStopWithNetwork() throws Exception {
        DerbyPluggableModuleImpl mod = new DerbyPluggableModuleImpl(getPluggableModuleManager());

        // start derby with network access option
        DerbyProperties derbyProperties = new DerbyProperties("", true);
        mod.startEngine(derbyProperties);

        // try to ping server
        NetworkServerControl ns = new NetworkServerControl();
        ns.ping();

        // shutdown derby
        mod.stopEngine();

    }
}