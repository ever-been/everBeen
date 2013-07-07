package cz.cuni.mff.d3s.been.util;

import cz.cuni.mff.d3s.been.util.ItemToArchive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple POJO file which represents file in generated BPK archive.
 * 
 * @author Tadeas Palusga
 * 
 */
public final class FileToArchive implements ItemToArchive {

	/**
	 * file on disk which should be added to BPK archive
	 */
	private File file;

	/**
	 * path in BPK archive
	 */
	private String pathInZip;

	/**
	 * @param pathInZip
	 *          path in BPK archive
	 * @param file
	 *          file on disk which should be added to BPK archive
	 */
	public FileToArchive(String pathInZip, File file) {
		this.pathInZip = pathInZip;
		this.file = file;
	}

    @Override
    public boolean isDirectory() {
        return !file.exists() || file.isDirectory();
    }

    @Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public String getPathInZip() {
		return pathInZip;
	}

    @Override
    public long getSize() {
        return file.length();
    }
}
