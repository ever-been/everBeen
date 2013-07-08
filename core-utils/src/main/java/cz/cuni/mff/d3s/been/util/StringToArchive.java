package cz.cuni.mff.d3s.been.util;

import cz.cuni.mff.d3s.been.util.ItemToArchive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple POJO file which represents file in generated BPK archive.
 * 
 * @author Tadeas Palusga
 * 
 */
public class StringToArchive implements ItemToArchive {

	/**
	 * string which should be added to BPK archive as content of file with path
	 * 'pathInZip'
	 */
	private String string;

	/**
	 * path in BPK archive
	 */
	private String pathInZip;

	/**
	 * @param pathInZip
	 *          path in BPK archive
	 * @param string
	 *          string which should be added to BPK archive as content of file
	 *          with path 'pathInZip'
	 */
	public StringToArchive(String pathInZip, String string) {
		this.pathInZip = pathInZip;
		this.string = string;
	}

    @Override
    public boolean isDirectory() {
        return string == null || string.isEmpty();
    }

    @Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(string.getBytes());
	}

	@Override
	public String getPathInZip() {
		return pathInZip;
	}

    @Override
    public long getSize() {
        return string.getBytes().length;
    }
}
