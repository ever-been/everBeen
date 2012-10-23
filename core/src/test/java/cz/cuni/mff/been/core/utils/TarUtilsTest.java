package cz.cuni.mff.been.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link TarUtils}.
 * 
 * @author darklight
 * 
 */
public class TarUtilsTest extends Assert {

	final File sourceFolder;
	final File targetFolder;

	public TarUtilsTest() throws IOException {
		sourceFolder = new File(TarUtilsTest.class.getResource("tar").getFile());
		targetFolder = File.createTempFile("TarUtilsTest_", "_tmp");
		targetFolder.delete(); //clean the space up for a directory rather than a file
	}

	@After
	public void cleanupTargetFolder() throws IOException {
		if (targetFolder.exists()) {
			FileUtils.delete(targetFolder);
		}
	}
	
	@Test
	public void testPackage() throws FileNotFoundException, IOException {
		File fileToPackage = new File(sourceFolder, "loremIpsum.txt");
		TarUtils.compress(fileToPackage, targetFolder);
		File packedFile = new File(targetFolder, "loremIpsum.txt.tgz");
		assertTrue(packedFile.exists());
		assertTrue(packedFile.length() > 0);
	}

	@Test(expected = NullPointerException.class)
	public void testPackageSourceNull() throws FileNotFoundException, IOException {
		TarUtils.compress(null, targetFolder);
	}

	@Test(expected = NullPointerException.class)
	public void testPackageTargetNull() throws FileNotFoundException, IOException {
		File fileToPackage = new File(sourceFolder, "loremIpsum.txt");
		TarUtils.compress(fileToPackage, null);
	}

	@Test(expected = FileNotFoundException.class)
	public void testPackageSourceDoesntExist() throws FileNotFoundException, IOException {
		File fileToPackage = new File("fileThatDoesntExist");
		TarUtils.compress(fileToPackage, targetFolder);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPackageTargetFolderIsAFile() throws IOException {
		File fileToPackage = new File(sourceFolder, "loremIpsum.txt");
		targetFolder.mkdirs();
		File tempFile = File.createTempFile("TarUtilsTest_testPackageTargetFolderIsAFile_", "_tmp", targetFolder);
		TarUtils.compress(fileToPackage, tempFile);
	}

	@Test
	public void testPackageExplicitNamingFileExists() throws IOException {
		File fileToPackage = new File(sourceFolder, "loremIpsum.txt");
		File sourcePackageFile = new File(sourceFolder, "loremIpsum.tgz");
		File targetPackageFile = new File(targetFolder, "loremIpsum.tgz");
		targetFolder.mkdirs();
		Date copyTime = Calendar.getInstance().getTime();
		FileUtils.copyFile(sourcePackageFile, targetPackageFile);
		TarUtils.compress(fileToPackage, targetFolder, "loremIpsum.tgz");
		assertTrue(FileUtils.isFileNewer(targetPackageFile, sourcePackageFile.lastModified()));
	}

	@Test
	public void testPackageDefaultNamingFileImplicitlyExists() throws IOException {
		File fileToPackage = new File(sourceFolder, "loremIpsum.txt");
		File sourcePackageFile = new File(sourceFolder, "loremIpsum.tgz");
		File targetPackageFile = new File(targetFolder, "loremIpsum.txt.tgz");
		targetFolder.mkdirs();
		FileUtils.copyFile(sourcePackageFile, targetPackageFile);
		TarUtils.compress(fileToPackage, targetFolder);
		assertTrue(FileUtils.isFileNewer(targetPackageFile, sourcePackageFile.lastModified()));
	}

	@Test
	public void testExtract() throws FileNotFoundException, IOException {
		File archive = new File(sourceFolder, "loremIpsum.tgz");
		TarUtils.extract(archive, targetFolder);
		File targetFile = new File(targetFolder, "loremIpsum.txt");
		assertTrue(targetFile.exists());
		assertFalse(targetFile.isDirectory());
		assertTrue(targetFile.length() > 0);
	}

	@Test(expected = NullPointerException.class)
	public void testExtractSourceNull() throws FileNotFoundException, IOException {
		TarUtils.extract(null, targetFolder);
	}

	@Test(expected = NullPointerException.class)
	public void testExtractTargetNull() throws FileNotFoundException, IOException {
		File archive = new File(sourceFolder, "loremIpsum.tgz");
		TarUtils.extract(archive, null);
	}

	@Test(expected = FileNotFoundException.class)
	public void testExtractSourceDoesntExist() throws FileNotFoundException, IOException {
		File inexistentArchive = new File(sourceFolder, "inexistentArchive.tgz");
		assertFalse(inexistentArchive.exists());
		TarUtils.extract(inexistentArchive, targetFolder);
	}

	@Test(expected = ZipException.class)
	public void testExtractSourceIsNotAnArchive() throws FileNotFoundException, IOException, ZipException {
		File notAnArchive = new File(sourceFolder, "loremIpsum.txt");
		assertTrue(notAnArchive.exists());
		TarUtils.extract(notAnArchive, targetFolder);
	}

	@Test
	public void testExtractFileExists() throws IOException {
		File archive = new File(sourceFolder, "loremIpsum.tgz");
		File sourceFile = new File(sourceFolder, "loremIpsum.txt");
		File copyOfSourceFile = new File(targetFolder, "loremIpsum.txt");
		FileUtils.copyFile(sourceFile, copyOfSourceFile);
		assertTrue(copyOfSourceFile.exists());
		Date dateModified = new Date(copyOfSourceFile.lastModified());
		TarUtils.extract(archive, targetFolder);
		assertTrue(FileUtils.isFileNewer(copyOfSourceFile, dateModified));
	}

	@Test
	public void testExtractDir() throws FileNotFoundException, ZipException, IOException {
		File packedFolder = new File(sourceFolder, "loremIpsumFolder.tgz");
		TarUtils.extract(packedFolder, targetFolder);
		File unpackedFolder = new File(targetFolder, "loremIpsumFolder");
		assertTrue(unpackedFolder.isDirectory());
		File unpackedFolderContents = new File(unpackedFolder, "loremIpsum.txt");
		assertTrue(unpackedFolderContents.exists());
		assertTrue(unpackedFolderContents.length() > 0);
	}

	@Test
	public void testExtractdirExists() throws FileNotFoundException, ZipException, IOException {
		File packedDir = new File(sourceFolder, "loremIpsumFolder.tgz");
		File folderInTarget = new File(targetFolder, "loremIpsumFolder");
		folderInTarget.mkdirs();
		assertTrue(folderInTarget.isDirectory());
		assertTrue(folderInTarget.list().length == 0);
		TarUtils.extract(packedDir, targetFolder);
		File extractedTextFile = new File(folderInTarget, "loremIpsum.txt");
		assertTrue(extractedTextFile.exists());
		assertTrue(extractedTextFile.length() > 0);
	}

	@Test
	public void testExtractDirContentExists() {}
}
