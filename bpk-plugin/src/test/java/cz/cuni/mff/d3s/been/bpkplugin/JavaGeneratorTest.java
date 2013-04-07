package cz.cuni.mff.d3s.been.bpkplugin;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cz.cuni.mff.d3s.been.bpk.JavaRuntime;

public class JavaGeneratorTest extends Assert {

	@Mock
	private Log log;

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	//test class
	private JavaGenerator generator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		generator = new JavaGenerator(log);
	}

	@Test
	public void testValidationIsSilentIfEverythingOk() throws Exception {
		Configuration cfg = createValidJavaTaskCfg();
		generator.validateRuntimeSpecific(cfg);
	}

	@Test(expected = ConfigurationException.class)
	public void testValidationThrowsExceptionWhenJarIsMissing() throws Exception {
		Configuration cfg = createValidJavaTaskCfg();
		cfg.packageJarFile = null;
		generator.validateRuntimeSpecific(cfg);
	}

	@Test(expected = ConfigurationException.class)
	public void testValidationThrowsExceptionWhenJarDoesNotExists() throws Exception {
		Configuration cfg = createValidJavaTaskCfg();
		cfg.packageJarFile = new File("this/file/does/not/exists");
		generator.validateRuntimeSpecific(cfg);
	}

	@Test(expected = ConfigurationException.class)
	public void testValidationThrowsExceptionWhenArtifactsAreMissing() throws Exception {
		Configuration cfg = createValidJavaTaskCfg();
		cfg.artifacts = null;
		generator.validateRuntimeSpecific(cfg);
	}

	@Test(expected = ConfigurationException.class)
	public void testValidationThrowsExceptionWhenBpkDependenciesAreMissing() throws Exception {
		Configuration cfg = createValidJavaTaskCfg();
		cfg.bpkDependencies = null;
		generator.validateRuntimeSpecific(cfg);
	}

	@Test(expected = ConfigurationException.class)
	public void testValidationThrowsExceptionWhenFilesToArchiveAreMissing() throws Exception {
		Configuration cfg = createValidJavaTaskCfg();
		cfg.bpkDependencies = null;
		generator.validateRuntimeSpecific(cfg);
	}

	@Test
	public void testCreatedRuntime() throws Exception {
		Configuration config = createValidJavaTaskCfg();
		JavaRuntime runtime = generator.createRuntime(config);
		assertEquals(1, runtime.getBpkArtifacts().getArtifact().size());
		assertEquals(config.packageJarFile.getName(), runtime.getJarFile());
	}

	private Configuration createValidJavaTaskCfg() throws Exception {
		Configuration cfg = new Configuration();
		cfg.packageJarFile = tmpFolder.newFile();
		Artifact artifact = mock(Artifact.class);
		when(artifact.getArtifactId()).thenReturn("artifact1artifactId");
		when(artifact.getGroupId()).thenReturn("artifact1groupId");
		when(artifact.getVersion()).thenReturn("artifact1version");
		cfg.artifacts = Arrays.asList(artifact);
		cfg.filesToArchive = new ArrayList<>();
		cfg.bpkDependencies = new ArrayList<>();
		cfg.bpkId = "bpkId";
		cfg.groupId = "groupId";
		cfg.version = "version";
		cfg.finalName = "finalName";
		return cfg;
	}

}
