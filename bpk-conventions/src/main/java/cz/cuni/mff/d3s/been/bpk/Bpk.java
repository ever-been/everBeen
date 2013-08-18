package cz.cuni.mff.d3s.been.bpk;

import java.io.File;
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

	/**
	 * Get a file handle to the package's content.
	 * 
	 * @return A temporary defensive copy of the package's content, within a
	 *         {@link java.io.File}
	 * 
	 * @deprecated Too much defensive copying involved in {@link java.io.File}
	 *             operations + abstractions go to hell
	 */
	@Deprecated
	File getFile();
}
