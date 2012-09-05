package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;

/**
 * Simple POJO file which represents file in generated bpk archive.
 * @author donarus
 *
 */
final class FileToArchive {

	//file on disk which should be added to bpk archive
	private File file;

	//path in bpk archive
	private String pathInZip;

	/**
	 * @param pathInZip path in bpk archive
	 * @param file file on disk which should be added to bpk archive
	 */
	public FileToArchive(String pathInZip, File file) {
		this.pathInZip = pathInZip;
		// TODO Auto-generated constructor stub
		this.file = file;
	}

	public String getPathInZip() {
		return pathInZip;
	}

	public File getFile() {
		return file;
	}

}
