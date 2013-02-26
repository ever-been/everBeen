package cz.cuni.mff.d3s.been.bpkplugin;

import static cz.cuni.mff.d3s.been.bpk.PackageNames.CONFIG_FILE;
import static cz.cuni.mff.d3s.been.bpk.PackageNames.FILES_DIR;
import static cz.cuni.mff.d3s.been.bpk.PackageNames.FILE_SUFFIX;
import static cz.cuni.mff.d3s.been.bpk.PackageNames.METADATA_FILE;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.inject.name.Names;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import cz.cuni.mff.d3s.been.bpk.PackageNames;

import cz.cuni.mff.d3s.been.bpk.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.*;

/*
 * Mojo plugin development is comment-annotation driven. 
 * For simple development documentation see http://maven.apache.org/guides/plugin/guide-java-plugin-development.html
 */

/**
 * This plugin should be used for creating BPK Ebeen packages. BPK files are
 * classical ZIP files with simple structure. Files which should be included in
 * BPK package must specified as plugin parameters. Files "config.xml" and
 * "metadata.xml" will be generated automatically. <br/>
 * <br/>
 * Example of plugin definition in pom.xml:
 * 
 * <pre>
 * &lt;plugin&gt;
 *   &lt;groupId&gt;cz.cuni.mff.d3s.been&lt;/groupId&gt;
 *   &lt;artifactId&gt;bpk-plugin&lt;/artifactId&gt;
 *   &lt;version&gt;1.0.1&lt;/version&gt;
 *   &lt;executions&gt;
 *     &lt;execution&gt;
 *       &lt;goals&gt;
 *         &lt;goal&gt;buildpackage&lt;/goal&gt;
 *       &lt;/goals&gt;
 *     &lt;/execution&gt;
 *   &lt;/executions&gt;
 *   &lt;configuration&gt;
 *     &lt;humanName&gt;Host Manager&lt;/humanName&gt;
 *     &lt;packageJarFile&gt;${project.build.directory}/${project.build.finalName}.one-jar.jar&lt;/packageJarFile&gt;
 *     &lt;mainClassName&gt;cz.cuni.mff.been.hostmanager.HostManagerService&lt;/mainClassName&gt;
 *     &lt;filesToArchive&gt;
 *       &lt;fileItem&gt;
 *         &lt;wildcardWorkingDirectory&gt;${basedir}/src/main/resources/files/&lt;/wildcardWorkingDirectory&gt;
 *         &lt;wildcard&gt;*&lt;/wildcard&gt;
 *       &lt;/fileItem&gt;
 *     &lt;/filesToArchive&gt;
 *   &lt;/configuration&gt;
 * &lt;/plugin&gt;
 * </pre>
 * 
 * @author donarus
 * 
 * @goal buildpackage
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class CreateBeenPackageMojo extends AbstractMojo {

	/** Character sequence used for output XML indentation. */
	private static final String XML_INDENT_SEQUENCE = "	";

	// log used in Maven output
	private final Log log = getLog();

	/**
	 * Files specified here will be added to bpk archive. All files will be placed
	 * in "files" folder in root folder of generated bpk package.
	 * 
	 * @parameter
	 * 
	 * @required
	 */
	List<FileItem> filesToArchive;

	/**
	 * Bpk file will be generated into this directory.
	 * <b>${project.build.directory}</b> by default.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * 
	 * @required
	 */
	File buildDirectory;

	/**
	 * Final name of bpk file (without bpk extension).
	 * <b>${project.artifactId}-${project.version}</b> by default.
	 * 
	 * @parameter expression="${project.artifactId}-${project.version}"
	 * 
	 * @required
	 */
	String finalName;


	/**
	 * Name of the bpk package. <b>${project.build.finalName}</b> by default.
	 * (Will be used in generated metadata.xml)
	 * 
	 * @parameter expression="${project.build.finalName}"
	 * 
	 * @required
	 */
	String name;



	/**
	 * Type of bpk package. <b>task</b> by default. (Will be used in generated
	 * metadata.xml)
	 * 
	 * @parameter default-value="task"
	 * 
	 * @required
	 */
	//String type;

	/**
	 * Human readable name of this bpk package. (Will be used in generated
	 * metadata.xml)
	 * 
	 * @parameter
	 * 
	 * @required
	 */
	String humanName;

	/**
	 * 
	 * @parameter
	 * 
	 */
	BpkModuleConfig module;

	/**
	 * @parameter default-value="${project.artifacts}"
	 * @required
	 * @readonly
	 */
	Collection<Artifact> artifacts;

	// *************************************************************************
	// RUNTIME
	// *************************************************************************
	/**
	 *
	 *
	 * @parameter default-value="java"
	 *
	 * @required
	 */
	String runtime;

	/**
	 * Jar file with classes for this package. (Will be used in generated
	 * config.xml)
	 *
	 * @parameter expression="${project.build.directory}/${project.build.finalName}.jar"
	 *
	 */
	File packageJarFile;


	// *************************************************************************
	// META-INF
	// *************************************************************************
	/**
	 * Version of bpk package. <b>${project.version}</b> by default. (Will be used
	 * in generated metadata.xml)
	 *
	 * @parameter expression="${project.groupId}"
	 *
	 * @required
	 */
	String groupId;

	/**
	 * Version of bpk package. <b>${project.artifactId}</b> by default. (Will be used
	 * in generated metadata.xml)
	 *
	 * @parameter expression="${project.artifactId}"
	 *
	 * @required
	 */
	String bpkId;

	/**
	 * Version of bpk package. <b>${project.version}</b> by default. (Will be used
	 * in generated metadata.xml)
	 *
	 * @parameter expression="${project.version}"
	 *
	 * @required
	 */
	String version;

	// *************************************************************************
	// BPK DEPENDENCIES
	// *************************************************************************

	/**
	 * List of dependencies of this module on other (data) BPKs. You should not
	 * include code dependencies here, but use runtime instead.
	 *
	 * @parameter
	 */
	List<BpkIdentifier> bpkDependencies;

	// *************************************************************************

	/**
	 * This is the plugin main method. All generation logic starts here.
	 */
	@Override
	public void execute() throws MojoExecutionException {
		logStart();

		File bpkFile = createEmptyBpkFile(); // output file

		try {
			ObjectFactory bpkFactory = new ObjectFactory();
			BpkConfiguration bpkConfiguration = bpkFactory.createBpkConfiguration();

			// META-INF
			bpkConfiguration.setMetaInf(createMetaInf(bpkFactory));

			// RUNTIME
			RuntimeType runtimeType = RuntimeType.valueOf(runtime.toUpperCase());
			BpkRuntime bpkRuntime = null;
			switch (runtimeType) {
				case JAVA:
					bpkRuntime = createJavaRuntime(bpkFactory);
					break;
				default:
					throw new UnsupportedOperationException("Don't know how to create '" + runtime + ", runtime");

			}


//			BpkDependencies dependencies = bpkRuntime.getBpkDependencies();
//			// DEPENDENCIES
//			if (bpkDependencies != null) {
//				for(BpkIdentifier identifier: bpkDependencies) {
//					//dependencies.getDependency().add(identifier);
//				}
//			}

			bpkConfiguration.setRuntime(bpkRuntime);


			List<FileToArchive> files = new ArrayList<FileToArchive>();

			String configXml = null;
			// add config
			try {

				configXml = BpkConfigUtils.toXml(bpkConfiguration);
				FileToArchive configFileToArchive = createFileToArchive(PackageNames.CONFIG_FILE, configXml);

				files.add(configFileToArchive);
			} catch (Exception e) {
				log.error(e);
			}



			// generate
			files.add(createFileToArchiveFromPackageJar());
			files.addAll(getLibsToArchive());



			for (FileItem fitem : filesToArchive) {
				files.addAll(fitem.getFilesToArchive(log));
			}

			new ZipUtil().createZip(files, bpkFile);
			log.info(configXml);
			log.info("BPK exported to '" + bpkFile.getAbsolutePath() + "'");
		} catch (IOException e) {
			log.error("Cannot create BPK archive", e);
		}

		logEnd();
	}

	private MetaInf createMetaInf(ObjectFactory bpkFactory) {
		MetaInf metaInf = bpkFactory.createMetaInf();
		metaInf.setGroupId(groupId);
		metaInf.setBpkId(bpkId);
		metaInf.setVersion(version);
		return metaInf;
	}

	private BpkRuntime createJavaRuntime(ObjectFactory bpkFactory) {
		if (packageJarFile == null) {
			throw new IllegalStateException("Java's jar file must be specified: missing packageJarFile");
		}
		JavaRuntime javaRuntime = bpkFactory.createJavaRuntime();
		javaRuntime.setJarFile(packageJarFile.getName());

		BpkArtifacts bpkArtifacts = bpkFactory.createBpkArtifacts();

		for (Artifact artifact : artifacts) {
			BpkArtifact bpkArtifact = bpkFactory.createBpkArtifact();

			bpkArtifact.setGroupId(artifact.getGroupId());
			bpkArtifact.setArtifactId(artifact.getArtifactId());
			bpkArtifact.setVersion(artifact.getVersion());

			bpkArtifacts.getArtifact().add(bpkArtifact);
		}

		javaRuntime.setBpkArtifacts(bpkArtifacts);

		return javaRuntime;
	}

	/**
	 * Get maven dependency artifacts.
	 * 
	 * @return A list of {@link FileToArchive} for the project's maven
	 *         dependencies.
	 */
	List<FileToArchive> getLibsToArchive() {
		List<FileToArchive> libs = new ArrayList<FileToArchive>();
		for (Artifact artifact : artifacts) {
			File artifactFile = artifact.getFile();
			File destinationFile = new File(PackageNames.LIB_DIR, artifactFile.getName());
			libs.add(new FileToArchive(destinationFile.getPath(), artifactFile));
		}
		return libs;
	}



	FileToArchive createFileToArchiveFromPackageJar() {
		String nameInBpk = FILES_DIR + "/" + packageJarFile.getName();
		log.info("    WILL BE ADDED: '" + packageJarFile.getAbsolutePath() + "' -> '" + nameInBpk + "'");
		return new FileToArchive(nameInBpk, packageJarFile);
	}

	File createEmptyBpkFile() {
		return new File(buildDirectory, finalName + FILE_SUFFIX);
	}

	private void logStart() {
		log.info("=====================================");
		log.info("==  CREATING BEEN PACKAGE STARTED  ==");
		log.info("=====================================");
	}

	private void logEnd() {
		log.info("===================================");
		log.info("==  CREATING BEEN PACKAGE ENDED  ==");
		log.info("===================================");
	}



	private FileToArchive createFileToArchive(String nameInBpk, String content) throws IOException {
		File tmpFile = File.createTempFile("tmp_generated_config", ".xml");

		FileUtils.write(tmpFile, content);
		return new FileToArchive(nameInBpk, tmpFile);
	}

}
