package cz.cuni.mff.d3s.been.util;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ZipUtilTest extends Assert {

	private static final char SLASH = File.separatorChar;

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private Map<String, String> stringContents = new HashMap<>();

	@Before
	public void setUp() {
		stringContents.put("root.xxx", "root example content");
		stringContents.put("root" + SLASH + "subdir.xxx", "subdir1 example content");
		stringContents.put("root" + SLASH + "subdir.xxx", "subdir2  example content");
		stringContents.put("x" + SLASH + "x" + SLASH + "x" + SLASH + "x" + SLASH + "x" + SLASH + "x.xxx", "mega nested example content");
	}

	@Test
	public void testZipStrings() throws Exception {
		Collection<ItemToArchive> items = getExampleItemsCollection();
		File outputFile = tmpFolder.newFile();
		ZipUtil.createZip(items, outputFile);
		Map<String, String> contents = readZipFileContents(outputFile);
		assertMapsEqual(stringContents, contents);
	}

    @Test
    public void testUnzipStrings() throws Exception {
        final File archive = File.createTempFile(getClass().getSimpleName(), "zip");

        final ZipOutputStream zipStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(archive)));
        for(Map.Entry<String,String> entry: stringContents.entrySet()) {
            zipStream.putNextEntry(new ZipArchiveEntry(entry.getKey()));
            final byte [] bytes = entry.getValue().getBytes();
            zipStream.write(bytes,0,bytes.length);
            zipStream.closeEntry();
        }
        zipStream.close();

        final File destDir = tmpFolder.newFolder();
        ZipUtil.unzipToDir(archive, destDir);

        assertFSContentCorrespondsWithMap(destDir, stringContents);
    }

    @Test
    public void testPackUnpackEmptyFolder() throws IOException {
        final File sourceFolder = tmpFolder.newFolder();
        final File targetFolder = tmpFolder.newFolder();
        final File archive = tmpFolder.newFile();

        final File folderToPack = new File(sourceFolder, "folder");
        folderToPack.mkdir();

        final Collection<ItemToArchive> toPack = Arrays.asList((ItemToArchive) new FileToArchive("myFolder", folderToPack));
        ZipUtil.createZip(toPack, archive);
        ZipUtil.unzipToDir(archive, targetFolder);

        final File expectedUnpackedFolder = new File(targetFolder, "myFolder");
        assertTrue(expectedUnpackedFolder.exists());
        assertTrue(expectedUnpackedFolder.isDirectory());
    }

	//---------------------------------------------------------
	// SUPPORT METHODS
	//---------------------------------------------------------

	private void assertMapsEqual(Map<String, String> expected,
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

    private void assertFSContentCorrespondsWithMap(File rootDir, Map<String,String> expectedContent) throws IOException {
        assertTrue(rootDir.exists() && rootDir.isDirectory());
        for(Map.Entry<String,String> entry: expectedContent.entrySet()) {
            final File expectedFile = new File(rootDir, entry.getKey());
            assertTrue(expectedFile.exists());
            if (expectedFile.isDirectory()) {
                assertNull(entry.getValue()); // the only kind of entries we want to create dirs for are empty ones
            } else {
                assertEquals(entry.getValue(), FileUtils.readFileToString(expectedFile));
            }
        }
    }

	private Collection<ItemToArchive> getExampleItemsCollection() {
		Collection<ItemToArchive> items = new ArrayList<>();
		for (Entry<String, String> ex : stringContents.entrySet()) {
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
