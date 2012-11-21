package cz.cuni.mff.been.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for custom {@link FileUtils} methods.
 * 
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
		FileUtils.delete(f);
		assertFalse(f.exists());
	}

	@Test(expected = NoSuchFileException.class)
	public void testDeleteNonExistingFile() throws Exception {
		File f = new File("non/existing/file");
		FileUtils.delete(f);
		assertFalse(f.exists());
	}

	@Test
	public void testDeleteExistingEmptyFolder() throws Exception {
		File dir = createTmpDir();
		FileUtils.delete(dir);
		assertFalse(dir.exists());
	}

	@Test
	public void testDeleteExistingNonEmptyFolder() throws Exception {
		File dir = createTmpDir();
		new File(dir, "subfolder/sobfolder2/subfolder3").mkdirs();
		new File(dir, "subfolder/sobfolder2/subfolder3/file.txt").createNewFile();
		FileUtils.delete(dir);
		assertFalse(dir.exists());
	}

	@Test(expected = IOException.class)
	public void testDeleteFileThrowsFNFExceptionIfThePathDoesNotReferToAFile() throws Exception {
		FileUtils.deleteFile(createTmpDir());
	}

	@Test(expected = IOException.class)
	public void testDeleteDirectoryThrowsFNFExceptionIfThePathDoesNotReferToAFolder() throws Exception {
		FileUtils.deleteDirectory(File.createTempFile("testFile", null));
	}

	@Test
	public void testRecursiveSearch_singleMatch() throws IOException {
		File tmpDir = createTmpDir();
		File tmpFile1 = new File(tmpDir, "tmpFile1.txt");
		File tmpFile2 = new File(tmpDir, "tmpFile2.log");
		tmpFile1.createNewFile();
		tmpFile2.createNewFile();
		Iterator<File> found = FileUtils.findFilesRecursivelyByName(tmpDir, ".*File.*\\.txt").iterator();
		assertTrue(found.hasNext());
		assertEquals(tmpFile1, found.next());
		assertFalse(found.hasNext());
		FileUtils.deleteDirectory(tmpDir);
	}
	@Test
	public void testRecursiveSearch_noMatch() throws IOException {
		File tmpDir = createTmpDir();
		File tmpFile1 = new File(tmpDir, "tmpFile1.txt");
		File tmpFile2 = new File(tmpDir, "tmpFile2.log");
		tmpFile1.createNewFile();
		tmpFile2.createNewFile();
		Iterator<File> found = FileUtils.findFilesRecursivelyByName(tmpDir, ".*\\.png").iterator();
		assertFalse(found.hasNext());
		FileUtils.deleteDirectory(tmpDir);
	}

	@Test
	public void testRecursiveSearch_multipleMatches() throws IOException {
		File tmpDir = createTmpDir();
		File tmpFile1 = new File(tmpDir, "tmpFile1.txt");
		File tmpFile2 = new File(tmpDir, "tmpFile2.log");
		File tmpFile3 = new File(tmpDir, "tmpFile3.png");
		tmpFile1.createNewFile();
		tmpFile2.createNewFile();
		tmpFile3.createNewFile();
		Iterator<File> found = FileUtils.findFilesRecursivelyByName(tmpDir, ".*\\.(png|log)").iterator();
		assertTrue(found.hasNext());
		assertEquals(tmpFile2, found.next());
		assertTrue(found.hasNext());
		assertEquals(tmpFile3, found.next());
		assertFalse(found.hasNext());
		FileUtils.deleteDirectory(tmpDir);
	}

	@Test
	public void testRecursiveSearch_testRecursion() throws IOException {
		File tmpDir = createTmpDir();
		File tmpSubdir1 = new File(tmpDir, "subdir1");
		File tmpSubdir2 = new File(tmpDir, "subdir2");
		tmpSubdir1.mkdir();
		tmpSubdir2.mkdir();

		File tmpFile1 = new File(tmpSubdir1, "tmpFile1.txt");
		File tmpFile2 = new File(tmpSubdir2, "tmpFile2.log");
		File tmpFile3 = new File(tmpDir, "tmpFile3.png");
		tmpFile1.createNewFile();
		tmpFile2.createNewFile();
		tmpFile3.createNewFile();

		Iterator<File> found = FileUtils.findFilesRecursivelyByName(tmpDir, ".*1.*").iterator();
		assertTrue(found.hasNext());
		assertEquals(tmpSubdir1, found.next());
		assertTrue(found.hasNext());
		assertEquals(tmpFile1, found.next());
		assertFalse(found.hasNext());
		FileUtils.deleteDirectory(tmpDir);
	}

	@Test
	public void testRecursiveSearch_testRootNotIncluded() throws IOException {
		File tmpDir = createTmpDir();
		Iterator<File> found = FileUtils.findFilesRecursivelyByName(tmpDir, tmpDir.getName()).iterator();
		assertFalse(found.hasNext());
		FileUtils.deleteDirectory(tmpDir);
	}

	@Test(expected = FileNotFoundException.class)
	public void testRecursiveSearch_rootDoesntExist() throws IOException {
		FileUtils.findFilesRecursivelyByName(new File("iDontExist"), ".*");
	}

	@Test(expected = NullPointerException.class)
	public void tesetRecursiveSearch_rootIsNull() throws IOException {
		FileUtils.findFilesRecursivelyByName(null, ".*");
	}

	private File createTmpDir() throws IOException {
		File unzipDestination = File.createTempFile("testFile_", "_tmp");
		unzipDestination.delete();
		unzipDestination.mkdir();
		return unzipDestination;
	}

}
