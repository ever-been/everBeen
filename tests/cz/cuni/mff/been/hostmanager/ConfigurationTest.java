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

package cz.cuni.mff.been.hostmanager;

import java.rmi.Naming;

import org.junit.Test;

import cz.cuni.mff.been.common.RMI;

/**
 * Test configuration API of the Host Manager. This test assumes that HM is running on the localhost.
 *
 * @author Branislav Repcek
 */
public class ConfigurationTest {
	
	public ConfigurationTest() {
	}
	
	@Test
	public void runTest() throws Exception {
		// ConfigurationTest.main( null );
	}
	
	/**
	 * Main.
	 * 
	 * @param args Comandline argument. Ignored.
	 * 
	 * @throws Exception If an error occured.
	 */
	public static void main(String []args) throws Exception {
		
		HostManagerInterface manager = null;
		
		manager = (HostManagerInterface) Naming.lookup(RMI.URL_PREFIX + "/been/hostmanager/main");
		
		System.out.println("Changing settings...");
		manager.getConfiguration().setActivityMonitorInterval(1000);
		manager.getConfiguration().setBriefModeInterval(4000);
		manager.getConfiguration().setDeadHostTimeout(40000);
		manager.getConfiguration().setDefaultDetailedModeInterval(500);
		manager.getConfiguration().setHostDetectionTimeout(240000);
		manager.getConfiguration().setPendingRefreshInterval(1000);
		System.out.println("Done");
	}
}
