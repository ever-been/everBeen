package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * @author donarus
 * 
 *         Creates been package
 * @goal buildpackage
 * @phase package
 * 
 */
public class CreateBeenPackageMojo extends AbstractMojo {

	private Log log = getLog();

	/**
	 * Files which should be added to bpk archive
	 * 
	 * @parameter
	 * 
	 * @required
	 */
	private List<FileToArchive> filesToArchive;

	/**
	 * @parameter expression="${project.build.directory}"
	 * 
	 * @required
	 */

	private File buildDirectory;

	/**
	 * Final bpk name (without BPK)
	 * 
	 * @parameter
	 * 
	 * @required
	 */
	private String finalName;

	public void execute() throws MojoExecutionException {
		logStart();

		File bpkFile = new File(buildDirectory, finalName + ".bpk");
		ZipUtil zipUtil = new ZipUtil();
		
		try {
			zipUtil.createZip(filesToArchive, bpkFile);
			log.info("BPK exported to '" + bpkFile.getAbsolutePath() +"'");
		} catch (IOException e) {
			log.error("Cannot create BPK archive", e);
		}

		logEnd();
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
