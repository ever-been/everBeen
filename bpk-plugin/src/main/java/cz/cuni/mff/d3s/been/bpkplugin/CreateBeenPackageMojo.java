package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/*
 * Mojo plugin development is comment-annotation driven. 
 * For simple development documentation see http://maven.apache.org/guides/plugin/guide-java-plugin-development.html
 */

/**
 * This plugin should be used for creating BPK Ebeen packages. BPK files are
 * classical ZIP files with simple structure. Each file, which should be
 * included in BPK package must be strictly specified in plugin parameters. It
 * is necessary to include relative path to the included file (files) and
 * relative path in the result *.bpk file. <br/>
 * <br/>
 * Example of usage:
 * 
 * <pre>
 * &lt;build&gt;
 *   &lt;plugins&gt;
 * ...
 *     &lt;plugin&gt;
 *       &lt;groupId&gt;cz.cuni.mff.d3s.been&lt;/groupId&gt;
 *       &lt;artifactId&gt;bpk-plugin&lt;/artifactId&gt;
 *       &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
 *       &lt;executions&gt;
 *         &lt;execution&gt;
 *           &lt;goals&gt;
 *             &lt;goal&gt;buildpackage&lt;/goal&gt;
 *           &lt;/goals&gt;
 *         &lt;/execution&gt;
 *       &lt;/executions&gt;
 *       &lt;configuration&gt;
 *         &lt;finalName&gt;${project.artifactId}-${project.version}&lt;/finalName&gt;
 *         &lt;filesToArchive&gt;
 * ...
 *           &lt;fileItem&gt;
 *             &lt;folder&gt;files&lt;/folder&gt;
 *             &lt;file&gt;${project.build.directory}/${project.build.finalName}.jar&lt;/file&gt;
 *           &lt;/fileItem&gt;
 *           &lt;fileItem&gt;
 *             &lt;wildcardWorkingDirectory&gt;${basedir}/src/main/resources/&lt;/wildcardWorkingDirectory&gt;
 *             &lt;wildcard&gt;*.xml&lt;/wildcard&gt;
 *           &lt;/fileItem&gt;
 *           &lt;fileItem&gt;
 *             &lt;folder&gt;files&lt;/folder&gt;
 *             &lt;wildcardWorkingDirectory&gt;${jaxb-xsd-dir}&lt;/wildcardWorkingDirectory&gt;
 *             &lt;wildcard&gt;*.xsd&lt;/wildcard&gt;
 *           &lt;/fileItem&gt;
 *         &lt;/filesToArchive&gt;
 * ...
 *       &lt;/configuration&gt;
 *     &lt;/plugin&gt;
 * ...
 *   &lt;/plugins&gt;
 * &lt;/build&gt;
 * </pre>
 * 
 * @author donarus
 * 
 * @goal buildpackage
 * @phase package
 */
public class CreateBeenPackageMojo extends AbstractMojo {

	// log used in Maven output
	private final Log log = getLog();

	/**
	 * Files specified here will be added to bpk archive.
	 * 
	 * @parameter
	 * 
	 * @required
	 */
	private List<FileItem> filesToArchive;

	/**
	 * Bpk file will be generated into this directory. <b>project_loc/target<b/>
	 * by default.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * 
	 * @required
	 */
	private File buildDirectory;

	/**
	 * Final name of bpk file (without bpk extension)
	 * 
	 * @parameter
	 * 
	 * @required
	 */
	private String finalName;

	/**
	 * This is the plugin main method. All generation logic starts here.
	 */
	public void execute() throws MojoExecutionException {
		logStart();

		File bpkFile = createEmptyBpkFile(); // output file

		try {
			List<FileToArchive> files = new ArrayList<FileToArchive>();
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

	private File createEmptyBpkFile() {
		return new File(buildDirectory, finalName + ".bpk");
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
