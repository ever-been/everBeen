package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple POJO file which represents file in generated BPK archive.
 * 
 * @author Tadeas Palusga
 * 
 */
class StringToArchive implements ItemToArchive {

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
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(string.getBytes());
	}

	@Override
	public String getPathInZip() {
		return pathInZip;
	}

}
