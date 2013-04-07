package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileToArchiveTest extends Assert {

	private static final String EXAMPLE_CONTENT = "This is an example content";

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Test
	public void testFileToArchiveReturnsCorrectStream() throws Exception {
		File testFile = tmpFolder.newFile();
		FileUtils.write(testFile, EXAMPLE_CONTENT);

		FileToArchive fileToArchive = new FileToArchive("", testFile);
		assertEquals(EXAMPLE_CONTENT, IOUtils.toString(fileToArchive.getInputStream()));
	}

}
