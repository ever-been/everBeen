package cz.cuni.mff.d3s.been.bpkplugin;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GeneratorMojoTest extends Assert {

	@Mock
	private Log log;

	// tested class
	private GeneratorMojo mojo;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mojo = spy(new GeneratorMojo());
		mojo.setLog(log);
	}

	@Test(expected = MojoFailureException.class)
	public void testExceptionIsThrownOnInvalidConfiguration() throws Exception {
		doThrow(ConfigurationException.class).when(mojo).generateBPK(log);
		mojo.execute();
	}

	@Test(expected = MojoFailureException.class)
	public void testExceptionIsThrownOnUnsuccessfullBpkGeneration() throws Exception {
		doThrow(GeneratorException.class).when(mojo).generateBPK(log);
		mojo.execute();
	}

	@Test
	public void testGetConfigurationReturnsCompleteConfiguration() throws Exception {
		mojo.artifacts = new ArrayList<>();
		mojo.bpkId = "bpkId";
		mojo.buildDirectory = new File("");
		mojo.filesToArchive = new ArrayList<>();
		mojo.finalName = "finalName";
		mojo.groupId = "groupId";
		mojo.packageJarFile = new File("");
		mojo.version = "version";
		mojo.bpkDependencies = new ArrayList<>();
        mojo.binary = new File("");
        mojo.mainClass = "mainClass";
        mojo.taskDescriptors = new File[] {};
        mojo.contextTaskDescriptors = new File[] {};

		Configuration cfg = mojo.getConfiguration();

		assertSame(mojo.artifacts, cfg.artifacts);
		assertSame(mojo.bpkId, cfg.bpkId);
		assertSame(mojo.buildDirectory, cfg.buildDirectory);
		assertSame(mojo.filesToArchive, cfg.filesToArchive);
		assertSame(mojo.finalName, cfg.finalName);
		assertSame(mojo.groupId, cfg.groupId);
		assertSame(mojo.packageJarFile, cfg.packageJarFile);
		assertSame(mojo.mainClass, cfg.mainClass);
		assertSame(mojo.binary, cfg.binary);
        assertSame(mojo.version, cfg.version);
        assertSame(mojo.bpkDependencies, cfg.bpkDependencies);
        assertSame(mojo.taskDescriptors, cfg.taskDescriptors);
        assertSame(mojo.contextTaskDescriptors, cfg.taskContextDescriptors);
	}

}
