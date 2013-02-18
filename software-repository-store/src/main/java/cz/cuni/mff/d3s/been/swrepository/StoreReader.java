package cz.cuni.mff.d3s.been.swrepository;

import java.io.IOException;
import java.io.InputStream;

/**
 * A reader class that enables the user to get the content of a previously
 * selected entity in the form of an {@link InputStream}.
 * 
 * @author darklight
 * 
 */
public interface StoreReader {
	/**
	 * Get a r/o stream on this reader's targeted content
	 * 
	 * @return An open stream to the content; the user is responsible for
	 *         closing it
	 * 
	 * @throws IOException
	 *             When the stream can not be opened
	 */
	InputStream getContentStream() throws IOException;

	/**
	 * Get the length (in bytes) of the content targeted by this reader.
	 * 
	 * @return The length of targeted content, or <code>-1</code> if the
	 *         targeted content doesn't exist.
	 */
	long getContentLength();
}
