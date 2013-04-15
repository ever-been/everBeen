package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ZipUtilTest extends Assert {

	private static final char SLASH = File.separatorChar;

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private Map<String, String> exampleContents = new HashMap<>();

	@Before
	public void setUp() {
		exampleContents.put("root.xxx", "root example content");
		exampleContents.put("root"+SLASH+"subdir.xxx", "subdir1 example content");
		exampleContents.put("root"+SLASH+"subdir.xxx", "subdir2  example content");
		exampleContents.put("x"+SLASH+"x"+SLASH+"x"+SLASH+"x"+SLASH+"x"+SLASH+"x.xxx", "mega nested example content");
	}

	@Test
	public void testZip() throws Exception {
		Collection<ItemToArchive> items = getExampleItemsCollection();
		File outputFile = tmpFolder.newFile();
		ZipUtil.createZip(items, outputFile);

		Map<String, String> contents = readZipFileContents(outputFile);

		assertMapsEquals(exampleContents, contents);
	}

	//---------------------------------------------------------
	// SUPPORT METHODS
	//---------------------------------------------------------

	private void assertMapsEquals(Map<String, String> expected,
			Map<String, String> actual) {

		assertEquals(expected.size(), actual.size());

		for (Entry<String, String> entry : expected.entrySet()) {
			if (!actual.containsKey(entry.getKey())) {
				fail("actual map does not contains key '%s'" + entry.getKey());
			}
			if (!actual.get(entry.getKey()).equals(entry.getValue())) {
				fail("value differs for key '" + entry.getKey() + "' Expected '" + entry.getValue() + "', actual '" + actual.get(entry.getKey()) + "'");
			}
		}

	}

	private Collection<ItemToArchive> getExampleItemsCollection() {
		Collection<ItemToArchive> items = new ArrayList<>();
		for (Entry<String, String> ex : exampleContents.entrySet()) {
			items.add(new StringToArchive(ex.getKey(), ex.getValue()));
		}
		return items;
	}

	private Map<String, String> readZipFileContents(File outputFile) throws FileNotFoundException, IOException {
		Map<String, String> contents = new HashMap<>();
		ZipInputStream zin = new ZipInputStream(new FileInputStream(outputFile));
		ZipEntry ze;
		while ((ze = zin.getNextEntry()) != null) {
			if (!ze.isDirectory()) {

				StringBuilder sb = new StringBuilder();
				for (int c = zin.read(); c != -1; c = zin.read()) {
					sb.append((char) c);
				}

				contents.put(ze.getName(), sb.toString());
				zin.closeEntry();
			}
		}
		zin.close();
		return contents;
	}
}
