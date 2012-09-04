package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;

/**
 * File specified here will be placed into generated bpk package.
 * 
 * @author donarus
 * 
 */
public class FileToArchive {

	/**
	 * Relative or absolute path to file, which will be placed into generated zip
	 * file.
	 * 
	 * @parameter
	 * 
	 * @required
	 */
	private File file;

	/**
	 * Relative path, where input file will be placed in generated bpk (zip) file.
	 * 
	 * @parameter
	 * 
	 * @required
	 */
	private String pathInZip;

	/* FIXME - getter/setter for each parameter field must be here??? 
	 * I tried it without it and it doesn't work. It is possible, 
	 * that some annotations are wrong or missing on parameter fields. */
	
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
