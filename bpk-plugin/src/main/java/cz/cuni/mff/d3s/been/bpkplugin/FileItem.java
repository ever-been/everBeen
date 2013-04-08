package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugins.annotations.Parameter;

import cz.cuni.mff.d3s.been.bpk.PackageNames;

/**
 * Represents wildcard or file which will be added into generated BPK bundle
 * into "files" folder.
 * 
 * @author Tadeáš Palusga
 * 
 */
public final class FileItem {

	/**
	 * When specified, no wildcard will be used, but this file will be added
	 * directly into root of generated bpk archive.
	 */
	@Parameter
	private File file;

	/**
	 * Files will be searched in {@link #wildcardWorkingDirectory} by this
	 * wildcard.
	 */
	@Parameter
	private String wildcard;

	/**
	 * When {@link #wildcard} is specified, files will be searched in this
	 * directory.
	 */
	@Parameter
	private File wildcardWorkingDirectory;

	/**
	 * Create list of files, which will be added to archive. Depending on
	 * parameters, single file or files found by specified {@link #wildcard} in
	 * {@link #wildcardWorkingDirectory} will be added.
	 * 
	 * @return files which will be added to archive
	 */
	public List<FileToArchive> getFilesToArchive() {
		checkParameters();

		String folderName = PackageNames.FILES_DIR;
		List<File> files = collectFilesForAdding();

		List<FileToArchive> filesToArchive = new ArrayList<FileToArchive>();
		for (File f : files) {
			String nameInBpk = String.format("%s%s%s", folderName, File.separator, f.getName());
			//	log.info("    WILL BE ADDED: '" + f.getAbsolutePath() + "' -> '" + nameInBpk + "'");
			filesToArchive.add(new FileToArchive(nameInBpk, f));
		}
		return filesToArchive;
	}

	private List<File> collectFilesForAdding() {
		List<File> files = new ArrayList<File>();
		if (file != null) {
			files.add(file);
		} else {
			FileFilter filter = new WildcardFileFilter(wildcard);
			files.addAll(Arrays.asList(wildcardWorkingDirectory.listFiles(filter)));
		}
		return files;
	}

	/**
	 * Check parameters of item. Log error into maven log, if some parameter is
	 * incorrect. (When ERROR is logge into maven log, build failed)
	 * 
	 * @param log
	 */
	private void checkParameters() {
		/*
		 * it is possibble to define file or wildcard, not both
		 */
		if (file == null && wildcard == null) {
			//		log.error("You must specify file or wildcard.");
		} else if (file != null && wildcard != null) {
			//	log.error("You should specify file or wildcard, you can't specify both.");
		} else if (wildcard != null && wildcardWorkingDirectory == null) {
			//log.error("Parameter wildcardWorkingDirectory must be specified when wildcard is specified.");
		}
		/*
		 * Files must exists
		 */
		else if (file != null && !file.exists()) {
			//log.error("Specified file '" + file.getAbsolutePath() + "' not found.");
		} else if (wildcardWorkingDirectory != null && !wildcardWorkingDirectory.exists()) {
			//log.error("Specified wildcardworking directory '" + wildcardWorkingDirectory.getAbsolutePath() + "' not found.");
		}
	}

}