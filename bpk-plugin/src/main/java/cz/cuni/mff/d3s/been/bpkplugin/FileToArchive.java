package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;

public class FileToArchive {
	/**
	 * @parameter
	 * 
	 * @required
	 */
	private String pathInZip;

	/**
	 * @parameter
	 */
	private File file;

	public String getPathInZip() {
		return pathInZip;
	}

	public void setPathInZip(String pathInZip) {
		this.pathInZip = pathInZip;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
