package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

/**
 * 
 * @author Tadeas Palusga
 * 
 */
@Mojo(name = "buildpackage", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyCollection = ResolutionScope.RUNTIME, threadSafe = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class GeneratorMojo extends AbstractMojo {

	/**
	 * Runtime type. See {@link RuntimeType}.
	 */
	@Parameter(alias = "runtime", required = true, defaultValue = "java")
	String runtime;

	// *************************************************************************
	// META-INF CONTENT
	// *************************************************************************
	/**
	 * GroupId of generated BPK package. Default value: <b>${project.groupId}</b>.
	 * (Will be used in generated metadata.xml)
	 */
	@Parameter(defaultValue = "${project.groupId}", required = true)
	String groupId;

	/**
	 * BpkId of generated BPK package. Default value:
	 * <b>${project.artifactId}</b>. (Will be used in generated metadata.xml)
	 */
	@Parameter(defaultValue = "${project.artifactId}", required = true)
	String bpkId;

	/**
	 * Version of generated BPK package. Default value: <b>${project.version}</b>.
	 * (Will be used in generated metadata.xml)
	 */
	@Parameter(defaultValue = "${project.version}", required = true)
	String version;

	// *************************************************************************
	// WHERE TO STORE BUILT BPK
	// *************************************************************************
	/**
	 * Directory to which the result BPK file will be generated. Default value:
	 * <b>${project.build.directory}</b>
	 */
	@Parameter(defaultValue = "${project.build.directory}", required = true)
	File buildDirectory;

	/**
	 * Final name of BPK file (without BPK extension ('.bpk')).
	 * <b>${project.artifactId}-${project.version}</b> by default.
	 */
	@Parameter(defaultValue = "${project.artifactId}-${project.version}", required = true)
	String finalName;

	/**
	 * Main class of the underlying main jar file.
	 */
	@Parameter
	String mainClass;

	/**
	 * Jar file with classes for this package.
	 */
	@Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}.jar")
	File packageJarFile;

	/**
	 * Binary (bash script, bat file, executable file etc..)
	 */
	@Parameter
	File binary;

	/**
	 * The set of dependencies for the web application being run.
	 */
	@Parameter(defaultValue = "${project.artifacts}", required = true, readonly = true)
	Collection<Artifact> artifacts;

	/**
	 * Files specified here will be added to bpk archive. All files will be placed
	 * in "files" folder in root folder of generated bpk package.
	 */
	@Parameter
	Collection<FileItem> filesToArchive;

	/**
	 * List of dependencies of this module on other (data) BPKs. You should not
	 * include code dependencies here, but use runtime instead.
	 */
	@Parameter
	Collection<BpkIdentifier> bpkDependencies;

	//----------------------------------------------------------------
	//----------------------------------------------------------------
	//----------------------------------------------------------------

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("[[ CREATING BEEN PACKAGE STARTED ]]");

		try {
			generateBPK(getLog());
		} catch (ConfigurationException e) {
			throw new MojoFailureException(String.format("Invalid configuration: ", e.getMessage()), e);
		} catch (GeneratorException e) {
			throw new MojoFailureException(String.format("Cannot generate BPK: ", e.getMessage()), e);
		}

		getLog().info("[[ CREATING BEEN PACKAGE FINISHED ]]");
	}

	void generateBPK(Log log) throws ConfigurationException, GeneratorException {
		Configuration configuration = getConfiguration();
		new GeneratorDriver(log).generate(runtime, configuration);
	}

	/**
	 * Returns Configuration wrapper object with values configured on this object
	 * 
	 * @return
	 * @throws MojoFailureException
	 */
	Configuration getConfiguration() throws ConfigurationException {
		Configuration configuration = new Configuration();

		configuration.groupId = groupId;
		configuration.bpkId = bpkId;
		configuration.version = version;
		configuration.buildDirectory = buildDirectory;
		configuration.finalName = finalName;
		configuration.packageJarFile = packageJarFile;

		try {
			configuration.mainClass = (mainClass == null
					? MainClassExtractor.getMainClass(packageJarFile.toPath()) : mainClass);
		} catch (IOException e) {
			throw new ConfigurationException(String.format("Invalid configuration: ", e.getMessage()), e);
		}
		configuration.binary = binary;
		configuration.artifacts = (artifacts == null
				? Collections.<Artifact> emptyList() : artifacts);
		configuration.filesToArchive = filesToArchive == null
				? Collections.<FileItem> emptyList() : filesToArchive;
				configuration.bpkDependencies = bpkDependencies == null
						? Collections.<BpkIdentifier> emptyList() : bpkDependencies;

						return configuration;
	}

}
