package cz.cuni.mff.been.utils;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tadeáš Palusga
 * 
 */
public class FileUtilsTest extends Assert {

	@Test
	public void testConstructPathIfSeperatorInDirNameAlreadyPresent() throws Exception {
		String dirName = "dirname" + File.separator;
		String fileName = "filename";
		assertEquals(dirName + fileName, FileUtils.constructPath(dirName, fileName));
	}

	@Test
	public void testConstructPathIfSeperatorInDirNameNotPresent() throws Exception {
		String dirName = "dirname";
		String fileName = "filename";
		assertEquals(dirName + File.separator + fileName, FileUtils.constructPath(dirName, fileName));
	}

	@Test
	public void testDeleteExistingFile() throws Exception {
		File f = File.createTempFile("test", null);
		assertTrue(FileUtils.delete(f));
		assertFalse(f.exists());
	}

	@Test
	public void testDeleteNonExistingFile() throws Exception {
		File f = new File("non/existing/file");
		assertFalse(FileUtils.delete(f));
		assertFalse(f.exists());
	}

	@Test
	public void testDeleteExistingEmptyFolder() throws Exception {
		File dir = createTmpDir();
		assertTrue(FileUtils.delete(dir));
		assertFalse(dir.exists());
	}

	@Test
	public void testDeleteExistingNonEmptyFolder() throws Exception {
		File dir = createTmpDir();
		new File(dir, "subfolder/sobfolder2/subfolder3").mkdirs();
		new File(dir, "subfolder/sobfolder2/subfolder3/file.txt").createNewFile();
		assertTrue(FileUtils.delete(dir));
		assertFalse(dir.exists());
	}

	private File createTmpDir() throws IOException {
		File unzipDestination = File.createTempFile("testExtractZipFile_", "_tmp");
		unzipDestination.delete();
		unzipDestination.mkdir();
		return unzipDestination;
	}

}
