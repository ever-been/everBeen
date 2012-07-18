/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
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
package cz.cuni.mff.been.softwarerepository;

import static org.junit.Assert.assertTrue;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 *
 *  @author: Jiri Tauber
 */
public class ProvidedInterfaceQueryCallbackTest {
	private SoftwareRepositoryImplementation softwareRepository;
	private String beenHome;
	private HashMap<String, Integer> matches;
	
	@Before
	public void setUp() throws Exception {
		if ((beenHome = System.getenv("BEEN_HOME")) == null) {
			throw new Exception("BEEN_HOME environment variable not defined.");
		}

		softwareRepository = Utils.setUpRepository(beenHome);
		
		Utils.uploadPackage(softwareRepository,
			"metadata-interface-test1.bpk", beenHome);
		Utils.uploadPackage(softwareRepository,
			"metadata-interface-test12.bpk", beenHome);
	}
	
	public void doMatchTest(HashMap<String, Integer> matches)
			throws MatchException, RemoteException {
		for (String query: matches.keySet()) {
			PackageMetadata[] metadata = softwareRepository.queryPackages(
				new ProvidedInterfaceQueryCallback(query)
			);
			assertTrue("Number of packages should be " + matches.get(query)
				+ ", but it is " + metadata.length + ".",
				matches.get(query).equals(metadata.length));
		}
	}
	
	@Test
	public void testInterfaces() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("test1", 2);
		matches.put("test2", 1);
		matches.put("test3", 0);
		doMatchTest(matches);
	}

}
