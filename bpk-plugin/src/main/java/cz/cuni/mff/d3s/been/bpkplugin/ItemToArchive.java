package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Tadeas Palusga
 * 
 */
interface ItemToArchive {

	public InputStream getInputStream() throws IOException;

	public String getPathInZip();

}
