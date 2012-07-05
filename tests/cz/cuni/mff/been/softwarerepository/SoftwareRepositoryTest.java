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

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.been.common.UploadStatus;
import cz.cuni.mff.been.softwarerepository.Utils.UploadResult;

public class SoftwareRepositoryTest {
	private SoftwareRepositoryImplementation softwareRepository;
	private String beenHome;
	
	@Before
	public void setUp() throws Exception {
		if ((beenHome = System.getenv("BEEN_HOME")) == null) {
			throw new Exception("BEEN_HOME environment variable not defined.");
		}

		softwareRepository = Utils.setUpRepository(beenHome);
	}
	
	private void assertPackageAccepted(String packageName) throws IOException {
		assertTrue(Utils.uploadPackage(softwareRepository, packageName, beenHome).getStatus()
			== UploadStatus.ACCEPTED);
	}

	private void assertPackageRejected(String packageName, String[] errorMessages) throws IOException {
		UploadResult result = Utils.uploadPackage(softwareRepository, packageName,
			beenHome);
		assertTrue(result.getStatus() == UploadStatus.REJECTED);
		assertTrue(Arrays.equals(result.getErrorMessages(), errorMessages));
	}

	@Test
	public void testGeneralEmptyFile() throws IOException {
		assertPackageRejected("general-empty-file.bpk", new String[] {
			"Error reading package file."
		});
	}
	
	@Test
	public void testGeneralEmptyZipFile() throws IOException {
		assertPackageRejected("general-empty-zip-file.bpk", new String[] {
			"Error reading package file."
		});
	}
	
	@Test
	public void testGeneralNoFiles() throws IOException {
		assertPackageRejected("general-no-files.bpk", new String[] {
			"Missing \"" + SoftwareRepositoryImplementation.PACKAGE_FILES_DIR
				+ "\" directory.",
		}); 
	}
	
	@Test
	public void testGeneralFilesNotDir() throws IOException {
		assertPackageRejected("general-files-not-dir.bpk", new String [] {
			"Missing \"" + SoftwareRepositoryImplementation.PACKAGE_FILES_DIR
				+ "\" directory.",
		});
	}
	
	@Test
	public void testGeneralNoMetadata() throws IOException {
		assertPackageRejected("general-no-metadata.bpk", new String[] {
			"Missing \"" + SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE
				+ "\" file."
		});
	}
	
	@Test
	public void testGeneralMetadataNotXML() throws IOException {
		assertPackageRejected("general-metadata-not-xml.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Not valid XML file." 
		}); 
	}
	
	@Test
	public void testMetadataNoPackageElement() throws IOException {
		assertPackageRejected("metadata-no-package-element.bpk", new String[] {
				SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE
					+ ": Root element must be <package>." 
		});
	}
	
	@Test
	public void testMetadataEmptyPackageElement() throws IOException {
		assertPackageRejected("metadata-empty-package-element.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <type> element or invalid value of element <type>." 
		}); 	
	}
	
	@Test
	public void testMetadataPackageElementWithUnrecognizedSubelement() throws IOException {
		assertPackageRejected("metadata-package-element-with-unrecognized-subelement.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <type> element or invalid value of element <type>." 
		});
	}
	
	@Test
	public void testMetadataNoType() throws IOException {
		assertPackageRejected("metadata-no-type.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE
				+ ": Missing <type> element or invalid value of element <type>." 
		});
	}
	
	@Test
	public void testMetadataInvalidType() throws IOException {
		assertPackageRejected("metadata-invalid-type.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <type> element or invalid value of element <type>." 
		}); 
	}
	
	@Test
	public void testMetadataSourceMinimal() throws IOException {
		assertPackageAccepted("metadata-source-minimal.bpk");
	}
	
	@Test
	public void testMetadataSourceMoreHardwarePlatforms() throws IOException {
		assertPackageAccepted("metadata-source-more-hardware-platforms.bpk"); 	
	}
	
	@Test
	public void testMetadataSourceMoreSoftwarePlatforms() throws IOException {
		assertPackageAccepted("metadata-source-more-software-platforms.bpk"); 	
	}
	
	@Test
	public void testMetadataSourceAllOptionalElements() throws IOException {
		assertPackageAccepted("metadata-source-all-optional-elements.bpk"); 	
	}
	
	@Test
	public void testMetadataSourceNoName() throws IOException {
		assertPackageRejected("metadata-source-no-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <name> element."
		}); 	
	}
	
	@Test
	public void testMetadataSourceEmptyName() throws IOException {
		assertPackageRejected("metadata-source-empty-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Invalid value of element <name>."
		});
	}
	
	@Test
	public void testMetadataSourceInvalidName() throws IOException {
		assertPackageRejected("metadata-source-invalid-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE
				+ ": Invalid value of element <name>."
		}); 	
	}
	
	@Test
	public void testMetadataSourceNoVersion() throws IOException {
		assertPackageRejected("metadata-source-no-version.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <version> element."
		}); 	
	}
	
	@Test
	public void testMetadataSourceNoHumanName() throws IOException {
		assertPackageRejected("metadata-source-no-human-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <humanName> element."
		}); 	
	}
	
	@Test
	public void testMetadataBinaryMinimal() throws IOException {
		assertPackageAccepted("metadata-binary-minimal.bpk");
	}
	
	@Test
	public void testMetadataBinaryMorehardwarePlatforms() throws IOException {
		assertPackageAccepted("metadata-binary-more-hardware-platforms.bpk");
	}
	
	@Test
	public void testMetadataBinaryMoreSoftwarePlatforms() throws IOException {
		assertPackageAccepted("metadata-binary-more-software-platforms.bpk");
	}
	
	@Test
	public void testMetadataBinaryAllOptionalElements() throws IOException {
		assertPackageAccepted("metadata-binary-all-optional-elements.bpk");
	}
	
	@Test
	public void testMetadataBinaryNoName() throws IOException {
		assertPackageRejected("metadata-binary-no-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <name> element."
		});
	}
	
	@Test
	public void testMetadataBinaryEmptyName() throws IOException {
		assertPackageRejected("metadata-binary-empty-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Invalid value of element <name>."
		});
	}
	
	@Test
	public void testMetadataBinaryInvalidName() throws IOException {
		assertPackageRejected("metadata-binary-invalid-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Invalid value of element <name>."
		});
	}
	
	@Test
	public void testMetadataBinaryNoVersion() throws IOException {
		assertPackageRejected("metadata-binary-no-version.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <version> element."
		});
	}
	
	@Test
	public void testMetadataBinaryNoHumanName() throws IOException {
		assertPackageRejected("metadata-binary-no-human-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <humanName> element."
		});
	}
	
	@Test
	public void testMetadataTaskMinimal() throws IOException {
		assertPackageAccepted("metadata-task-minimal.bpk");
	}
	
	@Test
	public void testMetadataTaskMoreHardwarePlatforms() throws IOException {
		assertPackageAccepted("metadata-task-more-hardware-platforms.bpk");
	}
	
	@Test
	public void testMetadataTaskMoreSoftwarePlatforms() throws IOException {
		assertPackageAccepted("metadata-task-more-software-platforms.bpk");
	}
	
	@Test
	public void testMetadataTaskAllOptionalElements() throws IOException {
		assertPackageAccepted("metadata-task-all-optional-elements.bpk");
	}
	
	@Test
	public void testMetadataTaskNoName() throws IOException {
		assertPackageRejected("metadata-task-no-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <name> element."
		});
	}
	
	@Test
	public void testMetadataTaskEmptyName() throws IOException {
		assertPackageRejected("metadata-task-empty-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Invalid value of element <name>."
		});
	}
	
	@Test
	public void testMetadataTaskInvalidTaskName() throws IOException {
		assertPackageRejected("metadata-task-invalid-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Invalid value of element <name>."
		});
	}
	
	@Test
	public void testMetadataTaskNoVersion() throws IOException {
		assertPackageRejected("metadata-task-no-version.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <version> element."
		});
	}
	
	@Test
	public void testMetadataTaskNoHumanName() throws IOException {
		assertPackageRejected("metadata-task-no-human-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <humanName> element."
		});
	}
	
	@Test
	public void testMetadataDataMinimal() throws IOException {
		assertPackageAccepted("metadata-data-minimal.bpk");
	}
	
	@Test
	public void testMetadataDataMoreHardwarePlatforms() throws IOException {
		assertPackageAccepted("metadata-data-more-hardware-platforms.bpk");
	}
	
	@Test
	public void testMetadataDataMoreSoftwarePlatforms() throws IOException {
		assertPackageAccepted("metadata-data-more-software-platforms.bpk");
	}
	
	@Test
	public void testMetadataDataAlloptionalElements() throws IOException {
		assertPackageAccepted("metadata-data-all-optional-elements.bpk");
	}
	
	@Test
	public void testMetadataDataNoName() throws IOException {
		assertPackageRejected("metadata-data-no-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <name> element."
		});
	}
	
	@Test
	public void testMetadataDataEmptyName() throws IOException {
		assertPackageRejected("metadata-data-empty-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Invalid value of element <name>."
		});
	}
	
	@Test
	public void testMetadataDataInvalidName() throws IOException {
		assertPackageRejected("metadata-data-invalid-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Invalid value of element <name>."
		});
	}
	
	@Test
	public void testMetadataDataNoVersion() throws IOException {
		assertPackageRejected("metadata-data-no-version.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE
				+ ": Missing <version> element."
		});
	}
	
	@Test
	public void testMetadataDataNoHumanName() throws IOException {
		assertPackageRejected("metadata-data-no-human-name.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_METADATA_FILE 
				+ ": Missing <humanName> element."
		}); 
	}
	
	@Test
	public void testConfigTaskNoConfig() throws IOException {
		assertPackageRejected("config-task-no-config.bpk", new String[] {
			"Missing \"" + SoftwareRepositoryImplementation.PACKAGE_CONFIG_FILE 
				+ "\" file."
		});
	}

// this legacy test does not work
//	@Test
//	public void testConfigTaskConfigNotXML() throws IOException {
//		assertPackageRejected("config-task-config-not-xml.bpk", new String[] {
//			SoftwareRepositoryImplementation.PACKAGE_CONFIG_FILE 
//				+ ": Not valid XML file." 
//		});  
//	}

	@Test
	public void testConfigTaskNoPackageConfigurationElement() throws IOException {
		assertPackageRejected("config-task-no-package-configuration-element.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_CONFIG_FILE 
				+ ": Root element must be <packageConfiguration>." 
		});  
	}

// this legacy test does not work
//	@Test
//	public void testConfigTaskEmptyPackageConfigurationElement() throws IOException {
//		assertPackageRejected("config-task-empty-package-configuration-element.bpk", new String[] {
//			SoftwareRepositoryImplementation.PACKAGE_CONFIG_FILE 
//				+ ": There must be one <java> element present." 
//		});  
//	}

// this legacy test does not work
//	@Test
//	public void testConfigTaskNoJavaElement() throws IOException {
//		assertPackageRejected("config-task-no-java-element.bpk", new String[] {
//			SoftwareRepositoryImplementation.PACKAGE_CONFIG_FILE 
//				+ ": There must be one <java> element present." 
//		});  
//	}

	@Test
	public void testConfigTaskNoClassPathAttribute() throws IOException {
		assertPackageRejected("config-task-no-class-path-attribute.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_CONFIG_FILE 
				+ ": Missing \"classPath\" attribute of the <java> element." 
		});  
	}

	@Test
	public void testConfigTaskNoMainClassAttribute() throws IOException {
		assertPackageRejected("config-task-no-main-class-attribute.bpk", new String[] {
			SoftwareRepositoryImplementation.PACKAGE_CONFIG_FILE 
				+ ": Missing \"mainClass\" attribute of the <java> element." 
		});  
	}
}
