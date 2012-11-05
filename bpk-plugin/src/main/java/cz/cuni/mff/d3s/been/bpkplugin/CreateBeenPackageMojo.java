package cz.cuni.mff.d3s.been.bpkplugin;

import static cz.cuni.mff.d3s.been.bpk.PackageNames.CONFIG_FILE;
import static cz.cuni.mff.d3s.been.bpk.PackageNames.FILES_DIR;
import static cz.cuni.mff.d3s.been.bpk.PackageNames.FILE_SUFFIX;
import static cz.cuni.mff.d3s.been.bpk.PackageNames.METADATA_FILE;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

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
	 * Jar file with classes for this package. (Will be used in generated
	 * config.xml)
	 * 
	 * @parameter
	 * 
	 * @required
	 */
	File packageJarFile;

	/**
	 * Fully qualified class name in jar file for this bpk package. (Will be used
	 * in generated config.xml)
	 * 
	 * @parameter
	 * 
	 * @required
	 */
	String mainClassName;

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
	 * Version of bpk package. <b>${project.version}</b> by default. (Will be used
	 * in generated metadata.xml)
	 * 
	 * @parameter expression="${project.version}"
	 * 
	 * @required
	 */
	String version;

	/**
	 * Type of bpk package. <b>task</b> by default. (Will be used in generated
	 * metadata.xml)
	 * 
	 * @parameter default-value="task"
	 * 
	 * @required
	 */
	String type;

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
	 * This is the plugin main method. All generation logic starts here.
	 */
	@Override
	public void execute() throws MojoExecutionException {
		logStart();

		File bpkFile = createEmptyBpkFile(); // output file

		try {
			List<FileToArchive> files = new ArrayList<FileToArchive>();
			// generate
			files.add(createFileToArchiveFromPackageJar());
			files.add(generateConfigXmlFile());
			files.add(generateMetadataXmlFile());

			generateModuleModuleConfigXmlFile(files);

			for (FileItem fitem : filesToArchive) {
				files.addAll(fitem.getFilesToArchive(log));
			}
			new ZipUtil().createZip(files, bpkFile);
			log.info("BPK exported to '" + bpkFile.getAbsolutePath() + "'");
		} catch (IOException e) {
			log.error("Cannot create BPK archive", e);
		}

		logEnd();
	}

	FileToArchive generateConfigXmlFile() {
		String nameInBpk = CONFIG_FILE;

		if (type.equals("task")) {
			return generateTaskConfigXmlFile(nameInBpk);
		} else if (type.equals("module")) {
			return generateModuleConfigXmlFile(nameInBpk);
		} else {
			log.error("Cannot create config.xml for unknown type '" + type + "'");
			return null;
		}
	}

	FileToArchive generateTaskConfigXmlFile(String nameInBpk) {

		log.info("    TASK WILL BE GENERATED: with:mainClass='" + mainClassName + "' -> '" + nameInBpk + "'");
		try {
			File config = File.createTempFile("tmp_generated_config", ".xml");
			String content = String.format("<?xml version=\"1.0\"?>\n" + "<packageConfiguration>\n" + "	<java classPath=\".:%s\" mainClass=\"%s\" />\n" + "</packageConfiguration>\n", packageJarFile.getName(), mainClassName);
			FileUtils.write(config, content);
			return new FileToArchive(nameInBpk, config);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	FileToArchive generateModuleConfigXmlFile(String nameInBpk) {
		// TODO: Ugly hack, figure out better way to set been.directory.jaxb
		// Probably will not work on Windows
		// FIXME: It's nice to recognize this is an ugly hack, but it'd be nicer to explain what is it trying to acomplish
		System.setProperty("been.directory.jaxb", "service_interfaces/src/main/xsd/");

		log.info("    MODULE WILL BE GENERATED: with:mainClass='" + mainClassName + "' -> '" + nameInBpk + "'");
		try {
			File config = File.createTempFile("tmp_generated_config", ".xml");

			if (module == null) {
				log.error("CANNOT CREATE MODULE config.xml, module section not specified");
			}

			PrintStream ps = new PrintStream(config);
			ps.println(indent(0, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));
			ps.println(indent(0, "<pluggableModuleConfiguration xmlns=\"%s\">", "http://been.mff.cuni.cz/pluggablemodule/config"));
			ps.println(indent(1, "<java mainClass=\"%s\">", this.mainClassName));
			ps.println(indent(2, "<classpathItems>"));
			ps.println(indent(3, "<classpathItem>%s</classpathItem>", packageJarFile.getName()));
			ps.println(indent(2, "</classpathItems>"));
			ps.println(indent(1, "</java>"));
			ps.println(indent(1, "<dependencies>"));
			if (this.module.dependencies != null) {
				for (BpkModuleDependency dependency : this.module.dependencies) {
					ps.println(indent(2, "<dependency moduleName=\"%s\" moduleVersion=\"%s\"/>", dependency.name, dependency.version));
				}
			}
			ps.println(indent(1, "</dependencies>", XML_INDENT_SEQUENCE));
			ps.println(indent(0, "</pluggableModuleConfiguration>"));
			ps.close();

			return new FileToArchive(nameInBpk, config);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	void generateModuleModuleConfigXmlFile(Collection<FileToArchive> collection) {
		String nameInBpk = "module-config.xml";
		if (type.equals("module") && module.config != null) {
			collection.add(new FileToArchive(nameInBpk, new File(module.config)));
		} else {
			log.info("MODULE DOES NOT INCLUDE module-config.xml");

		}
	}

	String indent(int indentLevel, String format, Object... args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indentLevel; ++i) {
			sb.append(XML_INDENT_SEQUENCE);
		}
		sb.append(format);
		return String.format(sb.toString(), args);
	}
	FileToArchive generateMetadataXmlFile() {
		String nameInBpk = METADATA_FILE;

		if (type.equals("task")) {
			return generateTaskMetadataXmlFile(nameInBpk);
		} else if (type.equals("module")) {
			return generateModuleMetadataXmlFile(nameInBpk);
		} else {
			log.error("Cannot create config.xml for unknown type '" + type + "'");
			return null;
		}
	}

	FileToArchive generateTaskMetadataXmlFile(String nameInBpk) {
		log.info("    TASK WILL BE GENERATED: with:name='" + name + "', version='" + version + "', type='" + type + "', humanName='" + humanName + "' -> '" + nameInBpk + "'");
		try {
			File config = File.createTempFile("tmp_generated_config", ".xml");
			String content = String.format("<?xml version=\"1.0\"?>\n" + "<package>\n" + "  <name>%s</name>\n" + "  <version>%s</version>\n" + "  <type>%s</type>\n" + "  <humanName>%s</humanName>\n" + "</package>\n", name, version, type, humanName);
			FileUtils.write(config, content);
			return new FileToArchive(nameInBpk, config);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	FileToArchive generateModuleMetadataXmlFile(String nameInBpk) {
		log.info("    MODULE WILL BE GENERATED: with:name='" + name + "', version='" + version + "', type='" + type + "', humanName='" + humanName + "' -> '" + nameInBpk + "'");
		try {
			File config = File.createTempFile("tmp_generated_config", ".xml");
			StringBuilder builder = new StringBuilder();

			if (module != null && !module.interfaces.isEmpty()) {
				builder.append("\n\t<providedInterfaces>\n");
				for (String iface : module.interfaces) {
					log.info("    PROVIDES INTERFACE " + iface);
					builder.append("\t\t<providedInterface>");
					builder.append(iface);
					builder.append("</providedInterface>");
				}
				builder.append("\n\t</providedInterfaces>\n");
			} else {
				log.error("MODULE MUST EXPORT AN INTERFACE!");
			}

			String content = String.format("<?xml version=\"1.0\"?>\n" + "<package>\n" + "  <name>%s</name>\n" + "  <version>%s</version>\n" + "  <type>%s</type>\n" + "  <humanName>%s</humanName>\n" + "%s" + "</package>\n", name, version, type, humanName, builder.toString());

			FileUtils.write(config, content);
			return new FileToArchive(nameInBpk, config);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
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

}
