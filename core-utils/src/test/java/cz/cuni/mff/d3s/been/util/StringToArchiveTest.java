package cz.cuni.mff.d3s.been.util;

import cz.cuni.mff.d3s.been.util.StringToArchive;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class StringToArchiveTest extends Assert {

	private static final String EXAMPLE_CONTENT = "This is an example content";

	@Test
	public void testFileToArchiveReturnsCorrectStream() throws Exception {
		StringToArchive fileToArchive = new StringToArchive("", EXAMPLE_CONTENT);
		assertEquals(EXAMPLE_CONTENT, IOUtils.toString(fileToArchive.getInputStream()));
	}

}
