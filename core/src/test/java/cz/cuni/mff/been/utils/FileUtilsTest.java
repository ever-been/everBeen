package cz.cuni.mff.been.utils;

import java.io.File;

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

}
