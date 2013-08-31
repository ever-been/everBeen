package cz.cuni.mff.d3s.been.bpk;

import java.io.IOException;
import java.io.InputStream;

/**
 * A BEEN software package.
 * 
 * @author darklight
 */
public interface Bpk {
	/**
	 * Get a unique identifier for the package.
	 * 
	 * @return The identifier
	 */
	BpkIdentifier getBpkIdentifier();

	/**
	 * Open a stream to the package's content. The user is responsible for closing
	 * it.
	 * 
	 * @return the stream representing the package content
	 * @throws IOException
	 *           when an I/O error occurs
	 */
	InputStream getInputStream() throws IOException;
}
