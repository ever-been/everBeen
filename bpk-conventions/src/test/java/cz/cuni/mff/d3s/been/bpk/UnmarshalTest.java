package cz.cuni.mff.d3s.been.bpk;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class UnmarshalTest extends Assert {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Test
	public void testUnmarshallingConfigFromBpkFile() throws Exception {
		File bpkFile = zip("config2.xml");

		BpkConfiguration config = BpkResolver.resolve(bpkFile);

		JavaRuntime javaRuntime = (JavaRuntime) config.getRuntime();
		assertEquals("cz.cuni.mff.d3s.been.MakeTea", javaRuntime.getMainClass());
		assertEquals("task-test-3.0.0.jar", javaRuntime.getJarFile());
		assertEquals(7, javaRuntime.getBpkArtifacts().getArtifact().size());
	}

	@Test(expected=BpkConfigurationException.class)
	public void testExceptionThrownOnMissingMainClassInConfigXml() throws Exception {
		File bpkFile = zip("config_no-main-class.xml");

		BpkConfiguration config = BpkResolver.resolve(bpkFile);

		JavaRuntime javaRuntime = (JavaRuntime) config.getRuntime();
		assertEquals("cz.cuni.mff.d3s.been.MakeTea", javaRuntime.getMainClass());
		assertEquals("task-test-3.0.0.jar", javaRuntime.getJarFile());
		assertEquals(7, javaRuntime.getBpkArtifacts().getArtifact().size());
	}

	@Test(expected=BpkConfigurationException.class)
	public void testExceptionThrownOnMissingJarFileInConfigXml() throws Exception {
		File bpkFile = zip("config_no-jar-file.xml");

		BpkConfiguration config = BpkResolver.resolve(bpkFile);

		JavaRuntime javaRuntime = (JavaRuntime) config.getRuntime();
		assertEquals("cz.cuni.mff.d3s.been.MakeTea", javaRuntime.getMainClass());
		assertEquals("task-test-3.0.0.jar", javaRuntime.getJarFile());
		assertEquals(7, javaRuntime.getBpkArtifacts().getArtifact().size());
	}

	public File zip(String resourceName) throws Exception {
		URL bpkUrl = getClass().getResource(resourceName);
		File bpkFile = new File(bpkUrl.toURI());
		File output = tmpFolder.newFile();

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));
		ZipEntry configXml = new ZipEntry(BpkNames.CONFIG_FILE);
		out.putNextEntry(configXml);
		byte[] data = Files.readAllBytes(bpkFile.toPath());
		out.write(data, 0, data.length);
		out.closeEntry();
		out.close();

		return output;

	}
}
