package cz.cuni.mff.been.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.been.utils.FileUtils;
import cz.cuni.mff.been.utils.ZipUtils;

public class ZipUtilsTest extends Assert {

	private File destination;

	@Before
	public void setUp() throws Exception {
		destination = createTmpDir();
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.delete(destination);
	}

	@Test
	public void testExtractZipFile() throws Exception {
		ZipUtils.extractZipFile(new File(getResource("compressedZipFile.zip")), destination);

		assertFileContains("file1", new File(destination, "file1.txt"));
		assertFileContains("file2", new File(destination, "subfolder1/file2.txt"));
		assertFileContains("file3", new File(destination, "subfolder1/subfolder2/file3.txt"));
	}

	@Test
	public void testExtractZipFileCreatesDestinationDirStructureIfDoesNotExists() throws Exception {
		File extendedDestination = new File(destination, "and/some/non/existing/part/in/file/path");
		ZipUtils.extractZipFile(new File(getResource("compressedZipFile.zip")), extendedDestination);

		assertFileContains("file1", new File(extendedDestination, "file1.txt"));
		assertFileContains("file2", new File(extendedDestination, "subfolder1/file2.txt"));
		assertFileContains("file3", new File(extendedDestination, "subfolder1/subfolder2/file3.txt"));
	}

	@Test(expected = FileNotFoundException.class)
	public void testExtractZipFileThrowsFNFExceptionIfDestinationIsFileInsteadOfDirectory() throws Exception {
		File file = File.createTempFile("existing", "test_file");
		file.deleteOnExit();
		ZipUtils.extractZipFile(new File(getResource("compressedZipFile.zip")), file);
	}

	@Test(expected = FileNotFoundException.class)
	public void testExtractZipFileThrowsFNFExceptionIfSourceFileDoesNotExists() throws Exception {
		ZipUtils.extractZipFile(new File("non/existing/compressed/file"), destination);
	}

	@Test(expected = NullPointerException.class)
	public void testExtractZipFileThrowsNPExceptionIfDestinationIsNotDefined() throws Exception {
		ZipUtils.extractZipFile(new File(getResource("compressedZipFile.zip")), null);
	}

	@Test(expected = NullPointerException.class)
	public void testExtractZipFileThrowsNPExceptionIfSourceFileIsNotDefined() throws Exception {
		ZipUtils.extractZipFile(null, destination);
	}

	private void assertFileContains(String expected, File file) throws Exception {
		FileReader reader = new FileReader(file);
		ByteArrayOutputStream os = new ByteArrayOutputStream(1);
		int b;
		while ((b = reader.read()) != -1) {
			os.write(b);
		}
		os.flush();
		String content = new String(os.toByteArray());

		os.close();
		reader.close();
		assertEquals(true, content.contains(expected));
	}

	private File createTmpDir() throws IOException {
		File unzipDestination = File.createTempFile("testExtractZipFile_", "_tmp");
		unzipDestination.delete();
		unzipDestination.mkdir();
		return unzipDestination;
	}

	private String getResource(String resourceName) {
		return ZipUtilsTest.class.getResource(resourceName).getFile();
	}

}
