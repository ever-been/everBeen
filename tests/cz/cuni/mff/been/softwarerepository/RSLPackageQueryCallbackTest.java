/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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

public class RSLPackageQueryCallbackTest {
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
			"metadata-source-all-optional-elements.bpk", beenHome);
		Utils.uploadPackage(softwareRepository,
			"metadata-binary-all-optional-elements.bpk", beenHome);
		Utils.uploadPackage(softwareRepository,
			"metadata-task-all-optional-elements.bpk", beenHome);
		Utils.uploadPackage(softwareRepository,
			"metadata-data-all-optional-elements.bpk", beenHome);
	}
	
	public void doMatchTest(HashMap<String, Integer> matches)
			throws MatchException, RemoteException {
		for (String query: matches.keySet()) {
			PackageMetadata[] metadata = softwareRepository.queryPackages(
				new RSLPackageQueryCallback(query)
			);
			assertTrue("Metadata length should be " + matches.get(query)
				+ ", but it is " + metadata.length + ".",
				matches.get(query).equals(metadata.length));
		}
	}
	
	@Test
	public void testName() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("name == \"metadata-source-all-optional-elements\"", 1);
		matches.put("name != \"metadata-source-all-optional-elements\"", 3);
		matches.put("name < \"metadata-source-all-optional-elements\"", 2);
		matches.put("name <= \"metadata-source-all-optional-elements\"", 3);
		matches.put("name > \"metadata-source-all-optional-elements\"", 1);
		matches.put("name >= \"metadata-source-all-optional-elements\"", 2);
		doMatchTest(matches);
	}
	
	@Test
	public void testVersion() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("version == 42.0", 4);
		matches.put("version != 42.0", 0);
		matches.put("version < 42.0", 0);
		matches.put("version <= 42.0", 4);
		matches.put("version > 42.0", 0);
		matches.put("version >= 42.0", 4);
		doMatchTest(matches);
	}
	
	@Test
	public void testHardwarePlatforms() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("hardwarePlatforms contains \"hardwarePlatform1\"", 4);
		matches.put("hardwarePlatforms contains \"invalidHardwarePlatform\"", 0);
		doMatchTest(matches);
	}
	 	
	@Test
	public void testSoftwarePlatforms() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("softwarePlatforms contains \"softwarePlatform1\"", 4);
		matches.put("softwarePlatforms contains \"invalidSoftwarePlatform\"", 0);
		doMatchTest(matches);
	}
	 	
	@Test
	public void testType() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("type == source", 1);
		matches.put("type != source", 3);
		matches.put("type == binary", 1);
		matches.put("type != binary", 3);
		matches.put("type == task", 1);
		matches.put("type != task", 3);
		matches.put("type == data", 1);
		matches.put("type != data", 3);
		doMatchTest(matches);
	}
	 	
	@Test
	public void testHumanName() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("humanName == \"humanNameValue\"", 4);
		matches.put("humanName != \"humanNameValue\"", 0);
		matches.put("humanName < \"humanNameValue\"", 0);
		matches.put("humanName <= \"humanNameValue\"", 4);
		matches.put("humanName > \"humanNameValue\"", 0);
		matches.put("humanName >= \"humanNameValue\"", 4);
		doMatchTest(matches);
	}
	 		 	
	@Test
	public void testDownloadURL() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("downloadURL == \"downloadURLValue\"", 4);
		matches.put("downloadURL != \"downloadURLValue\"", 0);
		matches.put("downloadURL < \"downloadURLValue\"", 0);
		matches.put("downloadURL <= \"downloadURLValue\"", 4);
		matches.put("downloadURL > \"downloadURLValue\"", 0);
		matches.put("downloadURL >= \"downloadURLValue\"", 4);
		doMatchTest(matches);
	}
	 	
	@Test
	public void testDownloadDate() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("downloadDate == 2004-11-23T20:38:00Z", 4);
		matches.put("downloadDate != 2004-11-23T20:38:00Z", 0);
		matches.put("downloadDate < 2004-11-23T20:38:00Z", 0);
		matches.put("downloadDate <= 2004-11-23T20:38:00Z", 4);
		matches.put("downloadDate > 2004-11-23T20:38:00Z", 0);
		matches.put("downloadDate >= 2004-11-23T20:38:00Z", 4);
		doMatchTest(matches);
	}
	 		 	
	@Test
	public void testSourcePackageFilename() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("sourcePackageFilename == \"sourcePackageFilenameValue\"", 2);
		matches.put("sourcePackageFilename != \"sourcePackageFilenameValue\"", 2);
		matches.put("sourcePackageFilename < \"sourcePackageFilenameValue\"", 0);
		matches.put("sourcePackageFilename <= \"sourcePackageFilenameValue\"", 2);
		matches.put("sourcePackageFilename > \"sourcePackageFilenameValue\"", 0);
		matches.put("sourcePackageFilename >= \"sourcePackageFilenameValue\"", 2);
		doMatchTest(matches);
	}

	@Test
	public void testBinaryIdentifier() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("binaryIdentifier == \"binaryIdentifierValue\"", 1);
		matches.put("binaryIdentifier != \"binaryIdentifierValue\"", 3);
		matches.put("binaryIdentifier < \"binaryIdentifierValue\"", 0);
		matches.put("binaryIdentifier <= \"binaryIdentifierValue\"", 1);
		matches.put("binaryIdentifier > \"binaryIdentifierValue\"", 0);
		matches.put("binaryIdentifier >= \"binaryIdentifierValue\"", 1);
		doMatchTest(matches);
	}

	@Test
	public void testBuildConfiguration() throws MatchException, RemoteException {
		matches = new HashMap<String, Integer>();
		matches.put("buildConfiguration == \"buildConfigurationValue\"", 1);
		matches.put("buildConfiguration != \"buildConfigurationValue\"", 3);
		matches.put("buildConfiguration < \"buildConfigurationValue\"", 0);
		matches.put("buildConfiguration <= \"buildConfigurationValue\"", 1);
		matches.put("buildConfiguration > \"buildConfigurationValue\"", 0);
		matches.put("buildConfiguration >= \"buildConfigurationValue\"", 1);
		doMatchTest(matches);
	} 
}
